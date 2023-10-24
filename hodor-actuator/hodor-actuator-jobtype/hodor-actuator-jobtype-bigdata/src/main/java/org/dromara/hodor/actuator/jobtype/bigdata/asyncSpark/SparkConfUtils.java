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

package org.dromara.hodor.actuator.jobtype.bigdata.asyncSpark;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.dromara.hodor.actuator.jobtype.bigdata.javautils.RegexUtil;
import org.dromara.hodor.actuator.jobtype.bigdata.javautils.YarnSubmitArguments;

/**
 * SparkConfUtils
 *
 * @author tomgs
 * @since 1.0
 */
public class SparkConfUtils {

    public static SparkConf getSparkConf(YarnSubmitArguments arguments) {
        SparkConf sparkConf = new SparkConf();
        if (StringUtils.isNotEmpty(arguments.getJobName())) {
            sparkConf.setAppName(arguments.getJobName());
        }

        sparkConf.set("spark.yarn.jars", arguments.getYarnJars());
        //sparkConf.set("spark.driver.host", conditions.getClientHost());
        if (arguments.getDependJars() != null && arguments.getDependJars().length > 0) {
            sparkConf.set("spark.jars", StringUtils.join(arguments.getDependJars(), ","));
        }

        if (arguments.getFiles() != null && arguments.getFiles().length > 0) {
            sparkConf.set("spark.files", StringUtils.join(arguments.getFiles(), ","));
        }

        if (arguments.getProperties() != null) {
            for (Map.Entry<Object, Object> e : arguments.getProperties().entrySet()) {
                sparkConf.set(e.getKey().toString(), e.getValue().toString());
            }
        }

        //sparkConf.set("yarn.resourcemanager.ha.rm-ids", "rm1,rm2");
        // 添加这个参数，不然spark会一直请求0.0.0.0:8030,一直重试
        List<String> host = RegexUtil.getResourceHost(arguments.getYarnResourcemanagerAddress());
        if (host.size() < 2) {
            throw new IllegalArgumentException("resourcemanagerAddress is illegal argument...");
        }
        try (Socket socket = new Socket()) {
            SocketAddress socketAddress = new InetSocketAddress(host.get(1), 8030);
            socket.connect(socketAddress, 3000);// 超时3秒
            sparkConf.set("yarn.resourcemanager.hostname", host.get(1));
        } catch (IOException e) {
            sparkConf.set("yarn.resourcemanager.hostname", host.get(0));
        }
        // 设置为true，不删除缓存的jar包，因为现在提交yarn任务是使用的代码配置，没有配置文件，删除缓存的jar包有问题，
        sparkConf.set("spark.yarn.preserve.staging.files", "true");
        sparkConf.set("spark.sql.session.timeZone", "Asia/Shanghai");
        sparkConf.set("spark.yarn.queue", arguments.getQueue());
        sparkConf.set("spark.submit.deployMode", "cluster");
        return sparkConf;
    }

}
