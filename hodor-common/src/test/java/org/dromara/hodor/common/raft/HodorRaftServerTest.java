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

package org.dromara.hodor.common.raft;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * HodorRaftServerTest
 *
 * @author tomgs
 * @since 1.0
 */
public class HodorRaftServerTest {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: java -cp *.jar org.dromara.hodor.common.raft.HodorRaftServerTest {serverIndex}");
            System.err.println("{serverIndex} could be 1, 2 or 3");
            System.exit(1);
        }

        List<String> endpoints = Lists.newArrayList("127.0.0.1:8081", "127.0.0.1:8082", "127.0.0.1:8083");

        HodorRaftGroup hodorRaftGroup = HodorRaftGroup.builder()
            .raftGroupName("RatisKVGroup0000")
            .addresses("127.0.0.1:8081:1,127.0.0.1:8082:2,127.0.0.1:8083:3")
            .build();

        HodorRaftGroup hodorRaftGroup2 = HodorRaftGroup.builder()
            .raftGroupName("RatisKVGroup0001")
            .addresses("127.0.0.1:8081:3,127.0.0.1:8082:2,127.0.0.1:8083:1")
            .build();

        Map<HodorRaftGroup, HodorRaftStateMachine> stateMachineMap = new HashMap<>();
        stateMachineMap.put(hodorRaftGroup, new DemoHodorRaftStateMachine());
        stateMachineMap.put(hodorRaftGroup2, new Demo2HodorRaftStateMachine());

        RaftOptions raftOptions = RaftOptions.builder()
                .endpoint(endpoints.get(Integer.parseInt(args[0]) - 1))
                .storageDir(new File("target/test_raft/"))
                .stateMachineMap(stateMachineMap)
                .build();
        HodorRaftServer hodorRaftServer = new HodorRaftServer(raftOptions);
        hodorRaftServer.start();

        //exit when any input entered
        Scanner scanner = new Scanner(System.in, UTF_8.name());
        scanner.nextLine();
        hodorRaftServer.stop();
    }

}
