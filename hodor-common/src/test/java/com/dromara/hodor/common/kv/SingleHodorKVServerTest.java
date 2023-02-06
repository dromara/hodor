package com.dromara.hodor.common.kv;

import java.io.File;
import java.util.Scanner;
import org.apache.ratis.conf.RaftProperties;
import org.dromara.hodor.common.raft.RaftOptions;
import org.dromara.hodor.common.raft.kv.core.HodorKVOptions;
import org.dromara.hodor.common.raft.kv.core.HodorKVServer;
import org.dromara.hodor.common.raft.kv.storage.StorageOptions;
import org.dromara.hodor.common.raft.kv.storage.StorageType;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * HodorKVServerTest
 *
 * @author tomgs
 * @version 2022/4/5 1.0
 */
public class SingleHodorKVServerTest {

    public static void main(String[] args) throws Exception {
        RaftOptions raftOptions = RaftOptions.builder()
                .endpoint("127.0.0.1:8081")
                .storageDir(new File("target/test_kv/single_raft"))
                .serverAddresses("127.0.0.1:8081")
                //.stateMachineMap(stateMachineMap) // for customer
                .raftProperties(new RaftProperties())
                .build();
        StorageOptions storageOptions = StorageOptions.builder()
                .storageType(StorageType.RocksDB)
                .storagePath(new File("target/test_kv/single_db"))
                .build();
        HodorKVOptions kvOptions = HodorKVOptions.builder()
                .clusterName("test_single_hodor_kv")
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
