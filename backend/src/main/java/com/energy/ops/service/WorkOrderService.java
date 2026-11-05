package com.energy.ops.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.energy.ops.common.BusinessException;
import com.energy.ops.common.DictText;
import com.energy.ops.common.PageResult;
import com.energy.ops.dto.WorkOrderDTO;
import com.energy.ops.dto.WorkOrderStatusDTO;
import com.energy.ops.entity.Device;
import com.energy.ops.entity.WorkOrder;
import com.energy.ops.entity.WorkOrderLog;
import com.energy.ops.mapper.DeviceMapper;
import com.energy.ops.mapper.WorkOrderLogMapper;
import com.energy.ops.mapper.WorkOrderMapper;
import com.energy.ops.vo.WorkOrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private final WorkOrderMapper workOrderMapper;
    private final WorkOrderLogMapper workOrderLogMapper;
    private final DeviceMapper deviceMapper;

    public PageResult<WorkOrderVO> page(long page, long size, Integer status, Integer type, String keyword) {
        IPage<WorkOrderVO> result = workOrderMapper.selectOrderPage(
                new Page<>(page, size), status, type, keyword);
        result.getRecords().forEach(this::fillText);
        return new PageResult<>(result.getTotal(), result.getRecords(), page, size);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long create(WorkOrderDTO dto) {
        Device device = deviceMapper.selectById(dto.getDeviceId());
        if (device == null) {
            throw new BusinessException(404, "设备不存在");
        }
        WorkOrder order = new WorkOrder();
        order.setCode("WO" + System.currentTimeMillis());
        order.setDeviceId(dto.getDeviceId());
        order.setType(dto.getType());
        order.setStatus(0);
        order.setAssignee(dto.getAssignee());
        order.setDescription(dto.getDescription());
        workOrderMapper.insert(order);
        writeLog(order.getId(), null, 0, "system", "创建工单");
        return order.getId();
    }

    /**
     * 工单状态流转：更新状态 + 写流转日志在同一个事务里。
     * 状态不合法时抛业务异常，两条 SQL 一起回滚。
     */
    @Transactional(rollbackFor = Exception.class)
    public void transition(Long id, WorkOrderStatusDTO dto) {
        WorkOrder order = workOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(404, "工单不存在");
        }
        int from = order.getStatus();
        int to = dto.getToStatus();
        if (!canTransition(from, to)) {
            throw new BusinessException(String.format("不允许从「%s」流转到「%s」",
                    DictText.orderStatus(from), DictText.orderStatus(to)));
        }
        order.setStatus(to);
        if (to == 3) {
            order.setFinishTime(LocalDateTime.now());
        }
        workOrderMapper.updateById(order);
        writeLog(id, from, to,
                dto.getOperator() == null || dto.getOperator().isBlank() ? "admin" : dto.getOperator(),
                dto.getRemark());
    }

    private boolean canTransition(int from, int to) {
        return (from == 0 && to == 1)
                || (from == 1 && to == 2)
                || (from == 2 && to == 3)
                || (from == 3 && to == 4);
    }

    private void writeLog(Long orderId, Integer from, Integer to, String operator, String remark) {
        WorkOrderLog log = new WorkOrderLog();
        log.setOrderId(orderId);
        log.setFromStatus(from);
        log.setToStatus(to);
        log.setOperator(operator);
        log.setRemark(remark);
        workOrderLogMapper.insert(log);
    }

    private void fillText(WorkOrderVO vo) {
        vo.setStatusText(DictText.orderStatus(vo.getStatus()));
        vo.setTypeText(DictText.orderType(vo.getType()));
    }
}
