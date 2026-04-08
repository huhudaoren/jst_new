/**
 * 认证相关接口
 * 对应后端: jst-user/controller/wx/WxAuthController + WxUserController
 * 文档: 架构设计/27-用户端API契约.md §3.1
 */
import request, { getToken, setToken, clearToken } from './request'

export { getToken, setToken, clearToken }

/** 小程序登录 (Mock 模式 code=MOCK_xxx) */
export function wxLogin(code) {
  return request({
    url: '/jst/wx/auth/login',
    method: 'POST',
    data: { code }
  })
}

/** 绑定手机号 */
export function bindPhone(encryptedData, iv) {
  return request({
    url: '/jst/wx/auth/bind-phone',
    method: 'POST',
    data: { encryptedData, iv }
  })
}

/** 登出 */
export function logout() {
  return request({
    url: '/jst/wx/auth/logout',
    method: 'POST'
  })
}

/** 获取我的资料 */
export function getProfile() {
  return request({
    url: '/jst/wx/user/profile',
    method: 'GET'
  })
}

/** 更新我的资料 (白名单字段) */
export function updateProfile(data) {
  return request({
    url: '/jst/wx/user/profile',
    method: 'PUT',
    data
  })
}
