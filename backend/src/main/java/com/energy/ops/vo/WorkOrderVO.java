package com.energy.ops.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单列表返回结构。
 *
 * <p>比 Entity 多了 deviceName（JOIN 设备表）、typeText 和 statusText（中文化），
 * 前端表格可以直接渲染。
 */
@Data
@Schema(description = "工单返回结构")
public class WorkOrderVO {

    private Long id;

    /** 工单号 */
    private String code;

    private Long deviceId;

    /** 设备名称（JOIN 得到），前端看名字比看 id 直观 */
    private String deviceName;

    /** 类型码：1 巡检 / 2 维修 */
    private Integer type;

    private String typeText;

    /** 状态码：0~4，含义见 DictText.orderStatus */
    private Integer status;

    private String statusText;

    private String assignee;

    private String description;

    private LocalDateTime createTime;

    /** 完成时间，状态到 3 时才写入 */
    private LocalDateTime finishTime;
}
