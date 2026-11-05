package com.energy.ops.controller;

import com.energy.ops.common.Result;
import com.energy.ops.entity.DeviceType;
import com.energy.ops.service.DeviceTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "设备类型字典")
@RestController
@RequestMapping("/api/device-types")
@RequiredArgsConstructor
public class DeviceTypeController {

    private final DeviceTypeService deviceTypeService;

    @Operation(summary = "查询全部类型（走 Redis 缓存）")
    @GetMapping
    public Result<List<DeviceType>> list() {
        return Result.success(deviceTypeService.listAll());
    }

    @Operation(summary = "新增类型")
    @PostMapping
    public Result<Long> create(@RequestBody DeviceType type) {
        return Result.success(deviceTypeService.create(type));
    }

    @Operation(summary = "修改类型")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DeviceType type) {
        deviceTypeService.update(id, type);
        return Result.success();
    }

    @Operation(summary = "删除类型")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        deviceTypeService.delete(id);
        return Result.success();
    }
}
