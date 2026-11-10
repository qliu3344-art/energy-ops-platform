package com.energy.ops.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理：Controller 里不再写 try-catch
 *
 * <p>Spring 捕获 Controller 抛出的异常后，会在这里按**异常类型从具体到宽泛**匹配：
 * 先找 BusinessException，再找参数校验异常，最后兜底 Exception。
 * 所以下面那个最宽的 Exception 处理器不会把前两个吞掉。
 *
 * <p>另一个作用：异常堆栈只写日志，不返回前端——避免把表名、类名、SQL 暴露出去。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常：是我们主动抛的，message 可以安全展示。
     * code 由抛出方决定（400 参数错、404 不存在、401 未登录）。
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验失败：@Valid 校验 DTO 不过时抛这个。
     * 把每个字段的提示拼起来返回，前端可以直接弹窗显示。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.error(400, msg);
    }

    /**
     * 兜底：没预料到的异常。
     * 堆栈进日志方便排查，前端只看到一句通用提示。
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统繁忙，请稍后再试");
    }
}
