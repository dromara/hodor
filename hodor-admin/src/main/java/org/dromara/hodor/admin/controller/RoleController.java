package org.dromara.hodor.admin.controller;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hodor.admin.core.PageInfo;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.core.Status;
import org.dromara.hodor.admin.domain.RolePermit;
import org.dromara.hodor.admin.domain.UserRole;
import org.dromara.hodor.admin.service.impl.RolePermitService;
import org.dromara.hodor.admin.service.impl.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * RoleController
 *
 * @author tomgs
 * @since 1.0
 **/
@RequiredArgsConstructor
@Controller
@RequestMapping("/service/role")
public class RoleController {

    private final RoleService roleService;

    private final RolePermitService rolePermitService;

    @RequestMapping("/addRole")
    @ResponseBody
    public Result<Void> auth(@RequestParam(value = "roleName", required = true) String roleName,
                             @RequestParam(value = "description", required = false) String description) {
        if (!roleService.ifExisted(roleName)) {
            UserRole role = new UserRole();
            role.setRoleName(roleName);
            role.setDescription(StringUtils.isNotEmpty(description) ? description : "");
            roleService.insert(role);
        } else {
            return ResultUtil.errorWithArgs(Status.REQUEST_BAD, "角色名重复");
        }
        return ResultUtil.success();
    }

    @RequestMapping("/deleteRole")
    @ResponseBody
    public Result<Void> deleteRole(@RequestParam(value = "id") Integer id) {
        if (null == id) {
            return ResultUtil.errorWithArgs(Status.REQUEST_BAD, "删除条件不可为空");
        }
        roleService.deleteById(id);
        //删除角色相应权限信息
        rolePermitService.removeByRoleId(id);
        return ResultUtil.success();
    }

    @RequestMapping("/getRoleList")
    @ResponseBody
    public Result<PageInfo<UserRole>> getRoles(@RequestParam(value = "limit", required = false) Integer pageSize,
                                               @RequestParam(value = "offset", required = false) Integer start,
                                               @RequestParam(value = "search", required = false) String roleName) throws Exception {
        Integer pageNum = null;
        if (start != null && pageSize != null) {
            pageNum = start / pageSize + 1;
        }
        PageInfo<UserRole> roles = roleService.getRoleLists(roleName, pageNum, pageSize);
        if (roles.getTotal() > 0) {
            for (UserRole role : roles.getTotalList()) {
                List<String> list = new ArrayList<>();
                for (RolePermit rolePermit : rolePermitService.getListByRoleId(role.getId())) {
                    list.add(rolePermit.getPermitIterm());
                }
                role.setItems(list);
            }
        }
        return ResultUtil.success(roles);
    }

    @RequestMapping("/checkRoleExisted")
    @ResponseBody
    public Result<Boolean> checkRoleExisted(@RequestParam(value = "roleName", required = false) String roleName) throws Exception {
        if (StringUtils.isNotEmpty(roleName)) {
            return ResultUtil.success(roleService.ifExisted(roleName));
        }
        return ResultUtil.success(true);
    }
}
