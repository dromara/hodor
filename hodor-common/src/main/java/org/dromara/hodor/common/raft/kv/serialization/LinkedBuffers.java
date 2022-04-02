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

package org.dromara.hodor.common.raft.kv.serialization;

import io.protostuff.LinkedBuffer;

/**
 * @author jiachun.fjc
 */
public final class LinkedBuffers {

    // reuse the 'byte[]' of LinkedBuffer's head node
    private static final ThreadLocal<LinkedBuffer> bufThreadLocal = ThreadLocal.withInitial(
        () -> LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

    public static LinkedBuffer getLinkedBuffer() {
        return bufThreadLocal.get();
    }

    public static void resetBuf(final LinkedBuffer buf) {
        buf.clear();
    }

    private LinkedBuffers() {
    }
}
