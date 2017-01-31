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
package org.wso2.carbon.identity.common.internal.cache;

/**
 * Cache Configuration.
 */
public class CacheConfig {

    private CacheConfigKey cacheConfigKey;
    private boolean isEnabled;
    private int timeout;
    private int capacity;
    private boolean isDistributed = true;

    public CacheConfig(CacheConfigKey cacheConfigKey) {
        this.cacheConfigKey = cacheConfigKey;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public CacheConfigKey getCacheConfigKey() {
        return cacheConfigKey;
    }

    public boolean isDistributed() {
        return isDistributed;
    }

    public void setDistributed(boolean isDistributed) {
        this.isDistributed = isDistributed;
    }
}
