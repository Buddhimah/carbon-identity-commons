/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.identity.event.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.identity.event.AbstractEventHandler;
import org.wso2.carbon.identity.event.EventException;
import org.wso2.carbon.identity.event.EventMessageContext;
import org.wso2.carbon.identity.event.model.Event;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * This has a queue inside. All publishers add events to this queue and this event distribution task is responsible
 * for distributing these events to Notification sending modules
 */
public class EventDistributionTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(EventDistributionTask.class);

    /**
     * Queue used to add events by publishers.
     */
    private BlockingDeque<EventMessageContext> eventQueue;
    /**
     * Registered message sending modules.
     */
    private List<AbstractEventHandler> notificationSendingModules;
    /**
     * Condition to break event distribution task.
     */
    private volatile boolean running;

    /**
     * Overridden constructor to initiate notification sending modules and thread pool size.
     *
     * @param notificationSendingModules List of notification sending modules registered
     * @param threadPoolSize             Size of thread pool for notification sending components
     */
    public EventDistributionTask(List<AbstractEventHandler> notificationSendingModules, int threadPoolSize) {
        this.notificationSendingModules = notificationSendingModules;
        this.eventQueue = new LinkedBlockingDeque<>();
        EventDataHolder.getInstance().setThreadPool(Executors.newFixedThreadPool(threadPoolSize));
    }

    public void addEventToQueue(EventMessageContext publisherEvent) {
        this.eventQueue.add(publisherEvent);
    }

    @Override
    public void run() {
        running = true;
        // Run forever until stop the bundle. Will stop in eventQueue.take()
        while (running) {
            try {
                final EventMessageContext eventMessageContext = eventQueue.take();
                Event event = eventMessageContext.getEvent();
                for (final AbstractEventHandler module : notificationSendingModules) {
                    // If the module is subscribed to the event, module will be executed.
                    if (module.isEnabled(eventMessageContext)) {
                        // Create a runnable and submit to the thread pool for sending message.
                        Runnable msgSender = () -> {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Executing " + module.getName() + " on event" + event.
                                        getEventName());
                            }
                            try {
                                module.handleEvent(eventMessageContext);
                            } catch (EventException e) {
                                logger.error("Error while invoking notification sending module " + module.
                                        getName(), e);
                            }
                        };
                        Future future = EventDataHolder.getInstance().getThreadPool().submit(msgSender);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Task is done: " + future.isDone());
                        }
                    }
                }
            } catch (InterruptedException e) {
                logger.error("Error while picking up event from event queue", e);
            }
        }
    }

    public void shutdown() {
        this.running = false;
    }
}
