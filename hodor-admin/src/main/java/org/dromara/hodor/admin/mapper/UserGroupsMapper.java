package org.dromara.hodor.admin.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dromara.hodor.admin.domain.UserGroups;

/**
 * (UserGroups)表数据库访问层
 *
 * @author tomgs
 * @since 2023-05-10 19:31:21
 */
@Mapper
public interface UserGroupsMapper extends MPJBaseMapper<UserGroups> {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    UserGroups queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param userGroups 查询条件
     * @return 对象列表
     */
    List<UserGroups> queryAllByLimit(UserGroups userGroups, @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);

    /**
     * 统计总行数
     *
     * @param userGroups 查询条件
     * @return 总行数
     */
    long count(UserGroups userGroups);

    /**
     * 新增数据
     *
     * @param userGroups 实例对象
     * @return 影响行数
     */
    int insert(UserGroups userGroups);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<UserGroups> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<UserGroups> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<UserGroups> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<UserGroups> entities);

    /**
     * 修改数据
     *
     * @param userGroups 实例对象
     * @return 影响行数
     */
    int update(UserGroups userGroups);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}

