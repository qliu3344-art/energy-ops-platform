package com.energy.ops.auth;

import com.energy.ops.common.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * 登录令牌服务：令牌存在 Redis 里，2 小时过期。
 * 服务器本身不保存会话状态，重启后令牌仍然有效（只要 Redis 没挂）。
 */
@Service
@RequiredArgsConstructor
public class TokenService {

    private static final String TOKEN_PREFIX = "auth:token:";
    private static final Duration TOKEN_TTL = Duration.ofHours(2);

    private final StringRedisTemplate redisTemplate;

    public String login(String username, String password) {
        if (!"admin".equals(username) || !"123456".equals(password)) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, username, TOKEN_TTL);
        return token;
    }

    public String getUsername(String token) {
        if (token == null) {
            return null;
        }
        return redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
    }

    public void logout(String token) {
        if (token != null) {
            redisTemplate.delete(TOKEN_PREFIX + token);
        }
    }
}
