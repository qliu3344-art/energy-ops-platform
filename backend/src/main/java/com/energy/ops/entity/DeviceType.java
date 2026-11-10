package com.energy.ops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 设备类型字典（对应 device_type 表）
 *
 * <p>字典表的作用：把「风机 / 箱变 / 汇流箱」这类固定选项集中管理，
 * 设备表只存类型 id，避免每个设备都重复写一遍类型名称。
 */
@Data
@TableName("device_type")
public class DeviceType {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 类型编码，如 WIND / BOX */
    private String code;

    /** 类型名称，如「风机」 */
    private String name;
}
