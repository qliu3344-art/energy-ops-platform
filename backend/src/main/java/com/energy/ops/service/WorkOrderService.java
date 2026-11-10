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

/**
 * 工单业务层：创建工单、推进状态、分页查询。
 *
 * <p>工单是「设备—巡检—告警」这条链路的闭环：任何一次维修最终都落成一张工单。
 * 状态变化必须留痕，所以每次流转都会同时写一条 {@link WorkOrderLog}。
 */
@Service
@RequiredArgsConstructor
public class WorkOrderService {

    // 构造器注入：字段声明成 final，@RequiredArgsConstructor 生成唯一构造器，Spring 自动传入
    private final WorkOrderMapper workOrderMapper;
    private final WorkOrderLogMapper workOrderLogMapper;
    private final DeviceMapper deviceMapper;

    /**
     * 分页查询工单。
     * Mapper 返回的 VO 里只有状态数字，这里统一补成中文，前端不用再维护一份映射表。
     */
    public PageResult<WorkOrderVO> page(long page, long size, Integer status, Integer type, String keyword) {
        // 分页参数交给 Page，实际 SQL 里的 LIMIT 由 MyBatis-Plus 分页插件在运行时补上
        IPage<WorkOrderVO> result = workOrderMapper.selectOrderPage(
                new Page<>(page, size), status, type, keyword);
        result.getRecords().forEach(this::fillText);
        return new PageResult<>(result.getTotal(), result.getRecords(), page, size);
    }

    /**
     * 创建工单：插入工单 + 写一条「创建」日志，两步在同一个事务里。
     */
    @Transactional(rollbackFor = Exception.class)
    public Long create(WorkOrderDTO dto) {
        // 先确认设备存在，避免产生指向空设备的孤儿工单
        Device device = deviceMapper.selectById(dto.getDeviceId());
        if (device == null) {
            throw new BusinessException(404, "设备不存在");
        }
        WorkOrder order = new WorkOrder();
        // 单号用毫秒时间戳保证唯一；生产环境一般换成「日期 + 当日序列」或发号器
        order.setCode("WO" + System.currentTimeMillis());
        order.setDeviceId(dto.getDeviceId());
        order.setType(dto.getType());
        order.setStatus(0);   // 新建工单一律从「待派发」开始
        order.setAssignee(dto.getAssignee());
        order.setDescription(dto.getDescription());
        workOrderMapper.insert(order);
        // insert 之后 MyBatis-Plus 会把自增主键回填到 order.getId()
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
        // 状态机校验：只允许相邻推进，跳级或回退一律拒绝
        if (!canTransition(from, to)) {
            throw new BusinessException(String.format("不允许从「%s」流转到「%s」",
                    DictText.orderStatus(from), DictText.orderStatus(to)));
        }
        order.setStatus(to);
        if (to == 1) {
            // 派发即指定执行人：负责人在 0→1 这一步落库
            if (dto.getAssignee() == null || dto.getAssignee().isBlank()) {
                throw new BusinessException("派发工单时必须指定负责人");
            }
            order.setAssignee(dto.getAssignee().trim());
        }
        if (to == 3) {
            // 到达「已完成」时记录完成时间，后续可以统计每张工单的耗时
            order.setFinishTime(LocalDateTime.now());
        }
        workOrderMapper.updateById(order);
        // 谁、在什么时候、把工单从哪一步推到了哪一步，全部留痕
        writeLog(id, from, to,
                dto.getOperator() == null || dto.getOperator().isBlank() ? "admin" : dto.getOperator(),
                dto.getRemark());
    }

    /**
     * 五态线性流转：待派发(0) → 已派发(1) → 执行中(2) → 已完成(3) → 已验收(4)。
     *
     * <p>这里写成显式规则而不是简单的「to > from」，是为了以后允许分支
     * （比如从「执行中」驳回回「已派发」）时，规则还能改得动。
     */
    private boolean canTransition(int from, int to) {
        return (from == 0 && to == 1)
                || (from == 1 && to == 2)
                || (from == 2 && to == 3)
                || (from == 3 && to == 4);
    }

    /**
     * 写一条流转日志。创建工单时 from 传 null、to 传 0，表示这是初始状态。
     */
    private void writeLog(Long orderId, Integer from, Integer to, String operator, String remark) {
        WorkOrderLog log = new WorkOrderLog();
        log.setOrderId(orderId);
        log.setFromStatus(from);
        log.setToStatus(to);
        log.setOperator(operator);
        log.setRemark(remark);
        workOrderLogMapper.insert(log);
    }

    /**
     * 把数据库里的数字状态码翻译成前端要显示的中文。
     */
    private void fillText(WorkOrderVO vo) {
        vo.setStatusText(DictText.orderStatus(vo.getStatus()));
        vo.setTypeText(DictText.orderType(vo.getType()));
    }
}
