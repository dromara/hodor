package org.dromara.hodor.admin.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.core.Status;
import org.dromara.hodor.admin.domain.RolePermit;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.service.impl.RolePermitService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author tomgs
 * 
 * @since 1.0
 **/
@Controller
@RequestMapping("/service/competence")
@RequiredArgsConstructor
public class RoleCompetenceController {

    private final RolePermitService rolePermitService;

    @RequestMapping("/updateOrSaveCompetence")
    @ResponseBody
    public Result<Void> auth(@RequestParam(value = "roleId", required = true) Integer roleId,
                       @RequestParam(value = "items", required = false) String[] items) throws Exception {
        User user = new User();
        user.setRoleId(roleId);
        if(items.length >0){
            //先删除，后插入
            rolePermitService.removeByRoleId(roleId);
            for (String item : items) {
                RolePermit rolePermit = new RolePermit();
                rolePermit.setPermitIterm(item.replace("\"","").replace("[","").replace("]",""));
                rolePermit.setRoleId(roleId.toString());
                rolePermitService.createPermitItem(rolePermit);
            }
            return ResultUtil.success();
        }
        return ResultUtil.error(Status.INTERNAL_SERVER_ERROR_ARGS);
    }

}
