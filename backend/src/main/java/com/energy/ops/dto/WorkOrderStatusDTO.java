package com.energy.ops.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 工单状态流转入参。
 *
 * <p>前端只传「我想把它推到哪一步」，能不能推由 Service 的状态机判断。
 * 这样规则只存在一个地方，前端改不了也绕不过。
 */
@Data
@Schema(description = "工单状态流转参数")
public class WorkOrderStatusDTO {

    @NotNull(message = "目标状态不能为空")
    @Schema(description = "目标状态：1 已派发 / 2 执行中 / 3 已完成 / 4 已验收", example = "1")
    private Integer toStatus;

    @Schema(description = "操作人", example = "admin")
    private String operator;

    @Size(max = 200, message = "备注不能超过 200 字")
    @Schema(description = "备注")
    private String remark;

    @Size(max = 32, message = "负责人不能超过 32 字")
    @Schema(description = "负责人：派发（0→1）时必填", example = "张工")
    private String assignee;
}
