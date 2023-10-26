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
import org.dromara.hodor.storage.api.StorageConfig;
import org.dromara.x.file.storage.core.FileStorageProperties;

/**
 * XFileStorage
 *
 * @author tomgs
 * @since 1.0
 */
public class SftpFileStorage extends AbstractXFileStorage {

    public SftpFileStorage(StorageConfig config) {
        FileStorageProperties.SftpConfig sftp = new FileStorageProperties.SftpConfig();
        sftp.setPlatform("sftp");
        sftp.setHost(config.getHost());
        sftp.setPort(config.getPort());
        sftp.setUser(config.getUser());
        sftp.setPassword(config.getPassword());
        sftp.setDomain(config.getDomain());
        sftp.setBasePath(config.getBasePath());
        sftp.setStoragePath(config.getStoragePath());
        sftp.setPrivateKeyPath(config.getPrivateKeyPath());
        FileStorageProperties properties = new FileStorageProperties();
        properties.setDefaultPlatform("sftp");
        properties.setSftp(Collections.singletonList(sftp));
        init(properties);
    }
}
