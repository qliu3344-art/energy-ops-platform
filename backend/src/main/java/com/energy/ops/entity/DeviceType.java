package com.energy.ops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 设备类型字典
 */
@Data
@TableName("device_type")
public class DeviceType {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    private String name;
}
