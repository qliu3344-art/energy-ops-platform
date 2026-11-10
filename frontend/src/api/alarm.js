import request from './request'

// 告警相关接口

/** 分页查询告警，可按处理状态、等级筛选 */
export function getAlarms(params) {
  return request.get('/alarms', { params })
}

/** 标记告警已处理 */
export function handleAlarm(id, data) {
  return request.put(`/alarms/${id}/handle`, data)
}
