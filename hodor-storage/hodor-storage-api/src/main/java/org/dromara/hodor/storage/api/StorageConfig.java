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

import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * StorageConfig
 *
 * @author tomgs
 * @since 1.0
 */
@Data
public class StorageConfig {

    // base config
    private String type;
    private String user;
    private String password;
    private String basePath;
    private String storagePath;

    // custom config
    private Map<String,Object> attr = new LinkedHashMap<>();

    // ftp/sftp
    private String host;
    private Integer port;
    private String domain;
    private String privateKeyPath;

    // s3
    private String accessKey;
    private String secretKey;
    private String region;
    private String endpoint;
    private String bucketName;
    private String defaultAcl;

    // hdfs
    private String url;
    private String nameServices;
    private String nameNodes;
    private String rpcAddress;

}
