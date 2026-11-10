package com.energy.ops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备告警（对应 alarm 表）
 *
 * <p>设备上报异常时写一条记录；处理后置为已处理并记录处理时间，
 * 需要维修时可以从告警转成工单，形成闭环。
 */
@Data
@TableName("alarm")
public class Alarm {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 触发告警的设备 */
    private Long deviceId;

    /** 1 提示 / 2 警告 / 3 严重 */
    private Integer level;

    /** 告警内容，如「箱变温度 85℃ 超过阈值」 */
    private String content;

    /** 0 未处理 / 1 已处理 */
    private Integer status;

    private LocalDateTime createTime;

    /** 处理时间，标记已处理时写入 */
    private LocalDateTime handleTime;
}
