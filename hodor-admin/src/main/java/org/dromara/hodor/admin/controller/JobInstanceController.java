package org.dromara.hodor.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.service.ActuatorOperatorService;
import org.dromara.hodor.admin.service.LogService;
import org.dromara.hodor.client.model.KillJobRequest;
import org.dromara.hodor.client.model.KillJobResult;
import org.dromara.hodor.client.model.LogQueryRequest;
import org.dromara.hodor.client.model.LogQueryResult;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.core.entity.JobExecDetail;
import org.dromara.hodor.core.service.JobExecDetailService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * job exec detail reporter controller
 *
 * @author tomgs
 * @since 1.0
 */
@Tag(name = "任务执行明细管理")
@RestController
@RequestMapping("jobExecDetail")
@RequiredArgsConstructor
public class JobInstanceController {

    private final JobExecDetailService jobExecDetailService;

    private final LogService logService;

    private final ActuatorOperatorService actuatorOperatorService;

    @Operation(summary = "分页查询任务执行明细")
    @GetMapping
    public Result<PageInfo<JobExecDetail>> queryByPage(JobExecDetail jobExecDetail,
                                                 @RequestParam(value = "pageNo") Integer pageNo,
                                                 @RequestParam(value = "pageSize") Integer pageSize) {
        PageInfo<JobExecDetail> pageInfo = jobExecDetailService.queryByPage(jobExecDetail, pageNo, pageSize);
        return ResultUtil.success(pageInfo);
    }

    @Operation(summary = "根据id查询任务执行明细")
    @GetMapping("{id}")
    public Result<JobExecDetail> queryById(@PathVariable("id") Long id) {
        return ResultUtil.success(jobExecDetailService.queryById(id));
    }

    @Operation(summary = "更新任务执行明细")
    @PutMapping
    public Result<JobExecDetail> update(@RequestBody JobExecDetail jobExecDetail) {
        jobExecDetailService.update(jobExecDetail);
        return ResultUtil.success();
    }

    @Operation(summary = "删除指定任务执行明细")
    @DeleteMapping
    public Result<Boolean> deleteById(Long id) {
        return ResultUtil.success(jobExecDetailService.deleteById(id));
    }

    @Operation(summary = "任务执行日志查看")
    @GetMapping("/logs")
    public Result<LogQueryResult> queryLog(LogQueryRequest request) throws Exception {
        return ResultUtil.success(logService.queryLog(request));
    }

    @Operation(summary = "杀死正在执行的任务")
    @PutMapping("/kill")
    public Result<KillJobResult> killRunningJob(@RequestBody KillJobRequest killJobRequest) throws Exception {
        return ResultUtil.success(actuatorOperatorService.killRunningJob(killJobRequest));
    }

}
