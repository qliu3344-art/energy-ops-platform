package com.energy.ops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.energy.ops.entity.InspectionPlan;
import com.energy.ops.vo.InspectionPlanVO;
import org.apache.ibatis.annotations.Param;

/**
 * 巡检计划数据访问接口。
 *
 * <p>列表要联设备表拿设备编码和名称，SQL 在 InspectionPlanMapper.xml。
 */
public interface InspectionPlanMapper extends BaseMapper<InspectionPlan> {

    /**
     * 分页查询巡检计划。
     *
     * @param deviceId 按设备过滤，null 表示查全部
     */
    IPage<InspectionPlanVO> selectPlanPage(IPage<InspectionPlanVO> page,
                                           @Param("deviceId") Long deviceId);
}
