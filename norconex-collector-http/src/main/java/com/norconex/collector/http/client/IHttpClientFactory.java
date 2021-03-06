/* Copyright 2010-2014 Norconex Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.norconex.collector.http.client;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Create (and initializes) an {@link HttpClient} to be used for all 
 * HTTP request this crawler will make.  If implementing 
 * {@link CloseableHttpClient} the crawler will take care of closing
 * it properly when crawling ends.
 * 
 * Implementors also implementing IXMLConfigurable must name their XML tag
 * <code>httpClientFactory</code> to ensure it gets loaded properly.
 * @since 1.3.0
 * @author Pascal Essiembre
 */
public interface IHttpClientFactory {

    /**
     * Initializes the HTTP Client used for crawling.
     * @param userAgent the HTTP request "User-Agent" header value
     * @return Apache HTTP Client
     */
	HttpClient createHTTPClient(String userAgent);
}
