package org.dromara.hodor.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.core.UserContext;
import org.dromara.hodor.admin.domain.UserInfo;
import org.dromara.hodor.admin.service.ActuatorOperatorService;
import org.dromara.hodor.model.actuator.ActuatorInfo;
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
@RequiredArgsConstructor
public class ActuatorController {

    private final ActuatorOperatorService actuatorOperatorService;

    @Operation(summary = "获取所有的actuator cluster")
    @GetMapping("/clusterNames")
    public Result<List<String>> getAllClusters() throws Exception {
        List<String> allClusters = actuatorOperatorService.allClusters();
        return ResultUtil.success(allClusters);
    }

    @Operation(summary = "获取可用执行器详细信息")
    @GetMapping("/list")
    public Result<List<ActuatorInfo>> getActuatorApps() throws Exception {
        final UserInfo user = UserContext.getUser();
        final List<ActuatorInfo> actuatorClusterInfos = actuatorOperatorService.getActuatorClusterInfos();
        return ResultUtil.success(actuatorClusterInfos);
    }

    @Operation(summary = "获取根据指定name的执行器详细信息")
    @GetMapping("/info")
    public Result<List<ActuatorInfo>> getActuatorByName(@RequestParam String name) throws Exception {
        final List<ActuatorInfo> actuatorInfos = actuatorOperatorService.getActuatorByName(name);
        return ResultUtil.success(actuatorInfos);
    }

}
