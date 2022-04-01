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

package com.dromara.hodor.common.raft;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.dromara.hodor.common.raft.HodorRaftGroup;
import org.dromara.hodor.common.raft.HodorRaftServer;
import org.dromara.hodor.common.raft.HodorRaftStateMachine;
import org.dromara.hodor.common.raft.RaftOptions;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * HodorRaftServerTest
 *
 * @author tomgs
 * @since 2022/3/31
 */
public class HodorRaftServerTest2 {

    public static void main(String[] args) throws IOException {
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

        RaftOptions raftOptions = new RaftOptions();
        raftOptions.setEndpoint("127.0.0.1:8082");
        raftOptions.setStorageDir(new File("target/test_raft/"));
        raftOptions.setStateMachineMap(stateMachineMap);

        HodorRaftServer hodorRaftServer = new HodorRaftServer(raftOptions);
        hodorRaftServer.start();

        //exit when any input entered
        Scanner scanner = new Scanner(System.in, UTF_8.name());
        scanner.nextLine();
        hodorRaftServer.stop();
    }

}
