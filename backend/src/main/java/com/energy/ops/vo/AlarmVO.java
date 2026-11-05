package com.energy.ops.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "告警返回结构")
public class AlarmVO {

    private Long id;
    private Long deviceId;
    private String deviceName;
    private Integer level;
    private String levelText;
    private String content;
    private Integer status;
    private String statusText;
    private LocalDateTime createTime;
    private LocalDateTime handleTime;
}
