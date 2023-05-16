package org.dromara.hodor.admin.service;

import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.core.PageInfo;

/**
 * (User)表服务接口
 *
 * @author tomgs
 * @since 1.0
 */
public interface UserService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    User queryById(Long id);

    /**
     * 分页查询
     *
     * @param user 筛选条件
     * @param pageNo      第几页
     * @param pageSize    分页大小
     * @return 查询结果
     */
    PageInfo<User> queryByPage(User user, Integer pageNo, Integer pageSize);

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    User insert(User user);

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    User update(User user);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

    User findUser(String username, String password);
}
