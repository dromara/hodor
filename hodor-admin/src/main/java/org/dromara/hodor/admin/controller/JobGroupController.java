package org.dromara.hodor.admin.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.hodor.admin.core.MsgCode;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.core.UserContext;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.service.JobGroupService;
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
@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class JobGroupController {

    private final JobGroupService jobGroupService;

    @PostMapping()
    public Result<JobGroup> createGroup(@RequestBody JobGroup group) {
        final User user = UserContext.getUser();
        JobGroup jobGroup = jobGroupService.createGroup(user, group);
        return ResultUtil.success(jobGroup);
    }

    @GetMapping()
    public Result<PageInfo<JobGroup>> queryGroupListPaging(@RequestParam(value = "groupName", required = false) String groupName,
                                                           @RequestParam(value = "pageNo") Integer pageNo,
                                                           @RequestParam(value = "pageSize") Integer pageSize) {
        final User user = UserContext.getUser();
        PageInfo<JobGroup> pageInfo = jobGroupService.queryGroupListPaging(user, groupName, pageNo, pageSize);
        return ResultUtil.success(pageInfo);
    }

    @GetMapping("{id}")
    public Result<JobGroup> queryById(@PathVariable("id") Long id) {
        return ResultUtil.success(jobGroupService.queryById(id));
    }

    @PutMapping()
    public Result<Void> update(@RequestBody JobGroup group) {
        final User user = UserContext.getUser();
        jobGroupService.updateJobGroup(user, group);
        return ResultUtil.success();
    }

    @DeleteMapping()
    public Result<Void> delete(@RequestParam(value = "id") int id) {
        final User user = UserContext.getUser();
        jobGroupService.deleteJobGroup(user, id);
        return ResultUtil.errorWithArgs(MsgCode.INTERNAL_SERVER_ERROR, "group暂不支持删除");
    }

}
