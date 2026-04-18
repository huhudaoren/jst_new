import request from '@/utils/request'

// 月结单列表（分页）
export function listSettlement(query) {
  return request({ url: '/admin/sales/settlement/list', method: 'get', params: query })
}

// 月结单详情（含 ledger 明细）
export function getSettlementDetail(settlementId) {
  return request({ url: `/admin/sales/settlement/${settlementId}`, method: 'get' })
}

// 审核通过
export function approveSettlement(settlementId) {
  return request({ url: `/admin/sales/settlement/${settlementId}/approve`, method: 'post' })
}

// 驳回
export function rejectSettlement(settlementId, reason) {
  return request({ url: `/admin/sales/settlement/${settlementId}/reject`, method: 'post', data: { reason } })
}

// 录入打款凭证
export function recordPayment(settlementId, voucher) {
  return request({ url: `/admin/sales/settlement/${settlementId}/pay-record`, method: 'post', data: { voucher } })
}
