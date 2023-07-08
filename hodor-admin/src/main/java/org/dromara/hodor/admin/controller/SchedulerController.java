package org.dromara.hodor.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.domain.SchedulerNodeInfo;
import org.dromara.hodor.admin.service.ScheduleOperatorService;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * SchedulerController
 *
 * @author tomgs
 * @version 1.0
 */
@Tag(name = "调度器管理")
@RestController
@RequestMapping("/scheduler")
@RequiredArgsConstructor
public class SchedulerController {

    private final ScheduleOperatorService scheduleOperatorService;

    @Operation(summary = "获取调度节点列表")
    @GetMapping("/list")
    public Result<List<SchedulerNodeInfo>> getSchedulers() throws Exception {
        List<SchedulerNodeInfo> result = scheduleOperatorService.getSchedulers();
        return ResultUtil.success(result);
    }

    @Operation(summary = "查看元数据信息")
    @GetMapping("/metadata")
    public Result<HodorMetadata> getMetadata(@RequestParam(required = false) String endpoint) throws Exception {
        HodorMetadata metadata = scheduleOperatorService.getMetadata(endpoint);
        return ResultUtil.success(metadata);
    }

}
