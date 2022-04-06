package com.dromara.hodor.common.kv;

import com.google.common.collect.Lists;
import org.dromara.hodor.common.raft.RaftOptions;
import org.dromara.hodor.common.raft.kv.core.HodorKVOptions;
import org.dromara.hodor.common.raft.kv.core.HodorKVServer;
import org.dromara.hodor.common.raft.kv.storage.StorageOptions;
import org.dromara.hodor.common.raft.kv.storage.StorageType;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * HodorKVServerTest
 *
 * @author tomgs
 * @version 2022/4/5 1.0
 */
public class HodorKVServerTest {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java -cp *.jar com.dromara.hodor.common.kv.HodorKVServerTest {serverIndex}");
            System.err.println("{serverIndex} could be 1, 2 or 3");
            System.exit(1);
        }

        List<String> endpoints = Lists.newArrayList("127.0.0.1:8081", "127.0.0.1:8082", "127.0.0.1:8083");

        RaftOptions raftOptions = RaftOptions.builder()
                .endpoint(endpoints.get(Integer.parseInt(args[0]) - 1))
                .storageDir(new File("target/test_kv/raft"))
                .serverAddresses("127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083")
                //.stateMachineMap(stateMachineMap) // for customer
                .build();
        StorageOptions storageOptions = StorageOptions.builder()
                .storageType(StorageType.RocksDB)
                .storagePath(new File("target/test_kv/db"))
                .build();
        HodorKVOptions kvOptions = HodorKVOptions.builder()
                .clusterName("test_hodor_kv")
                .raftOptions(raftOptions)
                .storageOptions(storageOptions)
                .build();
        HodorKVServer hodorKVServer = new HodorKVServer(kvOptions);
        hodorKVServer.start();

        //exit when any input entered
        Scanner scanner = new Scanner(System.in, UTF_8.name());
        scanner.nextLine();
        hodorKVServer.stop();
    }

}
