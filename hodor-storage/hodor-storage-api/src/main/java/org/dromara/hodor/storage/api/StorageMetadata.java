/*
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.dromara.hodor.storage.api;

import java.io.File;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class StorageMetadata {

    private final File resourcesDirectory;

    private final String jobId;

    private final int version;

    private final String uploader;

    private final byte[] hash;

    private final String uploaderIPAddr;

    public StorageMetadata(final File resourcesDirectory, final String jobId, final int version, final String uploader,
                           final byte[] hash, final String uploaderIPAddr) {
        this.resourcesDirectory = resourcesDirectory;
        this.jobId = jobId;
        this.version = version;
        this.uploader = requireNonNull(uploader);
        this.hash = hash;
        this.uploaderIPAddr = uploaderIPAddr;
    }

    @Override
    public String toString() {
        return "StorageMetadata{" + "jobKey='" + this.jobId + '\'' + ", version='" + this.version
            + '\''
            + '}';
    }

    public String getJobId() {
        return this.jobId;
    }

    public int getVersion() {
        return this.version;
    }

    public String getUploader() {
        return this.uploader;
    }

    public byte[] getHash() {
        return this.hash;
    }

    public String getUploaderIPAddr() {
        return this.uploaderIPAddr;
    }

    public File getResourcesDirectory() {
        return resourcesDirectory;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StorageMetadata that = (StorageMetadata) o;
        return Objects.equals(this.resourcesDirectory, that.resourcesDirectory) &&
            Objects.equals(this.jobId, that.jobId) &&
            Objects.equals(this.version, that.version) &&
            Objects.equals(this.uploader, that.uploader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.resourcesDirectory, this.jobId, this.version, this.uploader);
    }
}
