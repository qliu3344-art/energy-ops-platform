package com.energy.ops.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备列表返回结构（VO）。
 *
 * <p>VO 是「出参」：只放前端要显示的字段。比 Entity 多两个字段——
 * typeName 由 SQL 的 LEFT JOIN 带出来，statusText 由 Service 翻译，
 * 这样前端不用再查一次类型表、也不用维护状态码映射。
 */
@Data
@Schema(description = "设备列表返回结构")
public class DeviceVO {

    private Long id;

    private String code;

    private String name;

    /** 类型 id，前端编辑时回填下拉框 */
    private Long typeId;

    /** 类型名称，来自 device_type 表（JOIN 得到） */
    private String typeName;

    private String station;

    /** 状态码：0 停用 / 1 运行 / 2 故障 */
    private Integer status;

    /** 状态中文：Service 用 DictText 翻译后填进来 */
    private String statusText;

    private LocalDate installDate;

    private LocalDateTime createTime;
}
