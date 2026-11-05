package com.energy.ops.controller;

import com.energy.ops.common.PageResult;
import com.energy.ops.common.Result;
import com.energy.ops.dto.DeviceDTO;
import com.energy.ops.service.DeviceService;
import com.energy.ops.vo.DeviceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "设备台账")
@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @Operation(summary = "分页查询设备")
    @GetMapping
    public Result<PageResult<DeviceVO>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Integer status) {
        return Result.success(deviceService.page(page, size, keyword, typeId, status));
    }

    @Operation(summary = "查询设备详情")
    @GetMapping("/{id}")
    public Result<DeviceVO> detail(@PathVariable Long id) {
        return Result.success(deviceService.getById(id));
    }

    @Operation(summary = "新增设备")
    @PostMapping
    public Result<Long> create(@RequestBody @Valid DeviceDTO dto) {
        return Result.success(deviceService.create(dto));
    }

    @Operation(summary = "修改设备")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid DeviceDTO dto) {
        deviceService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除设备")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        deviceService.delete(id);
        return Result.success();
    }
}
