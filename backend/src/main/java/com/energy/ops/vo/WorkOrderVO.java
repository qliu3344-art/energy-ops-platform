package com.energy.ops.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "工单返回结构")
public class WorkOrderVO {

    private Long id;
    private String code;
    private Long deviceId;
    private String deviceName;
    private Integer type;
    private String typeText;
    private Integer status;
    private String statusText;
    private String assignee;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
}
