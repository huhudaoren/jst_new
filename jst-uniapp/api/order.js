/**
 * 订单与支付相关接口
 * 对应后端: jst-order/controller/wx/WxOrderController + WxPayController
 */
import request from './request'

export function createOrder(data, options = {}) {
  return request({
    url: '/jst/wx/order/create',
    method: 'POST',
    data,
    ...options
  })
}

export function getMyOrders(params = {}, options = {}) {
  return request({
    url: '/jst/wx/order/my',
    method: 'GET',
    data: params,
    ...options
  })
}

export function getOrderDetail(orderId, options = {}) {
  return request({
    url: `/jst/wx/order/${orderId}`,
    method: 'GET',
    ...options
  })
}

export function cancelOrder(orderId, options = {}) {
  return request({
    url: `/jst/wx/order/${orderId}/cancel`,
    method: 'POST',
    ...options
  })
}

export function mockPaySuccess(orderId, options = {}) {
  return request({
    url: `/jst/wx/pay/mock-success?orderId=${orderId}`,
    method: 'POST',
    ...options
  })
}
