package org.dromara.hodor.admin.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hodor.admin.core.PageInfo;
import org.dromara.hodor.admin.domain.UserRole;
import org.dromara.hodor.admin.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RoleService
 *
 * @author tomgs
 * @since 1.0
 **/
@RequiredArgsConstructor
@Service("roleService")
public class RoleService {

    private final UserRoleMapper userRoleMapper;

    public Boolean ifExisted(String roleName) {
        List<UserRole> roles = userRoleMapper.findRoleByName(roleName);
        if (roles != null && roles.size() != 0 && roles.get(0).getId() != null) {
            return true;
        }
        return false;
    }

    public void insert(UserRole role) {
        userRoleMapper.insert(role);
    }

    public void deleteById(Integer id) {
        userRoleMapper.deleteById(id);
    }

    public void deleteByName(String name) {
        userRoleMapper.deleteByRoleName(name);
    }

    public PageInfo<UserRole> getRoleLists(String roleName, Integer pageNum, Integer pageSize) {
        PageInfo<UserRole> pageInfo = new PageInfo<>();
        if (pageNum != null && pageSize != null) {
            Page<UserRole> page = PageHelper.startPage(pageNum, pageSize);
            userRoleMapper.findRoleByName(roleName);
            List<UserRole> data = page.getResult();
            pageInfo.setTotalPage((int) page.getTotal());
            pageInfo.setTotalList(data);
            return pageInfo;
        } else {
            List<UserRole> list = userRoleMapper.findRoleByName(roleName);
            pageInfo.setTotalPage(list.size());
            pageInfo.setTotalList(list);
            return pageInfo;
        }


    }

    public UserRole getRoleByIdOrName(Integer id, String roleName) {
        List<UserRole> roles = null;
        if (id != null) {
            roles = userRoleMapper.findRoleById(id);
        }
        if (StringUtils.isNotEmpty(roleName)) {
            roles = userRoleMapper.findRoleByName(roleName);
        }
        if (roles != null && roles.size() > 0) {
            return roles.get(0);
        }
        return null;
    }
}
