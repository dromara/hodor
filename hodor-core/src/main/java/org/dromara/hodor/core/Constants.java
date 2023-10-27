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

package org.dromara.hodor.core;

/**
 * Constants
 *
 * @author tomgs
 * @since 1.0
 */
public interface Constants {

    String SCHEDULER_NAME = "schedulerName";

    class JobConstants {

        public static final String TIME_TYPE_KEY = "timeType";

        public static final String TIME_EXP_KEY = "timeExp";

        public static final String COMMAND_TYPE_KEY = "commandType";

        public static final String COMMAND_KEY = "command";

        public static final String JOB_STAGE_KEY = "stage";

        public static final String SPLIT_STAGE = "split_stage";

        public static final String MAP_STAGE = "map_stage";

        public static final String REDUCE_STAGE = "reduce_stage";
    }

    /**
     * CopySet config
     */
    class CopySetConstants {

        public static final Integer LEAST_NODE_COUNT = 3;

        /**
         * 副本数量，不超过节点数量
         */
        public static final Integer REPLICA_COUNT = 2;

        /**
         * 副本散布宽度，小于节点数量
         */
        public static final Integer SCATTER_WIDTH = 2;
    }


    class FlowNodeConstants {

        public static final String ROOT_JOB_KEY = "rootJobKey";

        public static final String NODE_ID = "nodeId";

        public static final String NODE_STATUS = "nodeStatus";

        public static final String GROUP_NAME = "groupName";

        public static final String NODE_NAME = "nodeName";

        public static final String RAW_DATA = "rawData";

    }

}
