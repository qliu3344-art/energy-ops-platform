package com.energy.ops.controller;

import com.energy.ops.common.PageResult;
import com.energy.ops.common.Result;
import com.energy.ops.dto.WorkOrderDTO;
import com.energy.ops.dto.WorkOrderStatusDTO;
import com.energy.ops.service.WorkOrderService;
import com.energy.ops.vo.WorkOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 巡检工单接口。
 *
 * <p>除了常规的增查，最核心的是状态流转接口：PUT /api/work-orders/{id}/status。
 * 它的输入是「目标状态」，由 Service 判断这一步是否合法。
 */
@Tag(name = "巡检工单")
@RestController
@RequestMapping("/api/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @Operation(summary = "分页查询工单")
    @GetMapping
    public Result<PageResult<WorkOrderVO>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String keyword) {
        return Result.success(workOrderService.page(page, size, status, type, keyword));
    }

    @Operation(summary = "创建工单")
    @PostMapping
    public Result<Long> create(@RequestBody @Valid WorkOrderDTO dto) {
        return Result.success(workOrderService.create(dto));
    }

    /**
     * 工单状态流转：把工单推进到目标状态。
     * 这里是「动作型」接口（/status），不是纯资源型——动作语义用 PUT 比 POST 更合适。
     */
    @Operation(summary = "工单状态流转（带事务）")
    @PutMapping("/{id}/status")
    public Result<Void> transition(@PathVariable Long id, @RequestBody @Valid WorkOrderStatusDTO dto) {
        workOrderService.transition(id, dto);
        return Result.success();
    }
}
