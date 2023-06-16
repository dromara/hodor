package org.dromara.hodor.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.core.UserContext;
import org.dromara.hodor.admin.domain.ActuatorAppInfo;
import org.dromara.hodor.admin.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ActuatorController
 *
 * @author tomgs
 * @version 1.0
 */
@Tag(name = "任务执行器管理")
@RestController
@RequestMapping("/actuator")
public class ActuatorController {

    @Operation(summary = "获取可用执行器列表")
    @GetMapping("/list")
    public Result<List<ActuatorAppInfo>> getActuatorApps() {
        final User user = UserContext.getUser();
        return ResultUtil.success();
    }

    @Operation(summary = "获取执行器信息")
    @GetMapping("/info")
    public Result<ActuatorAppInfo> getActuatorApp(@RequestParam String appName) {
        final User user = UserContext.getUser();
        return ResultUtil.success();
    }

}
