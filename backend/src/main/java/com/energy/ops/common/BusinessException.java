package com.energy.ops.common;

import lombok.Getter;

/**
 * 业务异常：可以安全地把 message 返回给前端
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
