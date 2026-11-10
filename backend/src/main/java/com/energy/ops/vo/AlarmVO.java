package com.energy.ops.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 告警列表返回结构。
 *
 * <p>deviceName 从设备表 JOIN 出来；levelText / statusText 由 Service 翻译。
 * 告警记录本身只存 deviceId，前端要显示设备名。
 */
@Data
@Schema(description = "告警返回结构")
public class AlarmVO {

    private Long id;

    private Long deviceId;

    /** 设备名称（JOIN device 表得到） */
    private String deviceName;

    /** 等级码：1 提示 / 2 警告 / 3 严重 */
    private Integer level;

    /** 等级中文 */
    private String levelText;

    private String content;

    /** 处理状态：0 未处理 / 1 已处理 */
    private Integer status;

    /** 处理状态中文 */
    private String statusText;

    private LocalDateTime createTime;

    private LocalDateTime handleTime;
}
