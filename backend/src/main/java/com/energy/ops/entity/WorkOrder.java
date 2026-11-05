package com.energy.ops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 巡检 / 维修工单
 */
@Data
@TableName("work_order")
public class WorkOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    private Long deviceId;

    /** 1 巡检 / 2 维修 */
    private Integer type;

    /** 0 待派发 / 1 已派发 / 2 执行中 / 3 已完成 / 4 已验收 */
    private Integer status;

    private String assignee;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime finishTime;
}
