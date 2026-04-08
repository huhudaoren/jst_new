import request from '@/utils/request'

// 查询消息模板列表
export function listJst_message_template(query) {
  return request({
    url: '/jst/message/jst_message_template/list',
    method: 'get',
    params: query
  })
}

// 查询消息模板详细
export function getJst_message_template(templateId) {
  return request({
    url: '/jst/message/jst_message_template/' + templateId,
    method: 'get'
  })
}

// 新增消息模板
export function addJst_message_template(data) {
  return request({
    url: '/jst/message/jst_message_template',
    method: 'post',
    data: data
  })
}

// 修改消息模板
export function updateJst_message_template(data) {
  return request({
    url: '/jst/message/jst_message_template',
    method: 'put',
    data: data
  })
}

// 删除消息模板
export function delJst_message_template(templateId) {
  return request({
    url: '/jst/message/jst_message_template/' + templateId,
    method: 'delete'
  })
}
