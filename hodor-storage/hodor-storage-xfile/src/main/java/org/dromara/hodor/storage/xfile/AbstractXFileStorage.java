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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import org.dromara.hodor.storage.api.FileStorage;
import org.dromara.hodor.storage.api.StorageMetadata;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageProperties;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.FileStorageServiceBuilder;

/**
 * AbstractXFileStorage
 *
 * @author tomgs
 * @since 1.0
 */
public abstract class AbstractXFileStorage implements FileStorage {

    private FileStorageService storageService;

    public void init(FileStorageProperties properties) {
        this.storageService = FileStorageServiceBuilder.create(properties)
            .useDefault()
            .build();
    }

    @Override
    public InputStream fetchFile(Path path) throws IOException {
        AtomicReference<InputStream> inputStream = new AtomicReference<>();
        FileInfo fileInfo = storageService.getFileInfoByUrl(path.toString());
        storageService.download(fileInfo).inputStream(inputStream::set);
        return inputStream.get();
    }

    @Override
    public void pushFile(StorageMetadata metadata, File localFile) throws IOException {
        storageService.of(localFile)
            .putMetadata("", "")
            .upload();
    }

    @Override
    public boolean deleteFile(Path path) {
        return storageService.delete(path.toString());
    }
}
