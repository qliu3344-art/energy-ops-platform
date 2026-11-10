package com.energy.ops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.energy.ops.entity.Device;
import com.energy.ops.vo.DeviceVO;
import org.apache.ibatis.annotations.Param;

/**
 * 设备数据访问接口。
 *
 * <p>没有实现类，是 MyBatis 在启动时用动态代理生成的。
 * 继承 {@link BaseMapper} 后，单表增删改查不用写：
 * insert / deleteById / updateById / selectById / selectList / selectCount 都自带。
 *
 * <p>下面两个方法在 resources/mapper/DeviceMapper.xml 里，
 * 靠「namespace = 接口全限定名 + id = 方法名」对应上。
 */
public interface DeviceMapper extends BaseMapper<Device> {

    /**
     * 分页查询设备，LEFT JOIN 设备类型拿到类型名称
     *
     * <p>第一个参数是分页对象，分页插件检测到它就会自动加 LIMIT 并另发一条 COUNT。
     * 其余参数用 @Param 起名，XML 里用 #{keyword} 这样取。
     */
    IPage<DeviceVO> selectDevicePage(IPage<DeviceVO> page,
                                     @Param("keyword") String keyword,
                                     @Param("typeId") Long typeId,
                                     @Param("status") Integer status);

    /** 按 id 查详情，同样联类型表拿类型名称 */
    DeviceVO selectDeviceById(@Param("id") Long id);
}
