package org.dromara.hodor.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.BaseWebTest;
import org.dromara.hodor.common.utils.JSONUtils;
import org.dromara.hodor.core.entity.JobGroup;
import org.junit.jupiter.api.Test;

@Slf4j
class JobGroupControllerTest extends BaseWebTest {

    @Test
    void createGroup() throws Exception {
        JobGroup jobGroup = new JobGroup();
        jobGroup.setGroupName("testGroup");
        jobGroup.setCreateUser("test");
        jobGroup.setRemark("test group name");
        final String result = postJson("/group", JSONUtils.toJsonStr(jobGroup));
        log.info(result);
    }

    @Test
    void queryById() throws Exception {
        final String result = getRequest("/group/1656491741900492802");
        log.info(result);
    }

    @Test
    void queryGroupListPaging() throws Exception {
        final String result = getRequest("/group?pageNo=0&pageSize=10");
        log.info(result);
    }

    @Test
    void update() throws Exception {
        JobGroup jobGroup = new JobGroup();
        jobGroup.setId(1656491741900492802L);
        jobGroup.setGroupName("testGroup1");
        jobGroup.setRemark("test group name");
        putRequest("/group", jobGroup);
    }

    @Test
    void delete() throws Exception {
        deleteRequest("/group?id=1656491741900492802");
    }
}