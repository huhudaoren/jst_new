import request from '@/utils/request'

// 提现申请列表
export function listWithdraws(query) {
  return request({ url: '/jst/channel/withdraw/list', method: 'get', params: query })
}

// 提现详情
export function getWithdrawDetail(settlementId) {
  return request({ url: '/jst/channel/withdraw/' + settlementId, method: 'get' })
}

// 审核通过
export function approveWithdraw(settlementId, data) {
  return request({ url: '/jst/channel/withdraw/' + settlementId + '/approve', method: 'post', data })
}

// 审核驳回
export function rejectWithdraw(settlementId, data) {
  return request({ url: '/jst/channel/withdraw/' + settlementId + '/reject', method: 'post', data })
}

// 执行打款
export function executeWithdraw(settlementId) {
  return request({ url: '/jst/channel/withdraw/' + settlementId + '/execute', method: 'post' })
}
