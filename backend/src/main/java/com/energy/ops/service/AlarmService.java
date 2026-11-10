package com.energy.ops.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.energy.ops.common.BusinessException;
import com.energy.ops.common.DictText;
import com.energy.ops.common.PageResult;
import com.energy.ops.dto.AlarmHandleDTO;
import com.energy.ops.entity.Alarm;
import com.energy.ops.mapper.AlarmMapper;
import com.energy.ops.vo.AlarmVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 告警业务层：查询告警、标记处理。
 * 设备主动上报的异常落在这张表里，处理后可以转成工单进入维修流程。
 */
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmMapper alarmMapper;

    public PageResult<AlarmVO> page(long page, long size, Integer status, Integer level) {
        IPage<AlarmVO> result = alarmMapper.selectAlarmPage(new Page<>(page, size), status, level);
        result.getRecords().forEach(this::fillText);
        return new PageResult<>(result.getTotal(), result.getRecords(), page, size);
    }

    /**
     * 处理告警：置为已处理并记录处理时间。
     * 处理前先判断当前状态，保证重复点「处理」不会覆盖第一次的处理时间。
     */
    @Transactional(rollbackFor = Exception.class)
    public void handle(Long id, AlarmHandleDTO dto) {
        Alarm alarm = alarmMapper.selectById(id);
        if (alarm == null) {
            throw new BusinessException(404, "告警不存在");
        }
        // 幂等保护：已经处理过的告警不允许再处理一次
        if (alarm.getStatus() != null && alarm.getStatus() == 1) {
            throw new BusinessException("该告警已处理");
        }
        alarm.setStatus(1);
        alarm.setHandleTime(LocalDateTime.now());
        alarmMapper.updateById(alarm);
    }

    /**
     * 补中文：等级 1/2/3 → 提示/警告/严重，处理状态 0/1 → 未处理/已处理。
     */
    private void fillText(AlarmVO vo) {
        vo.setLevelText(DictText.alarmLevel(vo.getLevel()));
        vo.setStatusText(DictText.handleStatus(vo.getStatus()));
    }
}
