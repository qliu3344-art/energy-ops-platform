-- 能源场站设备运维管理平台 · 初始化脚本
-- 可重复执行：建表 if not exists，再清空重灌种子数据

CREATE DATABASE IF NOT EXISTS energy_ops DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE energy_ops;

CREATE TABLE IF NOT EXISTS device_type (
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(32)  NOT NULL UNIQUE COMMENT '类型编码',
    name VARCHAR(64)  NOT NULL COMMENT '类型名称'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='设备类型字典';

CREATE TABLE IF NOT EXISTS device (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    code         VARCHAR(64)  NOT NULL UNIQUE COMMENT '设备编码，一物一码',
    name         VARCHAR(100) NOT NULL COMMENT '设备名称',
    type_id      BIGINT       NOT NULL COMMENT '设备类型',
    station      VARCHAR(64)  NOT NULL COMMENT '所属场站',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '0 停用 / 1 运行 / 2 故障',
    install_date DATE         NULL COMMENT '投运日期',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_type (type_id),
    KEY idx_station (station)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='设备台账';

CREATE TABLE IF NOT EXISTS inspection_plan (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    device_id   BIGINT       NOT NULL,
    name        VARCHAR(100) NOT NULL COMMENT '计划名称',
    cycle_days  INT          NOT NULL COMMENT '巡检周期（天）',
    next_time   DATETIME     NOT NULL COMMENT '下次巡检时间',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1 启用 / 0 停用',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    KEY idx_device (device_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='巡检计划';

CREATE TABLE IF NOT EXISTS work_order (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    code        VARCHAR(64)  NOT NULL UNIQUE COMMENT '工单号',
    device_id   BIGINT       NOT NULL,
    type        TINYINT      NOT NULL COMMENT '1 巡检 / 2 维修',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '0 待派发 / 1 已派发 / 2 执行中 / 3 已完成 / 4 已验收',
    assignee    VARCHAR(32)  NULL COMMENT '负责人',
    description VARCHAR(500) NULL,
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    finish_time DATETIME     NULL,
    KEY idx_device (device_id),
    KEY idx_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='工单';

CREATE TABLE IF NOT EXISTS work_order_log (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id    BIGINT       NOT NULL,
    from_status TINYINT      NULL,
    to_status   TINYINT      NOT NULL,
    operator    VARCHAR(32)  NULL,
    remark      VARCHAR(200) NULL,
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    KEY idx_order (order_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='工单状态流转日志';

CREATE TABLE IF NOT EXISTS alarm (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    device_id   BIGINT       NOT NULL,
    level       TINYINT      NOT NULL COMMENT '1 提示 / 2 警告 / 3 严重',
    content     VARCHAR(255) NOT NULL,
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '0 未处理 / 1 已处理',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    handle_time DATETIME     NULL,
    KEY idx_device (device_id),
    KEY idx_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='设备告警';

-- ---------- 种子数据 ----------
TRUNCATE TABLE work_order_log;
TRUNCATE TABLE work_order;
TRUNCATE TABLE alarm;
TRUNCATE TABLE inspection_plan;
TRUNCATE TABLE device;
TRUNCATE TABLE device_type;

INSERT INTO device_type (id, code, name) VALUES
(1, 'TB',  '变压器'),
(2, 'INV', '逆变器'),
(3, 'JB',  '汇流箱'),
(4, 'MET', '气象站');

INSERT INTO device (id, code, name, type_id, station, status, install_date) VALUES
(1,  'DEV-001', '1号主变压器',   1, '雄安一号光伏电站', 1, '2023-05-12'),
(2,  'DEV-002', '2号主变压器',   1, '雄安一号光伏电站', 1, '2023-05-12'),
(3,  'DEV-003', '3号主变压器',   1, '雄安二号储能站',   2, '2023-07-20'),
(4,  'DEV-004', '1号组串逆变器', 2, '雄安一号光伏电站', 1, '2023-06-01'),
(5,  'DEV-005', '2号组串逆变器', 2, '雄安一号光伏电站', 1, '2023-06-01'),
(6,  'DEV-006', '3号组串逆变器', 2, '雄安二号储能站',   1, '2023-08-15'),
(7,  'DEV-007', '1号汇流箱',     3, '雄安一号光伏电站', 1, '2023-06-10'),
(8,  'DEV-008', '2号汇流箱',     3, '雄安一号光伏电站', 0, '2023-06-10'),
(9,  'DEV-009', '场站气象站',    4, '雄安一号光伏电站', 1, '2023-04-28'),
(10, 'DEV-010', '储能舱温控',    2, '雄安二号储能站',   2, '2024-03-06');

INSERT INTO inspection_plan (id, device_id, name, cycle_days, next_time, status) VALUES
(1, 1, '主变月度巡检',   30, '2026-11-15 09:00:00', 1),
(2, 4, '逆变器周巡检',   7,  '2026-11-08 09:00:00', 1),
(3, 7, '汇流箱季度检查', 90, '2026-12-20 09:00:00', 1),
(4, 9, '气象站校准',     180,'2027-01-10 09:00:00', 1),
(5, 10,'储能舱温控检查', 7,  '2026-11-09 09:00:00', 0);

INSERT INTO work_order (id, code, device_id, type, status, assignee, description, create_time, finish_time) VALUES
(1, 'WO20261101001', 3,  2, 0, NULL,   '主变压器油温异常，需现场排查',       '2026-11-01 08:30:00', NULL),
(2, 'WO20261101002', 4,  1, 1, '张工', '逆变器例行巡检',                     '2026-11-01 09:00:00', NULL),
(3, 'WO20261102001', 7,  1, 2, '李工', '汇流箱接线端子检查',                 '2026-11-02 10:15:00', NULL),
(4, 'WO20261102002', 1,  1, 3, '王工', '主变月度巡检',                       '2026-11-02 14:00:00', '2026-11-03 16:20:00'),
(5, 'WO20261103001', 10, 2, 1, '赵工', '储能舱温控模块告警复位',             '2026-11-03 08:45:00', NULL),
(6, 'WO20261103002', 5,  1, 4, '张工', '逆变器周巡检',                       '2026-11-03 09:30:00', '2026-11-04 11:00:00'),
(7, 'WO20261104001', 9,  1, 0, NULL,   '气象站数据漂移校验',                 '2026-11-04 09:10:00', NULL),
(8, 'WO20261104002', 2,  2, 2, '李工', '主变冷却风机异响处理',               '2026-11-04 15:40:00', NULL);

INSERT INTO alarm (id, device_id, level, content, status, create_time, handle_time) VALUES
(1, 3,  3, '主变压器油温 92℃，超过告警阈值 85℃', 0, '2026-11-01 08:20:00', NULL),
(2, 10, 2, '储能舱温度 41℃，超过告警阈值 40℃',   0, '2026-11-03 08:40:00', NULL),
(3, 5,  1, '逆变器效率短时低于 95%',              1, '2026-11-03 09:15:00', '2026-11-03 10:30:00'),
(4, 8,  2, '汇流箱通讯中断超过 5 分钟',           0, '2026-11-04 07:50:00', NULL),
(5, 9,  1, '气象站辐照度数据连续 10 分钟为 0',    0, '2026-11-04 09:05:00', NULL),
(6, 1,  1, '主变油位轻微下降',                    1, '2026-11-02 13:20:00', '2026-11-02 15:00:00');
