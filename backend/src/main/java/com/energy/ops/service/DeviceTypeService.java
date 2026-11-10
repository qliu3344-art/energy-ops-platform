package com.energy.ops.service;

import com.energy.ops.common.BusinessException;
import com.energy.ops.entity.DeviceType;
import com.energy.ops.mapper.DeviceTypeMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 设备类型字典：读多写少，用 Redis 做缓存（Cache Aside）。
 * 更新顺序：先改 MySQL，再删除缓存，下次读时自然重建。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceTypeService {

    // 缓存 key 统一加业务前缀，避免和其它模块的 key 撞车
    private static final String CACHE_KEY = "dict:device-type";
    // 基础过期时间 30 分钟；实际写入时会再加 0~300 秒的随机值
    private static final long BASE_TTL_SECONDS = 1800;

    private final DeviceTypeMapper deviceTypeMapper;
    // StringRedisTemplate 只存字符串，所以对象要先序列化成 JSON
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 读路径（Cache Aside 的「读」）：
     * 先查缓存，命中直接返回；没命中就查库，再顺手回填缓存。
     */
    public List<DeviceType> listAll() {
        String cached = redisTemplate.opsForValue().get(CACHE_KEY);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, new TypeReference<List<DeviceType>>() {
                });
            } catch (Exception e) {
                // 缓存里的数据坏了不能让接口挂掉，记日志后当成未命中，回源查库
                log.warn("缓存反序列化失败，回源数据库", e);
            }
        }
        List<DeviceType> list = deviceTypeMapper.selectList(null);
        cache(list);
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long create(DeviceType type) {
        validate(type);
        deviceTypeMapper.insert(type);
        // 先写库再删缓存：如果反过来，删完缓存但写库失败，缓存就永久是旧数据
        evictCache();
        return type.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, DeviceType type) {
        if (deviceTypeMapper.selectById(id) == null) {
            throw new BusinessException(404, "设备类型不存在");
        }
        validate(type);
        type.setId(id);
        deviceTypeMapper.updateById(type);
        evictCache();
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        deviceTypeMapper.deleteById(id);
        evictCache();
    }

    /**
     * 编码和名称是字典的语义所在，为空就没有存在意义，提前拦掉。
     */
    private void validate(DeviceType type) {
        if (type.getCode() == null || type.getCode().isBlank()) {
            throw new BusinessException("类型编码不能为空");
        }
        if (type.getName() == null || type.getName().isBlank()) {
            throw new BusinessException("类型名称不能为空");
        }
    }

    /**
     * 回填缓存。注意 TTL 加了随机值——如果所有 key 在同一秒过期，
     * 请求会同时压到数据库上，这就是缓存雪崩；打散过期时间可以避免。
     */
    private void cache(List<DeviceType> list) {
        try {
            String json = objectMapper.writeValueAsString(list);
            // 过期时间加随机值，避免同一时间批量失效（雪崩）
            long ttl = BASE_TTL_SECONDS + ThreadLocalRandom.current().nextInt(300);
            redisTemplate.opsForValue().set(CACHE_KEY, json, Duration.ofSeconds(ttl));
        } catch (Exception e) {
            // 缓存只是加速手段，写失败不影响主流程，下次读的时候会再回填
            log.warn("写入缓存失败", e);
        }
    }

    /**
     * 删除缓存，而不是更新缓存：更新要处理并发写的顺序问题，删除更简单可靠。
     */
    private void evictCache() {
        redisTemplate.delete(CACHE_KEY);
    }
}
