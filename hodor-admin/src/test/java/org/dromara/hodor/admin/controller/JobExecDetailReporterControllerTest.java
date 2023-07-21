package org.dromara.hodor.admin.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.BaseWebTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class JobExecDetailReporterControllerTest extends BaseWebTest {

    @Test
    void queryByPage() throws Exception {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("pageSize", "5");
        queryParams.put("pageNo", "5");
        queryParams.put("groupName", "testGroup");
        final String result = getRequest("/jobExecDetail", queryParams);
        log.info(result);
    }

    @Test
    void queryById() {
    }

    @Test
    void update() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void queryLog() {

    }
}
