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

package org.dromara.hodor.storage.api;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

/**
 * StorageConfig
 *
 * @author tomgs
 * @since 1.0
 */
@Builder
@Getter
public class StorageConfig {

    private String type;

    @Builder.Default
    private Map<String,Object> attr = new LinkedHashMap<>();

    // ftp/sftp
    private String host;
    private Integer port;
    private String user;
    private String password;
    private String domain;
    private String basePath;
    private String storagePath;
    private String privateKeyPath;

    // s3
    private String accessKey;
    private String secretKey;
    private String region;
    private String endPoint;
    private String bucketName;
    private String defaultAcl;

    // hdfs

}
