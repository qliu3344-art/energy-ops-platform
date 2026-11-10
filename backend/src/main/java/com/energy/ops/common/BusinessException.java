package com.energy.ops.common;

import lombok.Getter;

/**
 * 业务异常：可以安全地把 message 返回给前端
 *
 * <p>继承 {@link RuntimeException}（非受检异常）而不是 Exception，
 * 这样 Service 层抛出时方法签名不用写 throws，调用链也能一路往上冒泡，
 * 最后由 {@link GlobalExceptionHandler} 统一接住。
 */
@Getter
public class BusinessException extends RuntimeException {

    // 业务状态码：400 参数/业务错误，401 未登录，404 资源不存在
    private final int code;

    /** 只传消息时默认按 400 处理。 */
    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
