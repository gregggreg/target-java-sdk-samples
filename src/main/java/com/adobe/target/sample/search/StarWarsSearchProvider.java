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

import kong.unirest.GenericType;
import kong.unirest.HttpResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StarWarsSearchProvider extends AbstractSearchProvider {

    private static final String SEARCH_URL = "https://swapi.dev/api/people/";

    private static class StarWarsSearchProviderResult {
        private List<SearchResult> results;

        public List<SearchResult> getResults() { return results; }
    }

    public List<SearchResult> getResults(String term) {
        HttpResponse<StarWarsSearchProviderResult> result = unirestInstance.get(getSearchUrl())
                .queryString(getSearchParams(term))
                .asObject(new GenericType<StarWarsSearchProviderResult>() {
                });
        return result.getBody().getResults();
    }

    protected String getSearchUrl() {
        return SEARCH_URL;
    }

    protected Map<String, Object> getSearchParams(String term) {
        Map<String, Object> params = new HashMap<>();
        params.put("format", "json");
        params.put("search", term);
        return params;
    }

    @Override
    public String getDomain() {
        return "a Star Wars character ( try \"skywalker\" )";
    }

    @Override
    public String getName() {
        return "Star Wars";
    }

}
