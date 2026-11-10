package com.energy.ops.auth;

import com.energy.ops.common.Result;
import com.energy.ops.dto.LoginDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录认证接口。
 *
 * <p>登录接口在 {@code WebConfig} 里被排除在拦截器之外——不然没登录就调不了登录。
 */
@Tag(name = "登录认证")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final TokenService tokenService;

    /**
     * 账号密码校验通过后返回令牌，前端把它存进 localStorage，之后每次请求带上。
     */
    @Operation(summary = "登录，返回令牌")
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody @Valid LoginDTO dto) {
        String token = tokenService.login(dto.getUsername(), dto.getPassword());
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("username", dto.getUsername());
        return Result.success(data);
    }

    /**
     * 退出登录：把令牌从 Redis 删掉，这张「票」立刻作废。
     * 令牌可能不存在（重复退出），所以 header 允许为空，有值才处理。
     */
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String header) {
        if (header != null) {
            // 兼容前端带不带 "Bearer " 前缀两种写法
            tokenService.logout(header.startsWith("Bearer ") ? header.substring(7) : header);
        }
        return Result.success();
    }
}
