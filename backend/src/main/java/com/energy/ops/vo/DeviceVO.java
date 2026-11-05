package com.energy.ops.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "设备列表返回结构")
public class DeviceVO {

    private Long id;
    private String code;
    private String name;
    private Long typeId;
    private String typeName;
    private String station;
    private Integer status;
    private String statusText;
    private LocalDate installDate;
    private LocalDateTime createTime;
}
