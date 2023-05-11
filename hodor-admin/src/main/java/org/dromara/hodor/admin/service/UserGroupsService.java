package org.dromara.hodor.admin.service;

import org.dromara.hodor.admin.domain.UserGroups;
import org.dromara.hodor.core.PageInfo;

/**
 * (UserGroups)表服务接口
 *
 * @author tomgs
 * @since 1.0
 */
public interface UserGroupsService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    UserGroups queryById(Long id);

    /**
     * 分页查询
     *
     * @param userGroups 筛选条件
     * @param pageNo      第几页
     * @param pageSize    分页大小
     * @return 查询结果
     */
    PageInfo<UserGroups> queryByPage(UserGroups userGroups, Integer pageNo, Integer pageSize);

    /**
     * 新增数据
     *
     * @param userGroups 实例对象
     * @return 实例对象
     */
    UserGroups insert(UserGroups userGroups);

    /**
     * 修改数据
     *
     * @param userGroups 实例对象
     * @return 实例对象
     */
    UserGroups update(UserGroups userGroups);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

}
