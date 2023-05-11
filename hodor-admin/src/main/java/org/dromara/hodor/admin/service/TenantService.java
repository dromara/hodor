package org.dromara.hodor.admin.service;

import org.dromara.hodor.admin.domain.Tenant;
import org.dromara.hodor.core.PageInfo;

/**
 * (Tenant)表服务接口
 *
 * @author tomgs
 * @since 1.0
 */
public interface TenantService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Tenant queryById(Long id);

    /**
     * 分页查询
     *
     * @param tenant 筛选条件
     * @param pageNo      第几页
     * @param pageSize    分页大小
     * @return 查询结果
     */
    PageInfo<Tenant> queryByPage(Tenant tenant, Integer pageNo, Integer pageSize);

    /**
     * 新增数据
     *
     * @param tenant 实例对象
     * @return 实例对象
     */
    Tenant insert(Tenant tenant);

    /**
     * 修改数据
     *
     * @param tenant 实例对象
     * @return 实例对象
     */
    Tenant update(Tenant tenant);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

}
