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

    private static final String CACHE_KEY = "dict:device-type";
    private static final long BASE_TTL_SECONDS = 1800;

    private final DeviceTypeMapper deviceTypeMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public List<DeviceType> listAll() {
        String cached = redisTemplate.opsForValue().get(CACHE_KEY);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, new TypeReference<List<DeviceType>>() {
                });
            } catch (Exception e) {
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

    private void validate(DeviceType type) {
        if (type.getCode() == null || type.getCode().isBlank()) {
            throw new BusinessException("类型编码不能为空");
        }
        if (type.getName() == null || type.getName().isBlank()) {
            throw new BusinessException("类型名称不能为空");
        }
    }

    private void cache(List<DeviceType> list) {
        try {
            String json = objectMapper.writeValueAsString(list);
            // 过期时间加随机值，避免同一时间批量失效（雪崩）
            long ttl = BASE_TTL_SECONDS + ThreadLocalRandom.current().nextInt(300);
            redisTemplate.opsForValue().set(CACHE_KEY, json, Duration.ofSeconds(ttl));
        } catch (Exception e) {
            log.warn("写入缓存失败", e);
        }
    }

    private void evictCache() {
        redisTemplate.delete(CACHE_KEY);
    }
}
