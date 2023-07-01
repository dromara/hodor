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

package org.dromara.hodor.register.embedded;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.dromara.hodor.common.proto.WatchCreateRequest;
import org.dromara.hodor.register.embedded.core.WatchManager;
import org.junit.Test;

/**
 * WatchManagerTest
 *
 * @author tomgs
 * @since 1.0
 */
public class WatchManagerTest {

    @Test
    public void testContainsWatchKey() {
        final WatchManager watchManager = new WatchManager(null, null);
        WatchCreateRequest watchCreateRequest = WatchCreateRequest.newBuilder()
            .setKey(ByteString.copyFrom("/a/b".getBytes(StandardCharsets.UTF_8)))
            .build();
        watchManager.addWatchRequest("connectionId", watchCreateRequest);

        boolean result = watchManager.containsWatchKey("/a/b/c");
        System.out.println(result);

        result = watchManager.containsWatchKey("/a/b/d");
        System.out.println(result);

        result = watchManager.containsWatchKey("/a/b/b");
        System.out.println(result);

        result = watchManager.containsWatchKey("/b/d/d");
        System.out.println(result);

        result = watchManager.containsWatchKey("/a/d/d");
        System.out.println(result);

        final ByteString watchKeyByteString = ByteString.copyFromUtf8("/a/b/c");
        final Optional<byte[]> watchKey = watchManager.getWatchKey(watchKeyByteString.toByteArray());
        System.out.println(watchKey);
    }

}
