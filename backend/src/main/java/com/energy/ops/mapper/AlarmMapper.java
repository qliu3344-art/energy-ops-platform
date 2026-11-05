package com.energy.ops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.energy.ops.entity.Alarm;
import com.energy.ops.vo.AlarmVO;
import org.apache.ibatis.annotations.Param;

public interface AlarmMapper extends BaseMapper<Alarm> {

    IPage<AlarmVO> selectAlarmPage(IPage<AlarmVO> page,
                                   @Param("status") Integer status,
                                   @Param("level") Integer level);
}
