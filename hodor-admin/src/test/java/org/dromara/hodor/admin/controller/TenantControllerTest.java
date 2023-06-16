package org.dromara.hodor.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.BaseWebTest;
import org.dromara.hodor.admin.entity.Tenant;
import org.dromara.hodor.common.utils.DateUtils;
import org.junit.jupiter.api.Test;

@Slf4j
class TenantControllerTest extends BaseWebTest {

    @Test
    void queryAllByPage() throws Exception {
        final String result = getRequest("/tenant?pageNo=0&pageSize=10");
        log.info(result);
    }

    @Test
    void queryByPage() throws Exception {
        Tenant tenant = new Tenant();
        tenant.setCorpName("tomgs");
        final String result = getRequest("/tenant?pageNo=0&pageSize=10", toUriParams(tenant));
        log.info(result);
    }

    @Test
    void queryById() throws Exception {
        getRequest("/tenant/1");
    }

    @Test
    void add() throws Exception {
        Tenant tenant = new Tenant();
        tenant.setTenantName("tomgs");
        tenant.setEmail("123@qq.com");
        tenant.setCorpName("tomgs corp");
        tenant.setCreatedAt(DateUtils.nowDate());
        tenant.setUpdatedAt(DateUtils.nowDate());
        postJson("/tenant", tenant);
    }

    @Test
    void update() throws Exception {
        Tenant tenant = new Tenant();
        tenant.setId(1L);
        tenant.setTenantName("tomgs1");
        tenant.setEmail("12356@qq.com");
        tenant.setCorpName("tomgs corp1");
        tenant.setUpdatedAt(DateUtils.nowDate());
        putRequest("/tenant", tenant);
    }

    @Test
    void deleteById() throws Exception {
        deleteRequest("/tenant?id=1");
    }
}
