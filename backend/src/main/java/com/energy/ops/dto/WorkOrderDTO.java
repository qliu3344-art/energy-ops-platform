package com.energy.ops.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "工单创建参数")
public class WorkOrderDTO {

    @NotNull(message = "设备不能为空")
    @Schema(description = "设备 id", example = "1")
    private Long deviceId;

    @NotNull(message = "工单类型不能为空")
    @Schema(description = "1 巡检 / 2 维修", example = "1")
    private Integer type;

    @Size(max = 32, message = "负责人不能超过 32 字")
    @Schema(description = "负责人", example = "张工")
    private String assignee;

    @Size(max = 500, message = "描述不能超过 500 字")
    @Schema(description = "工单描述")
    private String description;
}
