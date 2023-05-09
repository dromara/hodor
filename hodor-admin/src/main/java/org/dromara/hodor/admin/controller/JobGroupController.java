package org.dromara.hodor.admin.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dromara.hodor.admin.core.PageInfo;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.core.ServerConfigKeys;
import org.dromara.hodor.admin.core.Status;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.service.JobGroupService;
import org.dromara.hodor.core.entity.JobGroup;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class JobGroupController {

    private final JobGroupService jobGroupService;

    @PostMapping("/create")
    public Result<JobGroup> createGroup(@RequestAttribute(value = ServerConfigKeys.USER_SESSION) User user,
                                        @RequestBody JobGroup group) {
        JobGroup jobGroup = jobGroupService.createGroup(user, group);
        return ResultUtil.success(jobGroup);
    }

    @GetMapping("/list")
    public Result<List<JobGroup>> list(@RequestAttribute(value = ServerConfigKeys.USER_SESSION) User user) {
        final List<JobGroup> allGroup = jobGroupService.getAllGroup(user);
        return ResultUtil.success(allGroup);
    }

    @GetMapping("/listPage")
    public Result<PageInfo<JobGroup>> queryGroupListPaging(@RequestParam(value = "queryVal", required = false) String queryVal,
                                                           @RequestParam(value = "pageNo") Integer pageNo,
                                                           @RequestParam(value = "pageSize") Integer pageSize,
                                                           @RequestAttribute(value = ServerConfigKeys.USER_SESSION) User user) {
        PageInfo<JobGroup> pageInfo = jobGroupService.queryGroupListPaging(user, queryVal, pageNo, pageSize);
        return ResultUtil.success(pageInfo);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@RequestAttribute(value = ServerConfigKeys.USER_SESSION) User user,
                               @PathVariable(value = "id") int id,
                               @RequestBody JobGroup group) {
        jobGroupService.updateJobGroup(user, id, group);
        return ResultUtil.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestAttribute(value = ServerConfigKeys.USER_SESSION) User user,
                               @PathVariable(value = "id") int id) {
        jobGroupService.deleteJobGroup(user, id);
        return ResultUtil.errorWithArgs(Status.INTERNAL_SERVER_ERROR_ARGS, "group暂不支持删除");
    }

}
