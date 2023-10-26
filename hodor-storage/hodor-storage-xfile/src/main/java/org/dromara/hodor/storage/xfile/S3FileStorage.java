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

package org.dromara.hodor.storage.xfile;

import java.util.Collections;
import org.dromara.hodor.common.extension.Join;
import org.dromara.hodor.storage.api.StorageConfig;
import org.dromara.x.file.storage.core.FileStorageProperties;

/**
 * S3Storage
 *
 * @author tomgs
 * @since 1.0
 */
@Join
public class S3FileStorage extends AbstractXFileStorage {

    public S3FileStorage(StorageConfig config) {
        FileStorageProperties.AmazonS3Config s3Config = new FileStorageProperties.AmazonS3Config();
        s3Config.setPlatform("s3");
        s3Config.setAccessKey(config.getAccessKey());
        s3Config.setSecretKey(config.getSecretKey());
        s3Config.setRegion(config.getRegion());
        s3Config.setEndPoint(config.getEndPoint());
        s3Config.setBucketName(config.getBucketName());
        s3Config.setDefaultAcl(config.getDefaultAcl());
        s3Config.setDomain(config.getDomain());
        s3Config.setBasePath(config.getBasePath());
        FileStorageProperties properties = new FileStorageProperties();
        properties.setDefaultPlatform("s3");
        properties.setAmazonS3(Collections.singletonList(s3Config));
        init(properties);
    }
}
