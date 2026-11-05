package com.energy.ops.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.energy.ops.common.BusinessException;
import com.energy.ops.common.DictText;
import com.energy.ops.common.PageResult;
import com.energy.ops.dto.DeviceDTO;
import com.energy.ops.entity.Device;
import com.energy.ops.mapper.DeviceMapper;
import com.energy.ops.vo.DeviceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceMapper deviceMapper;

    public PageResult<DeviceVO> page(long page, long size, String keyword, Long typeId, Integer status) {
        IPage<DeviceVO> result = deviceMapper.selectDevicePage(
                new Page<>(page, size), keyword, typeId, status);
        result.getRecords().forEach(this::fillText);
        return new PageResult<>(result.getTotal(), result.getRecords(), page, size);
    }

    public DeviceVO getById(Long id) {
        DeviceVO vo = deviceMapper.selectDeviceById(id);
        if (vo == null) {
            throw new BusinessException(404, "设备不存在");
        }
        fillText(vo);
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long create(DeviceDTO dto) {
        checkCodeUnique(dto.getCode(), null);
        Device device = new Device();
        BeanUtils.copyProperties(dto, device);
        if (device.getStatus() == null) {
            device.setStatus(1);
        }
        deviceMapper.insert(device);
        return device.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, DeviceDTO dto) {
        Device existing = deviceMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "设备不存在");
        }
        checkCodeUnique(dto.getCode(), id);
        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        deviceMapper.updateById(existing);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (deviceMapper.selectById(id) == null) {
            throw new BusinessException(404, "设备不存在");
        }
        deviceMapper.deleteById(id);
    }

    private void checkCodeUnique(String code, Long excludeId) {
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<Device>()
                .eq(Device::getCode, code);
        if (excludeId != null) {
            wrapper.ne(Device::getId, excludeId);
        }
        if (deviceMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("设备编码已存在：" + code);
        }
    }

    private void fillText(DeviceVO vo) {
        vo.setStatusText(DictText.deviceStatus(vo.getStatus()));
    }
}
