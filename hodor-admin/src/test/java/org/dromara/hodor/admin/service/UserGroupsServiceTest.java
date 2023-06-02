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

package org.dromara.hodor.admin.service;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.BaseTest;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.core.entity.JobGroup;
import org.junit.jupiter.api.Test;

/**
 * UserGroupsServiceTest
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class UserGroupsServiceTest extends BaseTest {

    @Resource
    private UserGroupsService userGroupsService;

    @Test
    public void testQueryByUserId() {
        final PageInfo<JobGroup> jobGroupPageInfo = userGroupsService.queryByUserId(1L, 0, 10);
        log.info("{}", jobGroupPageInfo);
    }

}
