package com.energy.ops.controller;

import com.energy.ops.common.PageResult;
import com.energy.ops.common.Result;
import com.energy.ops.dto.AlarmHandleDTO;
import com.energy.ops.service.AlarmService;
import com.energy.ops.vo.AlarmVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "设备告警")
@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @Operation(summary = "分页查询告警")
    @GetMapping
    public Result<PageResult<AlarmVO>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer level) {
        return Result.success(alarmService.page(page, size, status, level));
    }

    @Operation(summary = "处理告警")
    @PutMapping("/{id}/handle")
    public Result<Void> handle(@PathVariable Long id, @RequestBody @Valid AlarmHandleDTO dto) {
        alarmService.handle(id, dto);
        return Result.success();
    }
}
