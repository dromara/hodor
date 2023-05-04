package org.dromara.hodor.admin.controller;

import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.domain.PermitItem;
import org.dromara.hodor.admin.domain.RolePermit;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.service.impl.PermitItemService;
import org.dromara.hodor.admin.service.impl.PermitService;
import org.dromara.hodor.admin.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/service/permit")
public class PermitController {

	@Autowired
	private UserService userService;
	
	@Resource
	private PermitItemService permitItemService;
	
	@Autowired
	private PermitService permitService;

	/**
	*  废弃，授权方式不够友好
	 */
	/*@RequestMapping("/auth")
	@ResponseBody
	public Result auth(@RequestParam(value = "permititem", required = true) String permititem,
			@RequestParam(value = "roleId", required = true) int roleId) throws Exception {
		permitService.addRolePermit(roleId, permititem);
		return new Result();
	}

	@RequestMapping("/unAuth")
	@ResponseBody
	public Result unAuth(@RequestParam(value = "permititem", required = true) String permititem,
			@RequestParam(value = "roleId", required = true) int roleId) throws Exception {
		permitService.removeRolePermit(roleId, permititem);
		return new Result();
	}*/

	
	@RequestMapping("/getUserList")
	@ResponseBody
	public List<String> getUserList() throws Exception {
		return userService.getAllUsername();
	}
	
	@RequestMapping("/getPermitList")
	@ResponseBody
	public List<PermitItem> getPermitList() throws Exception {
		return permitItemService.getAllPermit();
	}
	
	@RequestMapping("/getPermitInfo")
	@ResponseBody
	public boolean getPermitInfo(@RequestParam(value = "roleId", required = true) int roleId,
	                             @RequestParam(value = "permititem", required = true) String permititem) throws Exception {
		User user = new User();
		user.setRoleId(roleId);
		return permitService.hasPermit(user, permititem);
	}

	@RequestMapping("/getRolePermitInfo")
	@ResponseBody
	public Result getRolePermitInfo(@RequestParam(value = "roleId", required = true) int roleId) throws Exception {
		List<RolePermit> list = permitService.getPermitListByRoleId(roleId);
		List<String> items = new ArrayList<>();
		for (RolePermit rolePermit : list) {
			items.add(rolePermit.getPermitIterm());
		}
		return Result.success(items);
	}
	
}
