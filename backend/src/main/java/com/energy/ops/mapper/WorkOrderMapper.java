package com.energy.ops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.energy.ops.entity.WorkOrder;
import com.energy.ops.vo.WorkOrderVO;
import org.apache.ibatis.annotations.Param;

public interface WorkOrderMapper extends BaseMapper<WorkOrder> {

    IPage<WorkOrderVO> selectOrderPage(IPage<WorkOrderVO> page,
                                       @Param("status") Integer status,
                                       @Param("type") Integer type,
                                       @Param("keyword") String keyword);
}
