package com.energy.ops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备台账（对应数据库 device 表）
 *
 * <p>Entity 只负责「一行数据长什么样」，不放业务逻辑。
 * 字段名用驼峰，数据库列名用下划线，靠 MyBatis-Plus 的
 * map-underscore-to-camel-case 自动对应（type_id ↔ typeId）。
 */
@Data                    // Lombok：自动生成 getter/setter/toString/equals
@TableName("device")     // 显式指定表名，避免类名和表名不一致时找不到
public class Device {

    @TableId(type = IdType.AUTO)   // 主键由数据库自增生成，insert 后自动回填
    private Long id;

    /** 设备编码，一物一码，数据库里建了唯一索引 */
    private String code;

    /** 设备名称，如「1 号风机」 */
    private String name;

    /** 设备类型 id，关联 device_type */
    private Long typeId;

    /** 所属场站 */
    private String station;

    /** 0 停用 / 1 运行 / 2 故障 */
    private Integer status;

    /** 投运日期，只有日期没有时间 */
    private LocalDate installDate;

    /** 创建时间，由数据库默认值填充 */
    private LocalDateTime createTime;

    /** 最后修改时间 */
    private LocalDateTime updateTime;
}
