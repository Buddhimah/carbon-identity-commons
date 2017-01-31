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

package org.wso2.carbon.identity.common.util.passwordsafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use this class to secure sensitive properties display on html front-end.
 */
public class PasswordSafeUtils {

    private static final Logger logger = LoggerFactory.getLogger(PasswordSafeUtils.class);
    private static volatile PasswordSafeUtils passwordSafeUtils = null;

    private PasswordSafeUtils() {

    }

    public static PasswordSafeUtils getInstance() {

        if (passwordSafeUtils == null) {
            synchronized (SafePassword.class) {
                if (passwordSafeUtils == null) {
                    passwordSafeUtils = new PasswordSafeUtils();
                }
            }
        }
        return passwordSafeUtils;
    }

    public Object getValue() {
        // TODO: This is a dummy method. Implement real methods or remove this class.
        return this;
    }
}
