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

@Tag(name = "登录认证")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;

    @Operation(summary = "登录，返回令牌")
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody @Valid LoginDTO dto) {
        String token = tokenService.login(dto.getUsername(), dto.getPassword());
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("username", dto.getUsername());
        return Result.success(data);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String header) {
        if (header != null) {
            tokenService.logout(header.startsWith("Bearer ") ? header.substring(7) : header);
        }
        return Result.success();
    }
}
