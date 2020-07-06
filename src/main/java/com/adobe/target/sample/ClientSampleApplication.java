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
package com.adobe.target.sample;

import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.TargetClient;
import com.adobe.target.edge.client.model.ExecutionMode;
import com.adobe.target.edge.client.model.local.LocalExecutionHandler;
import com.adobe.target.edge.client.service.TargetClientException;
import com.adobe.target.sample.model.Product;
import com.adobe.target.sample.model.ProductRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.simple.SimpleLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import com.adobe.target.sample.model.InMemoryProductRepository;

import javax.annotation.PostConstruct;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class ClientSampleApplication {

    private static final Logger log = Logger.getLogger(ClientSampleApplication.class.getName());

    public static void main(String[] args) {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
        SpringApplication.run(ClientSampleApplication.class, args);
    }

    @Bean
    public ProductRepository productRepository() {
        return new InMemoryProductRepository();
    }

    @Bean
    public Converter<String, Product> productConverter() {
        //Don't convert to lambda. It'll break at runtime.
        return new Converter<String, Product>() {
            @Override
            public Product convert(String id) {
                return productRepository().findMessage(Long.valueOf(id));
            }

        };
    }

    @Bean
    TargetClient marketingCloudClient() {
        ClientConfig clientConfig = ClientConfig.builder()
                .client("emeaprod4")
                .organizationId("0DD934B85278256B0A490D44@AdobeOrg")
                .localEnvironment("sample")
                .defaultExecutionMode(ExecutionMode.HYBRID)
                .localExecutionHandler(new LocalExecutionHandler() {
                    @Override
                    public void localExecutionReady() {
                        log.info("Local Execution Ready");
                    }

                    @Override
                    public void artifactDownloadSucceeded(byte[] bytes) {
                        log.info("Local Execution Artifact Download Succeeded");
                    }

                    @Override
                    public void artifactDownloadFailed(TargetClientException e) {
                        log.log(Level.WARNING, "Local Execution Artifact Download Failed", e);
                    }
                })
                .build();

        return TargetClient.create(clientConfig);
    }

    @Bean
    public CommandLineRunner run(ProductRepository repository) {
        return args -> {
            Product bag = new Product(1L, "https://images-na.ssl-images-amazon.com/images/I/61qXziTJp%2BL._UX385_" +
                    ".jpg", "Women's Luxury Leather Bag");
            repository.save(bag);
            Product shoe = new Product(2L, "https://static.nike.com/a/images/c_limit,w_592,f_auto/t_product_v1/" +
                    "03119662-2585-4658-bed3-1fa9a19196c2/air-zoom-pegasus-37-mens-running-shoe-KLvDcj.jpg"
                    , "Running Shoe Nike");
            repository.save(shoe);
            Product dryer = new Product(3L, "https://target.scene7.com/is/image/Target/GUEST_9125f63c-6e50-4591-834b" +
                    "-26d7e7c293cf?wid=488&hei=488&fmt=pjpeg", "InfinitiPro Hair Dryer");
            repository.save(dryer);
            Product car = new Product(4L, "https://auto.ndtvimg.com/car-images/medium/maruti-suzuki/baleno/maruti" +
                    "-suzuki-baleno.jpg?v=1_1", "Maruti Suzuki Baleno");
            repository.save(car);
        };
    }

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void test() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);  // NON_EMPTY for '' or NULL value
    }
}
