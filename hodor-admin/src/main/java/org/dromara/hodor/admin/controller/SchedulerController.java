package org.dromara.hodor.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.core.UserContext;
import org.dromara.hodor.admin.domain.SchedulerAppInfo;
import org.dromara.hodor.admin.domain.UserInfo;
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
public class SchedulerController {

    @Operation(summary = "获取调度节点列表")
    @GetMapping("/list")
    public Result<List<SchedulerAppInfo>> getSchedulers() {
        final UserInfo user = UserContext.getUser();
        return ResultUtil.success();
    }

    @Operation(summary = "查看指定节点信息")
    @GetMapping("/info")
    public Result<SchedulerAppInfo> getActuatorApp(@RequestParam String appName) {
        final UserInfo user = UserContext.getUser();
        return ResultUtil.success();
    }

}
