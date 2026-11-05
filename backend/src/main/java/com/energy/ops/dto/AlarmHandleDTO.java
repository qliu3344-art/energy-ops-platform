package com.energy.ops.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "告警处理参数")
public class AlarmHandleDTO {

    @Size(max = 200, message = "处理意见不能超过 200 字")
    @Schema(description = "处理意见", example = "已更换温度传感器")
    private String remark;
}
