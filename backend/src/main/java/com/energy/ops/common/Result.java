package com.energy.ops.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一返回结构：所有接口都返回 {code, msg, data, timestamp}
 *
 * <p>接口一律 HTTP 200，业务结果看 code 字段——前端拦截器只判断一次
 * code 是否为 200，不用每个页面各写一套解析逻辑。
 * 泛型 T 是业务数据的类型，比如 Result&lt;DeviceVO&gt; 或 Result&lt;PageResult&lt;DeviceVO&gt;&gt;。
 */
@Data
@Schema(description = "统一返回结构")
public class Result<T> {

    @Schema(description = "业务状态码，200 表示成功")
    private int code;

    @Schema(description = "提示信息")
    private String msg;

    @Schema(description = "业务数据")
    private T data;

    @Schema(description = "服务器时间戳")
    private long timestamp;

    /** 成功并带数据 */
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.msg = "success";
        r.data = data;
        r.timestamp = System.currentTimeMillis();
        return r;
    }

    /** 成功但无数据（新增、修改、删除这类操作） */
    public static <T> Result<T> success() {
        return success(null);
    }

    /** 失败：code 和 msg 由调用方决定，比如 400 / 401 / 404 / 500 */
    public static <T> Result<T> error(int code, String msg) {
        Result<T> r = new Result<>();
        r.code = code;
        r.msg = msg;
        r.timestamp = System.currentTimeMillis();
        return r;
    }
}
