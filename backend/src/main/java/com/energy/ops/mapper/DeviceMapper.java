package com.energy.ops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.energy.ops.entity.Device;
import com.energy.ops.vo.DeviceVO;
import org.apache.ibatis.annotations.Param;

public interface DeviceMapper extends BaseMapper<Device> {

    /**
     * 分页查询设备，LEFT JOIN 设备类型拿到类型名称
     */
    IPage<DeviceVO> selectDevicePage(IPage<DeviceVO> page,
                                     @Param("keyword") String keyword,
                                     @Param("typeId") Long typeId,
                                     @Param("status") Integer status);

    DeviceVO selectDeviceById(@Param("id") Long id);
}
