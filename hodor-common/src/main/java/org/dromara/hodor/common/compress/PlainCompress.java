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

import cn.hutool.core.lang.TypeReference;
import org.dromara.hodor.common.utils.SerializeUtils;

/**
 * ByteCompressData
 *
 * @author tomgs
 * @since 2021/9/14
 */
public class PlainCompress implements Compress {

    private static volatile PlainCompress instance;

    private PlainCompress() {

    }

    public static Compress getInstance() {
        if (instance == null) {
            synchronized (PlainCompress.class) {
                if (instance == null) {
                    instance = new PlainCompress();
                }
            }
        }
        return instance;
    }

    @Override
    public <T> T uncompress(byte[] data) {
        TypeReference<T> typeReference = new TypeReference<T>() {};
        return SerializeUtils.deserialize(data, typeReference.getType());
    }

    @Override
    public <T> byte[] compress(T t) {
        return SerializeUtils.serialize(t);
    }

}
