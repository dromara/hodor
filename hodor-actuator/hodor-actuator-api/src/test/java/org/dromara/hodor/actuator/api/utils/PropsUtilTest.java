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

package org.dromara.hodor.actuator.api.utils;

import org.dromara.hodor.common.utils.Props;
import org.dromara.hodor.common.utils.PropsUtils;
import org.junit.Test;

/**
 * PropsUtilTest
 *
 * @author tomgs
 * @since 1.0
 */
public class PropsUtilTest {

    @Test
    public void testResolveProps() {
        Props props = new Props();
        props.put("a", 123);
        props.put("b", "${a}");
        props.put("command", "sh log_clean/log_clean/hive2ESDataxJob.sh $(YEAR)-$(MONTH)-$($(DAY)+0) dm_complete_burial_agg_d_p_uvJob.sh $(YEAR)$(MONTH)$($(DAY)+0)");
        Props props1 = PropsUtils.resolveProps(props);
        System.out.println(props1);
    }

}
