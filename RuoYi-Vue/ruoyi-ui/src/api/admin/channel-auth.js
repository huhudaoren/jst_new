import request from '@/utils/request'

// 渠道认证申请列表（管理员审核视角）
export function listChannelAuth(query) {
  return request({ url: '/jst/organizer/channel/apply/list', method: 'get', params: query })
}

// 渠道认证申请详情
export function getChannelAuth(applyId) {
  return request({ url: '/jst/organizer/channel/apply/' + applyId, method: 'get' })
}

// 审核通过
export function approveChannelAuth(applyId) {
  return request({ url: '/jst/organizer/channel/apply/' + applyId + '/approve', method: 'post' })
}

// 驳回
export function rejectChannelAuth(applyId, data) {
  return request({ url: '/jst/organizer/channel/apply/' + applyId + '/reject', method: 'post', data })
}

// 暂停
export function suspendChannelAuth(applyId) {
  return request({ url: '/jst/organizer/channel/apply/' + applyId + '/suspend', method: 'post' })
}
