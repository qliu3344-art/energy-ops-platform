package com.energy.ops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.energy.ops.entity.WorkOrderLog;

/**
 * 工单日志数据访问接口。
 *
 * <p>只有插入需求，BaseMapper 自带的 insert 就够了，
 * 所以这里一个方法都不用声明，也不需要对应的 XML。
 */
public interface WorkOrderLogMapper extends BaseMapper<WorkOrderLog> {
}
