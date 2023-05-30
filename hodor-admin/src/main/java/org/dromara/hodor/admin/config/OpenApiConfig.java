/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hodor.admin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SwaggerConfig
 *
 * @author tomgs
 * @since 1.0
 */
@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI apiV1Info() {
        return new OpenAPI()
            .info(new Info()
                .title("Hodor Admin Api Docs")
                .description("HodorAdmin接口文档")
                .contact(new Contact()
                    .name("Hodor Group")
                    .email("tincopper@foxmail.com"))
                .version("v1"));
    }
}
