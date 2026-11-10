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

/**
 * 设备台账业务层：分页、详情、新增、修改、删除。
 *
 * <p>业务规则集中在这里：编码唯一校验、默认状态、状态码转中文。
 * Mapper 只管取数据，不判断对错。
 */
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceMapper deviceMapper;

    /**
     * 分页查询。分页和联表 SQL 交给 Mapper，这里只负责补中文。
     */
    public PageResult<DeviceVO> page(long page, long size, String keyword, Long typeId, Integer status) {
        IPage<DeviceVO> result = deviceMapper.selectDevicePage(
                new Page<>(page, size), keyword, typeId, status);
        result.getRecords().forEach(this::fillText);
        return new PageResult<>(result.getTotal(), result.getRecords(), page, size);
    }

    /**
     * 查详情。查不到直接抛业务异常，由全局异常处理转成 404，
     * 避免 Controller 里到处写 if (vo == null)。
     */
    public DeviceVO getById(Long id) {
        DeviceVO vo = deviceMapper.selectDeviceById(id);
        if (vo == null) {
            throw new BusinessException(404, "设备不存在");
        }
        fillText(vo);
        return vo;
    }

    /**
     * 新增设备。
     *
     * <p>{@code BeanUtils.copyProperties} 底层是反射，按属性名把 DTO 的值拷到 Entity，
     * 这样不用手写十几行 set。DTO 里没有 id 字段，所以拷不进来，主键仍由数据库自增。
     */
    @Transactional(rollbackFor = Exception.class)
    public Long create(DeviceDTO dto) {
        // 第二道防线：数据库有唯一索引，这里提前给出友好提示
        checkCodeUnique(dto.getCode(), null);
        Device device = new Device();
        BeanUtils.copyProperties(dto, device);
        if (device.getStatus() == null) {
            // 没传状态时默认「运行」
            device.setStatus(1);
        }
        deviceMapper.insert(device);
        return device.getId();   // 自增主键已回填
    }

    /**
     * 修改设备。先确认存在，再校验编码没有被**别的设备**占用。
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, DeviceDTO dto) {
        Device existing = deviceMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "设备不存在");
        }
        // 排除自己：不改编码时，自己当然会和自己重复
        checkCodeUnique(dto.getCode(), id);
        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);      // 防止 DTO 里带 id 把主键覆盖掉
        deviceMapper.updateById(existing);
    }

    /**
     * 删除设备。不存在的 id 返回 404，而不是静默成功。
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (deviceMapper.selectById(id) == null) {
            throw new BusinessException(404, "设备不存在");
        }
        deviceMapper.deleteById(id);
    }

    /**
     * 编码唯一校验。
     *
     * <p>{@code Device::getCode} 是方法引用，MyBatis-Plus 通过反射把方法名转成列名，
     * 好处是写错编译期就报错，不用拼字符串。excludeId 用于修改时排除自己。
     */
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

    /** 把状态数字翻译成中文，前端直接显示 */
    private void fillText(DeviceVO vo) {
        vo.setStatusText(DictText.deviceStatus(vo.getStatus()));
    }
}
