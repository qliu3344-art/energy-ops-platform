package com.energy.ops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备告警
 */
@Data
@TableName("alarm")
public class Alarm {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long deviceId;

    /** 1 提示 / 2 警告 / 3 严重 */
    private Integer level;

    private String content;

    /** 0 未处理 / 1 已处理 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime handleTime;
}
