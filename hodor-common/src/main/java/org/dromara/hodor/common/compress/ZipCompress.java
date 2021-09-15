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

package org.dromara.hodor.common.compress;

/**
 * ZipCompress
 *
 * @author tomgs
 * @since 2021/9/14
 */
public class ZipCompress implements Compress {

    private static volatile ZipCompress instance;

    private ZipCompress() {

    }

    public static Compress getInstance() {
        if (instance == null) {
            synchronized (ZipCompress.class) {
                if (instance == null) {
                    instance = new ZipCompress();
                }
            }
        }
        return instance;
    }

    @Override
    public <T> T uncompress(byte[] data) {
        return null;
    }

    @Override
    public <T> byte[] compress(T t) {
        return new byte[]{};
    }

}
