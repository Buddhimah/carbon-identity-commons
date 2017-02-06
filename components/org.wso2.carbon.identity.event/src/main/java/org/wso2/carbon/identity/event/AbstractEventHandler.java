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

package org.wso2.carbon.identity.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.identity.common.base.exception.IdentityRuntimeException;
import org.wso2.carbon.identity.common.base.handler.AbstractMessageHandler;
import org.wso2.carbon.identity.common.base.handler.InitConfig;
import org.wso2.carbon.identity.common.base.message.MessageContext;
import org.wso2.carbon.identity.event.internal.ConfigParser;
import org.wso2.carbon.identity.event.model.Event;
import org.wso2.carbon.identity.event.model.ModuleConfig;
import org.wso2.carbon.identity.event.model.Subscription;

import java.util.List;
import java.util.Map;

/**
 * Abstract Event Handler class.
 */
public abstract class AbstractEventHandler extends AbstractMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractEventHandler.class);
    protected ModuleConfig moduleConfig;

    public boolean canHandle(MessageContext messageContext) throws IdentityRuntimeException {

        Event event = ((EventMessageContext) messageContext).getEvent();
        String eventName = event.getEventName();
        String moduleName = this.getName();
        ConfigParser notificationMgtConfigBuilder;
        try {
            notificationMgtConfigBuilder = ConfigParser.getInstance();
        } catch (EventException e) {
            logger.error("Error while retrieving event mgt config builder", e);
            return false;
        }
        List<Subscription> subscriptionList = null;
        ModuleConfig moduleConfig = notificationMgtConfigBuilder.getModuleConfigurations(moduleName);
        if (moduleConfig != null) {
            subscriptionList = moduleConfig.getSubscriptions();
        }
        if (subscriptionList != null) {
            for (Subscription subscription : subscriptionList) {
                if (subscription.getSubscriptionName().equals(eventName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isAssociationAsync(String eventName) throws EventException {

        Map<String, ModuleConfig> moduleConfigurationList = ConfigParser.getInstance()
                .getModuleConfiguration();
        ModuleConfig moduleConfig = moduleConfigurationList.get(this.getName());
        List<Subscription> subscriptions = moduleConfig.getSubscriptions();

        for (Subscription sub : subscriptions) {
            if (sub.getSubscriptionName().equals(eventName)) {
                continue;
            }
            return Boolean.parseBoolean(sub.getSubscriptionProperties().getProperty(this.getName() + ".subscription."
                    + eventName + "" + ".operationAsync"));
        }
        return false;
    }

    public abstract void handleEvent(EventMessageContext eventMessageContext) throws EventException;

    /**
     * Rollback opreation of the handler.
     *
     * @param messageContext The runtime message context.
     */
    public abstract void rollBack(MessageContext messageContext);

    @Override
    public void init(InitConfig configuration) throws IdentityRuntimeException {

        if (configuration instanceof  ModuleConfig) {
            this.moduleConfig = (ModuleConfig) configuration;
        }
    }

}
