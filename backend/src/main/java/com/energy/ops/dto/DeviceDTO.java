package com.energy.ops.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 设备新增 / 修改的入参对象（DTO）。
 *
 * <p>为什么不直接用 Entity 接参数：Entity 里有 id、createTime 这些
 * 不该让前端控制的字段。DTO 只暴露允许前端传的部分，
 * 配合 {@code @Valid} 在进入业务逻辑之前就把非法数据挡掉。
 */
@Data
@Schema(description = "设备新增 / 修改参数")
public class DeviceDTO {

    // @NotBlank：不能为 null、空串或纯空格；@Size：长度上限
    @NotBlank(message = "设备编码不能为空")
    @Size(max = 64, message = "设备编码不能超过 64 位")
    @Schema(description = "设备编码", example = "DEV-001")
    private String code;

    @NotBlank(message = "设备名称不能为空")
    @Size(max = 100, message = "设备名称不能超过 100 字")
    @Schema(description = "设备名称", example = "1号主变压器")
    private String name;

    // @NotNull：对象类型只校验非空，不校验空串
    @NotNull(message = "设备类型不能为空")
    @Schema(description = "设备类型 id", example = "1")
    private Long typeId;

    @NotBlank(message = "所属场站不能为空")
    @Schema(description = "所属场站", example = "雄安一号光伏电站")
    private String station;

    @Schema(description = "状态：0 停用 / 1 运行 / 2 故障", example = "1")
    private Integer status;

    @Schema(description = "投运日期", example = "2024-06-01")
    private LocalDate installDate;
}
