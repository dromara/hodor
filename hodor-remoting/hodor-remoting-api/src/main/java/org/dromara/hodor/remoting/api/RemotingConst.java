/*
 *     Licensed to the Apache Software Foundation (ASF) under one or more
 *     contributor license agreements.See the NOTICE file distributed with
 *     this work for additional information regarding copyright ownership.
 *     The ASF licenses this file to You under the Apache License, Version 2.0
 *     (the "License"); you may not use this file except in compliance with
 *     the License.You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package org.dromara.hodor.remoting.api;


/**
 * RemotingConst.
 *
 * @author xiaoyu
 */
public class RemotingConst  {
    /**
     * Remote service host.
     */
    public static final String HOST_KEY = "host";

    /**
     * The constant PORT_KEY.
     */
    public static final String PORT_KEY = "port";

    /**
     * The constant PORT_KEY.
     */
    public static final String USE_EPOLL_NATIVE = "use_epoll_native";

    /**
     * The constant IO_THREADS_KEY.
     */
    public static final String IO_THREADS_KEY = "io_threads";

    /**
     * The constant NET_TIMEOUT_KEY.
     */
    public static final String NET_TIMEOUT_KEY = "net_timeout";

    /**
     * The constant HTTP_PROTOCOL
     */
    public static final String HTTP_PROTOCOL = "http_protocol";

    /**
     * The constant TCP_PROTOCOL
     */
    public static final String TCP_PROTOCOL = "tcp_protocol";

    /**
     * The constant RPC_VERSION
     */
    public static final int RPC_VERSION = 1;

    public static final int RPC_CRC_CODE = 12345;

    public static final int MAX_FRAME_LENGTH = 32 * 1024 * 1024;

    public static final int LENGTH_FIELD_OFFSET = 5;

    public static final int LENGTH_FIELD_LENGTH = 4;
}
