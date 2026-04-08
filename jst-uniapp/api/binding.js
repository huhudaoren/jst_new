/**
 * 学生绑定相关接口
 * 对应后端: jst-user/controller/wx/WxBindingController
 */
import request from './request'

export function getMyBindings(options = {}) {
  return request({
    url: '/jst/wx/user/binding',
    method: 'GET',
    ...options
  })
}

export function switchBinding(channelId, options = {}) {
  return request({
    url: '/jst/wx/user/binding/switch',
    method: 'POST',
    data: { channelId },
    ...options
  })
}

export function unbindCurrent(reason = '', options = {}) {
  return request({
    url: '/jst/wx/user/binding/unbind',
    method: 'POST',
    data: { reason },
    ...options
  })
}
