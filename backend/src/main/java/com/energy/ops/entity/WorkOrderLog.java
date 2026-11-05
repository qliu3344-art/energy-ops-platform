package com.energy.ops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单状态流转日志：和状态更新在同一个事务里写入
 */
@Data
@TableName("work_order_log")
public class WorkOrderLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Integer fromStatus;

    private Integer toStatus;

    private String operator;

    private String remark;

    private LocalDateTime createTime;
}
