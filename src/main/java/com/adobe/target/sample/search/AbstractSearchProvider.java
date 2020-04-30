/*
 * Copyright 2019 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.adobe.target.sample.search;

import com.adobe.target.edge.client.http.JacksonObjectMapper;
import kong.unirest.*;

public abstract class AbstractSearchProvider implements SearchProvider {

    protected UnirestInstance unirestInstance = Unirest.spawnInstance();

    public AbstractSearchProvider() {
        ObjectMapper serializer = new JacksonObjectMapper();
        unirestInstance.config()
                .socketTimeout(10000)
                .connectTimeout(10000)
                .concurrency(4, 2)
                .automaticRetries(true)
                .enableCookieManagement(false)
                .setObjectMapper(serializer)
                .setDefaultHeader("Accept", "application/json");
    }

    @Override
    public void close() {
        unirestInstance.shutDown();
    }

}
