import request from './request'

export function getAlarms(params) {
  return request.get('/alarms', { params })
}

export function handleAlarm(id, data) {
  return request.put(`/alarms/${id}/handle`, data)
}
