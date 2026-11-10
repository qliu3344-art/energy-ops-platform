package com.energy.ops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 巡检计划（对应 inspection_plan 表）
 *
 * <p>描述「哪台设备、多少天巡检一次、下次什么时候巡检」，
 * 是工单的上游——计划到期后应该产生一张巡检工单。
 */
@Data
@TableName("inspection_plan")
public class InspectionPlan {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联的设备 */
    private Long deviceId;

    /** 计划名称，如「月度例行巡检」 */
    private String name;

    /** 巡检周期（天） */
    private Integer cycleDays;

    /** 下次巡检时间 */
    private LocalDateTime nextTime;

    /** 1 启用 / 0 停用 */
    private Integer status;

    private LocalDateTime createTime;
}
