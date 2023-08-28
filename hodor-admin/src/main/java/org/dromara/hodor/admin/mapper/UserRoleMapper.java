package org.dromara.hodor.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dromara.hodor.admin.dto.user.UserRole;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @author tomgs
 *
 * @since 1.0
 **/
@Mapper
public interface UserRoleMapper {

    /**
     * 通过id查询角色.
     *
     * @param id
     * @return org.dromara.hodor.admin.domain.DBUserRole
     */
    List<UserRole> findRoleById(@Param("id") Integer id);

    /**
     * 通过角色名查询.
     *
     * @param name
     * @return org.dromara.hodor.admin.domain.DBUserRole
     */
    List<UserRole> findRoleByName(@Param("name") String name);

    /**
     * 查询角色.
     *
     * @return org.dromara.hodor.admin.domain.DBUserRole
     */
    List<UserRole> findRoles();

    /**
     * 通过id删除.
     *
     * @param id
     * @return void
     */
    void deleteById(@Param("id") Integer id);

    /**
     * 通过角色名删除.
     *
     * @param roleName
     * @return void
     */
    void deleteByRoleName(@Param("roleName") String roleName);

    /**
     * 新增角色.
     *
     * @param userRole
     * @return int
     */
    int insert(UserRole userRole);



}
