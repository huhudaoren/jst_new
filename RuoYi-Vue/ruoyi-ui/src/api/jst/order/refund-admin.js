import request from '@/utils/request'

// 退款列表（管理端）
export function listRefunds(query) {
  return request({ url: '/jst/order/refund/list', method: 'get', params: query })
}

// 审核通过
export function approveRefund(refundId, data) {
  return request({ url: '/jst/order/refund/' + refundId + '/approve', method: 'post', data })
}

// 审核驳回
export function rejectRefund(refundId, data) {
  return request({ url: '/jst/order/refund/' + refundId + '/reject', method: 'post', data })
}

// 执行退款
export function executeRefund(refundId) {
  return request({ url: '/jst/order/refund/' + refundId + '/execute', method: 'post' })
}

// Admin 特批退款
export function specialRefund(orderId, data) {
  return request({ url: '/jst/admin/order/' + orderId + '/special-refund', method: 'post', data })
}
