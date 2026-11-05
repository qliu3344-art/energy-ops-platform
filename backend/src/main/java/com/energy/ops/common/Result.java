package com.energy.ops.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一返回结构：所有接口都返回 {code, msg, data, timestamp}
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

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.msg = "success";
        r.data = data;
        r.timestamp = System.currentTimeMillis();
        return r;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(int code, String msg) {
        Result<T> r = new Result<>();
        r.code = code;
        r.msg = msg;
        r.timestamp = System.currentTimeMillis();
        return r;
    }
}
