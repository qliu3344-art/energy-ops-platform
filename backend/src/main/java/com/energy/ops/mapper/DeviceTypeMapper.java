package com.energy.ops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.energy.ops.entity.DeviceType;

/**
 * 设备类型数据访问接口。
 *
 * <p>全部是单表操作，直接复用 BaseMapper，无需自定义方法和 XML。
 */
public interface DeviceTypeMapper extends BaseMapper<DeviceType> {
}
