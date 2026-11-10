package com.energy.ops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单状态流转日志：和状态更新在同一个事务里写入
 *
 * <p>这张表是「谁在什么时候把工单从哪一步推到了哪一步」的唯一凭据。
 * 状态机和日志一起提交，要么都成功、要么都回滚。
 */
@Data
@TableName("work_order_log")
public class WorkOrderLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联的工单 id */
    private Long orderId;

    /** 流转前的状态；创建工单时为 null */
    private Integer fromStatus;

    /** 流转后的状态 */
    private Integer toStatus;

    /** 操作人；系统自动创建时写 system */
    private String operator;

    /** 备注，比如「已更换轴承」 */
    private String remark;

    private LocalDateTime createTime;
}
