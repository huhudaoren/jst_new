import request from '@/utils/request'

// 订单列表（管理端）
export function listOrderMains(query) {
  return request({ url: '/jst/order/main/list', method: 'get', params: query })
}

// 订单详情
export function getOrderDetail(orderId) {
  return request({ url: '/jst/order/main/' + orderId, method: 'get' })
}
