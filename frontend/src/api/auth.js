import request from './request'

// 登录相关接口

/** 登录成功返回 { token, username }，由页面存进 localStorage */
export function login(data) {
  return request.post('/auth/login', data)
}

/** 退出登录：后端会把令牌从 Redis 删掉 */
export function logout() {
  return request.post('/auth/logout')
}
