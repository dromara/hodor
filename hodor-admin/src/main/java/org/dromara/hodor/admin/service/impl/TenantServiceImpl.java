package org.dromara.hodor.admin.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.admin.entity.Tenant;
import org.dromara.hodor.admin.mapper.TenantMapper;
import org.dromara.hodor.admin.service.TenantService;
import org.springframework.stereotype.Service;

/**
 * (Tenant)表服务实现类
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
@Service("tenantService")
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantMapper tenantMapper;

    @Override
    public Tenant queryById(Long id) {
        return tenantMapper.queryById(id);
    }

    @Override
    public PageInfo<Tenant> queryByPage(Tenant tenant, Integer pageNo, Integer pageSize) {
        long total = tenantMapper.count(tenant);
        List<Tenant> result = tenantMapper.queryAllByLimit(tenant, pageNo, pageSize);
		PageInfo<Tenant> pageInfo = new PageInfo<>();
        return pageInfo.setRows(result)
            .setTotal(total)
            .setPageNo(pageNo)
            .setPageSize(pageSize);
    }

    @Override
    public Tenant insert(Tenant tenant) {
        tenantMapper.insert(tenant);
        return tenant;
    }

    @Override
    public Tenant update(Tenant tenant) {
        tenantMapper.update(tenant);
        return queryById(tenant.getId());
    }

    @Override
    public boolean deleteById(Long id) {
        return tenantMapper.deleteById(id) > 0;
    }
}
