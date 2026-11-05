package com.energy.ops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.energy.ops.entity.InspectionPlan;
import com.energy.ops.vo.InspectionPlanVO;
import org.apache.ibatis.annotations.Param;

public interface InspectionPlanMapper extends BaseMapper<InspectionPlan> {

    IPage<InspectionPlanVO> selectPlanPage(IPage<InspectionPlanVO> page,
                                           @Param("deviceId") Long deviceId);
}
