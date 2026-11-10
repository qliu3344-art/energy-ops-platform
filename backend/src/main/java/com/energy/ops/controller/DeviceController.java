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

/**
 * 设备台账接口。
 *
 * <p>接口按「资源」划分：/api/devices 这个资源，用 HTTP 方法表达动作——
 * GET 查、POST 增、PUT 改、DELETE 删，这就是 RESTful 的基本约定。
 *
 * <p>这一层只做三件事：接参数、调用 Service、包统一返回，不含任何业务判断。
 */
@Tag(name = "设备台账")
@RestController          // = @Controller + @ResponseBody，返回值直接序列化成 JSON
@RequestMapping("/api/devices")   // 这个类下所有接口的公共前缀
@RequiredArgsConstructor          // 为 final 字段生成构造器，Spring 用它做构造器注入
public class DeviceController {

    private final DeviceService deviceService;

    /**
     * 分页查询。参数从 URL 的查询串里取：?page=1&size=10&keyword=风机
     *
     * <p>{@code @RequestParam} 用于 ?key=value 形式；
     * 路径里的 /devices/5 用 {@code @PathVariable}；
     * 请求体里的 JSON 用 {@code @RequestBody}。
     */
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

    /**
     * 新增设备。{@code @Valid} 触发 DTO 上的字段校验，
     * 不通过会抛 MethodArgumentNotValidException，由全局异常处理返回 400。
     */
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
