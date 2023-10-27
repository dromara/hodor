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

package org.dromara.hodor.storage.local;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.HexUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.dromara.hodor.common.extension.Join;
import org.dromara.hodor.common.utils.Utils.Assert;
import org.dromara.hodor.storage.api.FileStorage;
import org.dromara.hodor.storage.api.StorageConfig;
import org.dromara.hodor.storage.api.StorageMetadata;
import org.dromara.hodor.storage.exception.StorageException;

/**
 * LocalFileStorage
 *
 * @author tomgs
 * @since 1.0
 */
@Join
@Slf4j
public class LocalFileStorage implements FileStorage {

    private final StorageConfig storageConfig;

    public LocalFileStorage(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
    }

    @Override
    public InputStream fetchFile(Path path) {
        Assert.notNull(path, "file path must be not null.");
        return FileUtil.getInputStream(path);
    }

    @Override
    public void pushFile(StorageMetadata metadata, File localFile) {
        // // ${data_path}/resources/${job_key}/${version}
        Path jobResourcePath = Paths.get(metadata.getResourcesDirectory().getPath(),
                String.valueOf(metadata.getJobId()), String.valueOf(metadata.getVersion()));
        final File resourcesDir = jobResourcePath.toFile();
        if (resourcesDir.mkdir()) {
            log.info("Created job resources dir: " + resourcesDir.getAbsolutePath());
        }

        final File targetFile = new File(resourcesDir, String.format("%s-%s.zip",
                metadata.getJobId(),
                new String(HexUtil.encodeHex(metadata.getHash()))));

        if (targetFile.exists()) {
            log.info(String.format("Duplicate found: meta: %s, targetFile: %s, ", metadata,
                    targetFile.getAbsolutePath()));
            return;
        }

        // Copy file to storage dir
        try {
            FileUtils.copyFile(localFile, targetFile);
        } catch (final IOException e) {
            log.error("LocalStorage error in put(): meta: " + metadata);
            throw new StorageException(e);
        }
    }

    @Override
    public boolean deleteFile(Path path) {
        Assert.notNull(path, "file path must be not null.");
        return FileUtil.del(path);
    }

}
