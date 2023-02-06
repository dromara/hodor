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
package org.dromara.hodor.common.metrics;

/**
 * KVMetrics prefix names.
 *
 * @author jiachun.fjc
 */
public class KVMetricNames {

    // for state machine
    public static final String STATE_MACHINE_APPLY_QPS   = "hodor-st-apply-qps";
    public static final String STATE_MACHINE_BATCH_WRITE = "hodor-st-batch-write";

    // for rpc
    public static final String RPC_REQUEST_HANDLE_TIMER  = "hodor-rpc-request-timer";

    public static final String DB_TIMER                  = "hodor-db-timer";

    public static final String REGION_KEYS_READ          = "hodor-region-keys-read";
    public static final String REGION_KEYS_WRITTEN       = "hodor-region-keys-written";

    public static final String REGION_BYTES_READ         = "hodor-region-bytes-read";
    public static final String REGION_BYTES_WRITTEN      = "hodor-region-bytes-written";

    public static final String SEND_BATCHING             = "send_batching";
}
