package org.dromara.hodor.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.service.UserService;
import org.dromara.hodor.core.PageInfo;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserController
 *
 * @author tomgs
 * @since 1.0
 */
@Tag(name = "用户信息接口管理")
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户列表",
        parameters = {
            @Parameter(name = "user", description = "用户筛选条件"),
            @Parameter(name = "pageNo", description = "第几页"),
            @Parameter(name = "pageSize", description = "分页大小")
        })
    @GetMapping
    public Result<PageInfo<User>> queryByPage(@RequestBody User user,
                                              @RequestParam(value = "pageNo") Integer pageNo,
                                              @RequestParam(value = "pageSize") Integer pageSize) {
        PageInfo<User> pageInfo = userService.queryByPage(user, pageNo, pageSize);
        return ResultUtil.success(pageInfo);
    }

    @Operation(summary = "通过用户Id查询")
    @GetMapping("{id}")
    public Result<User> queryById(@PathVariable("id") Long id) {
        return ResultUtil.success(userService.queryById(id));
    }

    @Operation(summary = "创建用户")
    @PostMapping
    public Result<User> add(@RequestBody User user) {
        return ResultUtil.success(userService.insert(user));
    }

    @Operation(summary = "更新用户")
    @PutMapping
    public Result<User> update(@RequestBody User user) {
        return ResultUtil.success(userService.update(user));
    }

    @Operation(summary = "删除用户")
    @DeleteMapping
    public Result<Boolean> deleteById(Long id) {
        return ResultUtil.success(userService.deleteById(id));
    }

}

