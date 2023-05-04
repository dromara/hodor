package org.dromara.hodor.admin.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.core.ServerConfigKeys;
import org.dromara.hodor.admin.domain.RolePermit;
import org.dromara.hodor.admin.domain.User;
import org.springframework.stereotype.Service;

/**
 * 权限相关服务类, 供Controller使用
 *
 * @author chengangxiong
 */
@Slf4j
@RequiredArgsConstructor
@Service("permitService")
public class PermitService {

    private final PermitItemService permitItemService;

    private final RolePermitService rolePermitService;

    //private final DBUserActionRecordService dbUserActionRecordService;

    /**
     * filter中检查角色是否具有url的权限
     *
     * @return
     */
    public boolean hasPermit(User user, String path) {
        if (ServerConfigKeys.ADMINISTRATOR_ROLE_ID == user.getRoleId()) {
            return true;
        }
        return doHasPermit(user, path);
    }

    /**
     * 获取权限列表
     *
     * @return
     */
    public List<RolePermit> getPermitListByRoleId(Integer roleId) {
        return rolePermitService.getListByRoleId(roleId);
    }

    /**
     * 添加权限
     *
     * @param roleId   角色id
     * @param permitId 权限Id
     */
    public void addRolePermit(Integer roleId, String permitId) {
        rolePermitService.createPermitItem(new RolePermit(roleId.toString(), permitId));
    }

    /**
     * 去掉某个角色的权限
     *
     * @param roleId   角色id
     * @param permitId 权限Id
     */
    public void removeRolePermit(Integer roleId, String permitId) {
        rolePermitService.removePermitItem(new RolePermit(roleId.toString(), permitId));
    }

    public boolean doHasPermit(User user, String permitItem) {
        return rolePermitService.hasPermit(user, permitItem);
    }
	
	/*public boolean hasPermit(DBUser user, String permitid) {
		DBPermitItem dbPermitItem = dbPermitItemService.getPermitById(permitid);
		if(dbPermitItem != null){
			return doHasPermit(username, dbPermitItem.getUrl());			
		}else{
			return true;
		}
	}*/

    /*public void createUserActionRecord(DBUserActionRecord userActionRecord) {
        dbUserActionRecordService.createUserActionRecord(userActionRecord);
    }

    public void createUserActionRecord(String username, String pathInfo, String host, String addr) {
        createUserActionRecord(username, pathInfo, null, host, addr);
    }

    public void createUserActionRecord(String username, String pathInfo, String param, String host, String addr) {
        DBPermitItem permitItem = dbPermitItemService.getPermitByUri(pathInfo);
        if (permitItem != null && "ACTION".equals(permitItem.getItemType()) && !"SELECT".equals(permitItem.getOperateType())) {
            DBUserActionRecord record = new DBUserActionRecord(username, permitItem.getId(), param);
            record.setHost(host);
            record.setAddr(addr);
            dbUserActionRecordService.createUserActionRecord(record);
        }
    }*/
}
