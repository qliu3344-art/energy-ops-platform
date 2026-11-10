package com.energy.ops.auth;

import com.energy.ops.common.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * 登录令牌服务：令牌存在 Redis 里，2 小时空闲过期。
 * 每次请求校验通过都会续期，所以连续操作不会掉线，闲置满 2 小时才失效。
 * 服务器本身不保存会话状态，重启后令牌仍然有效（只要 Redis 没挂）。
 */
@Service
@RequiredArgsConstructor
public class TokenService {

    // key 形如 auth:token:3f9a1c...，值是这个令牌对应的用户名
    private static final String TOKEN_PREFIX = "auth:token:";
    private static final Duration TOKEN_TTL = Duration.ofHours(2);

    private final StringRedisTemplate redisTemplate;

    /**
     * 登录：校验账号密码，生成随机令牌并写入 Redis。
     *
     * <p>这里把账号密码写死是为了演示方便。真实项目会去用户表比对密码哈希。
     */
    public String login(String username, String password) {
        if (!"admin".equals(username) || !"123456".equals(password)) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        // UUID 去掉横线后是 32 位十六进制字符串，重复概率可以忽略
        String token = UUID.randomUUID().toString().replace("-", "");
        // 令牌 → 用户名，设置 2 小时过期；期间每次请求会续期，闲置满 2 小时才自动删除
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, username, TOKEN_TTL);
        return token;
    }

    /**
     * 用令牌换用户名。返回 null 表示令牌无效或已过期。
     */
    public String getUsername(String token) {
        if (token == null) {
            return null;
        }
        return redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
    }

    /**
     * 续期：校验通过后把过期时间重新推后 2 小时。
     * 这样 2 小时是「空闲过期」——只要一直在操作，就不会因为登录满 2 小时被打断。
     */
    public void touch(String token) {
        redisTemplate.expire(TOKEN_PREFIX + token, TOKEN_TTL);
    }

    /**
     * 退出登录：删掉 key，令牌立即失效（不用等 2 小时过期）。
     */
    public void logout(String token) {
        if (token != null) {
            redisTemplate.delete(TOKEN_PREFIX + token);
        }
    }
}
