package org.dromara.hodor.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.hodor.admin.core.MsgCode;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.core.UserContext;
import org.dromara.hodor.admin.entity.User;
import org.dromara.hodor.admin.service.ActuatorOperatorService;
import org.dromara.hodor.admin.service.JobGroupService;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.core.entity.JobGroup;
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
 * 任务分组管理
 *
 * @author tomgs
 * @since 1.0
 */
@Tag(name = "任务分组管理")
@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class JobGroupController {

    private final JobGroupService jobGroupService;

    private final ActuatorOperatorService actuatorOperatorService;

    @Operation(summary = "创建分组")
    @PostMapping()
    public Result<JobGroup> createGroup(@RequestBody JobGroup group) {
        final User user = UserContext.getUser();
        JobGroup jobGroup = jobGroupService.createGroup(user, group);
        return ResultUtil.success(jobGroup);
    }

    @Operation(summary = "分页查询分组信息")
    @GetMapping()
    public Result<PageInfo<JobGroup>> queryGroupListPaging(@RequestParam(value = "groupName", required = false) String groupName,
                                                           @RequestParam(value = "pageNo") Integer pageNo,
                                                           @RequestParam(value = "pageSize") Integer pageSize) {
        final User user = UserContext.getUser();
        PageInfo<JobGroup> pageInfo = jobGroupService.queryGroupListPaging(user, groupName, pageNo, pageSize);
        return ResultUtil.success(pageInfo);
    }

    @Operation(summary = "id查询分组")
    @GetMapping("{id}")
    public Result<JobGroup> queryById(@PathVariable("id") Long id) {
        return ResultUtil.success(jobGroupService.queryById(id));
    }

    @Operation(summary = "更新分组信息")
    @PutMapping()
    public Result<Void> update(@RequestBody JobGroup group) {
        final User user = UserContext.getUser();
        jobGroupService.updateJobGroup(user, group);
        return ResultUtil.success();
    }

    @Operation(summary = "删除分组信息")
    @DeleteMapping()
    public Result<Void> delete(@RequestParam(value = "id") int id) {
        final User user = UserContext.getUser();
        jobGroupService.deleteJobGroup(user, id);
        return ResultUtil.errorWithArgs(MsgCode.INTERNAL_SERVER_ERROR, "group暂不支持删除");
    }

    @Operation(summary = "绑定执行集群")
    @PostMapping("/bindActuator")
    public Result<Void> bindActuatorCluster(@RequestParam String clusterName, @RequestParam String group) throws Exception {
        final User user = UserContext.getUser();
        actuatorOperatorService.binding(clusterName, group);
        return ResultUtil.success();
    }

    @Operation(summary = "解绑执行集群")
    @PostMapping("/unbindActuator")
    public Result<Void> unbindActuatorCluster(@RequestParam String clusterName, @RequestParam String group) throws Exception {
        final User user = UserContext.getUser();
        actuatorOperatorService.unbinding(clusterName, group);
        return ResultUtil.success();
    }
}
