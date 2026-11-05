package com.energy.ops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备台账
 */
@Data
@TableName("device")
public class Device {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 设备编码，一物一码 */
    private String code;

    private String name;

    /** 设备类型 id，关联 device_type */
    private Long typeId;

    /** 所属场站 */
    private String station;

    /** 0 停用 / 1 运行 / 2 故障 */
    private Integer status;

    private LocalDate installDate;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
