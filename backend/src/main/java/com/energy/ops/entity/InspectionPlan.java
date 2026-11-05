package com.energy.ops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 巡检计划
 */
@Data
@TableName("inspection_plan")
public class InspectionPlan {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long deviceId;

    private String name;

    /** 巡检周期（天） */
    private Integer cycleDays;

    /** 下次巡检时间 */
    private LocalDateTime nextTime;

    /** 1 启用 / 0 停用 */
    private Integer status;

    private LocalDateTime createTime;
}
