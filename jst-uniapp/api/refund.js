/**
 * 退款相关接口
 * 对应后端: jst-order/controller/wx/WxRefundController
 */
import request from './request'

export function applyRefund(orderId, data, options = {}) {
  return request({
    url: `/jst/wx/order/${orderId}/refund`,
    method: 'POST',
    data,
    ...options
  })
}

export function getMyRefunds(params = {}, options = {}) {
  return request({
    url: '/jst/wx/refund/my',
    method: 'GET',
    data: params,
    ...options
  })
}

export function getRefundDetail(refundId, options = {}) {
  return request({
    url: `/jst/wx/refund/${refundId}`,
    method: 'GET',
    ...options
  })
}

export function cancelRefund(refundId, options = {}) {
  return request({
    url: `/jst/wx/refund/${refundId}/cancel`,
    method: 'POST',
    ...options
  })
}
