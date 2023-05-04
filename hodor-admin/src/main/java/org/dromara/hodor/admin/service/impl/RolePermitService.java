package org.dromara.hodor.admin.service.impl;

import org.dromara.hodor.admin.domain.PermitItem;
import org.dromara.hodor.admin.domain.RolePermit;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.mapper.PermitItemMapper;
import org.dromara.hodor.admin.mapper.RolePermitMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dbPermitService")
public class RolePermitService {

    @Autowired
    private RolePermitMapper rolePermitMapper;

    @Autowired
    private PermitItemMapper permitItermMapper;

    /**
     * 查询是否具有某权限	对于一个查询来说,如果具有查询的父页面权限,则默认具有该查询权限;如果设置了没有该查询条件,那么结果则是没有查询权限
     *
     * @param user
     * @param permitItem
     * @return
     */
    public boolean hasPermit(User user, String permitItem) {

        PermitItem dbPermitItem = permitItermMapper.selectByUri(permitItem);
        if (dbPermitItem != null) {
            String permitId = dbPermitItem.getId();
            if (!dbPermitItem.isEnabled()) {
                return true;
            } else {
                if (rolePermitMapper.select(new RolePermit(user.getRoleId().toString(), permitId)) == null) {
                    if (dbPermitItem.getOperateType() != null && dbPermitItem.getOperateType().equals("SELECT")) {
                        return hasParentPagePermit(user.getRoleId(), dbPermitItem.getParentId());
                    }
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            return true;
        }
    }

    private boolean hasParentPagePermit(Integer roleId, String permitId) {
        PermitItem permitItem = permitItermMapper.selectById(permitId);
        if ("PAGE".equals(permitItem.getItemType())) {
            return rolePermitMapper.select(new RolePermit(roleId.toString(), permitId)) != null;
        }
        return false;
    }

    /**
     * 创建用户的权限
     *
     * @param permit
     */
    public void createPermitItem(RolePermit permit) {
        if (rolePermitMapper.select(permit) == null) {
            rolePermitMapper.insert(permit);
        }
    }

    /**
     *
     */
    public void removePermitItem(RolePermit permit) {
        rolePermitMapper.delete(permit);
    }

    /**
     *
     */
    public void removeByRoleId(Integer roleId) {
        RolePermit permit = new RolePermit();
        permit.setRoleId(roleId.toString());
        rolePermitMapper.deleteByRoleId(permit);
    }

    public List<RolePermit> getListByRoleId(Integer roleId){
        RolePermit rolePermit = new RolePermit();
        rolePermit.setRoleId(roleId.toString());
        return rolePermitMapper.getListByRoleId(rolePermit);
    }
}
