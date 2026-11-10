package com.energy.ops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.energy.ops.entity.WorkOrder;
import com.energy.ops.vo.WorkOrderVO;
import org.apache.ibatis.annotations.Param;

/**
 * 工单数据访问接口。
 *
 * <p>单表操作由 BaseMapper 提供，这里只声明需要联表/动态条件的查询，
 * 具体 SQL 在 resources/mapper/WorkOrderMapper.xml。
 */
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {

    /**
     * 分页查询工单，联设备表拿到设备名称。
     *
     * @param page   分页参数，插件据此拼 LIMIT
     * @param status 工单状态，null 表示不筛选
     * @param type   工单类型，null 表示不筛选
     * @param keyword 按工单号或设备名模糊搜，null 表示不筛选
     */
    IPage<WorkOrderVO> selectOrderPage(IPage<WorkOrderVO> page,
                                       @Param("status") Integer status,
                                       @Param("type") Integer type,
                                       @Param("keyword") String keyword);
}
