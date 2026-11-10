package com.energy.ops.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 巡检计划列表返回结构。
 *
 * <p>deviceName 由 SQL 联设备表带出，statusText 由 Service 翻译。
 */
@Data
@Schema(description = "巡检计划返回结构")
public class InspectionPlanVO {

    private Long id;

    private Long deviceId;

    /** 设备名称（JOIN 得到） */
    private String deviceName;

    /** 计划名称，如「月度例行巡检」 */
    private String name;

    /** 巡检周期（天） */
    private Integer cycleDays;

    /** 下次巡检时间 */
    private LocalDateTime nextTime;

    /** 1 启用 / 0 停用 */
    private Integer status;

    private String statusText;
}
