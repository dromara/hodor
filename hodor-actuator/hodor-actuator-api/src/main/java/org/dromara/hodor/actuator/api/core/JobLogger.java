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

package org.dromara.hodor.actuator.api.core;

import org.apache.log4j.Category;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

/**
 * JobLogger
 *
 * @author tomgs
 * @since 2022/2/15
 */
public class JobLogger {

    private final String name;

    private final Path logPath;

    private final Logger logger;

    public JobLogger(String name, Path logPath, Logger logger) {
        this.name = name;
        this.logPath = logPath;
        this.logger = logger;
    }

    public String getName() {
        return name;
    }

    public Path getLogPath() {
        return logPath;
    }

    public Logger getLogger() {
        return logger;
    }

    public org.apache.log4j.Logger getLog4jLogger() {
        Category category = Category.getInstance(logger.getName());
        return (org.apache.log4j.Logger) category;
    }


}
