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

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmMapper alarmMapper;

    public PageResult<AlarmVO> page(long page, long size, Integer status, Integer level) {
        IPage<AlarmVO> result = alarmMapper.selectAlarmPage(new Page<>(page, size), status, level);
        result.getRecords().forEach(this::fillText);
        return new PageResult<>(result.getTotal(), result.getRecords(), page, size);
    }

    @Transactional(rollbackFor = Exception.class)
    public void handle(Long id, AlarmHandleDTO dto) {
        Alarm alarm = alarmMapper.selectById(id);
        if (alarm == null) {
            throw new BusinessException(404, "告警不存在");
        }
        if (alarm.getStatus() != null && alarm.getStatus() == 1) {
            throw new BusinessException("该告警已处理");
        }
        alarm.setStatus(1);
        alarm.setHandleTime(LocalDateTime.now());
        alarmMapper.updateById(alarm);
    }

    private void fillText(AlarmVO vo) {
        vo.setLevelText(DictText.alarmLevel(vo.getLevel()));
        vo.setStatusText(DictText.handleStatus(vo.getStatus()));
    }
}
