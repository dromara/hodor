package org.dromara.hodor.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.domain.UserGroups;
import org.dromara.hodor.admin.service.UserGroupsService;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.core.entity.JobGroup;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserGroupsController
 *
 * @author tomgs
 * @since 1.0
 */
@Tag(name = "用户-分组接口管理")
@RestController
@RequestMapping("/userGroups")
@RequiredArgsConstructor
public class UserGroupsController {

    private final UserGroupsService userGroupsService;

    @Operation(summary = "通过用户Id查询关联分组")
    @GetMapping("/{userId}")
    public Result<PageInfo<JobGroup>> queryByUserId(@PathVariable("userId") Long userId,
                                                    @RequestParam(value = "pageNo") Integer pageNo,
                                                    @RequestParam(value = "pageSize") Integer pageSize) {
        return ResultUtil.success(userGroupsService.queryByUserId(userId, pageNo, pageSize));
    }

    @Operation(summary = "关联用户Id与分组")
    @PostMapping
    public Result<UserGroups> add(UserGroups userGroups) {
        return ResultUtil.success(userGroupsService.insert(userGroups));
    }

    @Operation(summary = "删除对应关联")
    @DeleteMapping
    public Result<Boolean> deleteByUserIdAndGroupId(UserGroups userGroups) {
        return ResultUtil.success(userGroupsService.deleteByUserIdAndGroupId(userGroups));
    }

}

