package com.energy.ops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 巡检 / 维修工单（对应 work_order 表）
 *
 * <p>状态是这张表的核心：0→1→2→3→4 只能相邻推进，
 * 每次变更都会在 work_order_log 里留一条记录。
 */
@Data
@TableName("work_order")
public class WorkOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 工单号，如 WO1756800000000 */
    private String code;

    /** 关联的设备 */
    private Long deviceId;

    /** 1 巡检 / 2 维修 */
    private Integer type;

    /** 0 待派发 / 1 已派发 / 2 执行中 / 3 已完成 / 4 已验收 */
    private Integer status;

    /** 负责人 */
    private String assignee;

    /** 故障或巡检说明 */
    private String description;

    private LocalDateTime createTime;

    /** 完成时间，流转到 3（已完成）时写入 */
    private LocalDateTime finishTime;
}
