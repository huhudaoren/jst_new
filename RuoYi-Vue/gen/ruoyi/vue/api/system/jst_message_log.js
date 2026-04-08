import request from '@/utils/request'

// 查询消息发送日志列表
export function listJst_message_log(query) {
  return request({
    url: '/system/jst_message_log/list',
    method: 'get',
    params: query
  })
}

// 查询消息发送日志详细
export function getJst_message_log(logId) {
  return request({
    url: '/system/jst_message_log/' + logId,
    method: 'get'
  })
}

// 新增消息发送日志
export function addJst_message_log(data) {
  return request({
    url: '/system/jst_message_log',
    method: 'post',
    data: data
  })
}

// 修改消息发送日志
export function updateJst_message_log(data) {
  return request({
    url: '/system/jst_message_log',
    method: 'put',
    data: data
  })
}

// 删除消息发送日志
export function delJst_message_log(logId) {
  return request({
    url: '/system/jst_message_log/' + logId,
    method: 'delete'
  })
}
