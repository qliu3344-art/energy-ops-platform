package com.energy.ops.controller;

import com.energy.ops.common.PageResult;
import com.energy.ops.common.Result;
import com.energy.ops.service.InspectionPlanService;
import com.energy.ops.vo.InspectionPlanVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "巡检计划")
@RestController
@RequestMapping("/api/inspection-plans")
@RequiredArgsConstructor
public class InspectionPlanController {

    private final InspectionPlanService inspectionPlanService;

    @Operation(summary = "分页查询巡检计划")
    @GetMapping
    public Result<PageResult<InspectionPlanVO>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Long deviceId) {
        return Result.success(inspectionPlanService.page(page, size, deviceId));
    }
}
