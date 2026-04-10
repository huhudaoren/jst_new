/**
 * 消息相关接口
 * 对应后端: jst-message/controller/wx/WxMessageController
 * 文档: 架构设计/27-用户端API契约.md §3.9
 */
import request from './request'

// 我的消息列表
export function getMyMessageList(params = {}, options = {}) {
  return request({
    url: '/jst/wx/message/my',
    method: 'GET',
    data: params,
    silent: true,
    ...options
  })
}

// 标记单条已读
export function markMessageRead(id, options = {}) {
  return request({
    url: `/jst/wx/message/${id}/read`,
    method: 'POST',
    ...options
  })
}

// 全部标记已读
export function markAllMessageRead(options = {}) {
  return request({
    url: '/jst/wx/message/read-all',
    method: 'POST',
    ...options
  })
}
