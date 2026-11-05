package com.energy.ops.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "巡检计划返回结构")
public class InspectionPlanVO {

    private Long id;
    private Long deviceId;
    private String deviceName;
    private String name;
    private Integer cycleDays;
    private LocalDateTime nextTime;
    private Integer status;
    private String statusText;
}
