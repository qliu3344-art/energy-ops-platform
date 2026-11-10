package com.energy.ops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.energy.ops.entity.Alarm;
import com.energy.ops.vo.AlarmVO;
import org.apache.ibatis.annotations.Param;

/**
 * 告警数据访问接口。
 *
 * <p>列表要联设备表拿设备名称，所以单独声明一个方法，SQL 在 AlarmMapper.xml。
 */
public interface AlarmMapper extends BaseMapper<Alarm> {

    /**
     * 分页查询告警，联设备表拿到设备名称。
     *
     * @param status 处理状态，null 表示不筛选
     * @param level  告警等级，null 表示不筛选
     */
    IPage<AlarmVO> selectAlarmPage(IPage<AlarmVO> page,
                                   @Param("status") Integer status,
                                   @Param("level") Integer level);
}
