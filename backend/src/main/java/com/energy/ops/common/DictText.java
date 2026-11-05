package com.energy.ops.common;

/**
 * 状态码转中文，避免前端再维护一份映射
 */
public final class DictText {

    private DictText() {
    }

    public static String deviceStatus(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 0 -> "停用";
            case 1 -> "运行";
            case 2 -> "故障";
            default -> "未知";
        };
    }

    public static String orderStatus(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 0 -> "待派发";
            case 1 -> "已派发";
            case 2 -> "执行中";
            case 3 -> "已完成";
            case 4 -> "已验收";
            default -> "未知";
        };
    }

    public static String orderType(Integer type) {
        if (type == null) return "";
        return switch (type) {
            case 1 -> "巡检";
            case 2 -> "维修";
            default -> "未知";
        };
    }

    public static String alarmLevel(Integer level) {
        if (level == null) return "";
        return switch (level) {
            case 1 -> "提示";
            case 2 -> "警告";
            case 3 -> "严重";
            default -> "未知";
        };
    }

    public static String handleStatus(Integer status) {
        if (status == null) return "";
        return status == 1 ? "已处理" : "未处理";
    }

    public static String planStatus(Integer status) {
        if (status == null) return "";
        return status == 1 ? "启用" : "停用";
    }
}
