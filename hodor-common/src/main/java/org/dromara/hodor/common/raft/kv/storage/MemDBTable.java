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

package org.dromara.hodor.common.raft.kv.storage;

import java.io.IOException;
import java.util.List;
import org.dromara.hodor.common.raft.kv.protocol.KVEntry;

/**
 * MemoryDB Table
 *
 * @author tomgs
 * @since 1.0
 */
public class MemDBTable implements Table<byte[], byte[]> {

    @Override
    public void put(byte[] bytes, byte[] bytes2) throws IOException {

    }

    @Override
    public boolean isEmpty() throws IOException {
        return false;
    }

    @Override
    public byte[] get(byte[] bytes) throws IOException {
        return new byte[0];
    }

    @Override
    public void delete(byte[] bytes) throws IOException {

    }

    @Override
    public TableIterator<byte[], ? extends KeyValue<byte[], byte[]>> iterator() {
        return null;
    }

    @Override
    public String getName() throws IOException {
        return null;
    }

    @Override
    public long getEstimatedKeyCount() throws IOException {
        return 0;
    }

    @Override
    public List<KVEntry> scan(byte[] startKey, byte[] endKey, boolean returnValue) {
        return null;
    }

    @Override
    public Boolean containsKey(byte[] key) {
        return null;
    }

}
