import request from '@/utils/request'

// 查询订单主列表
export function listJst_order_main(query) {
  return request({
    url: '/system/jst_order_main/list',
    method: 'get',
    params: query
  })
}

// 查询订单主详细
export function getJst_order_main(orderId) {
  return request({
    url: '/system/jst_order_main/' + orderId,
    method: 'get'
  })
}

// 新增订单主
export function addJst_order_main(data) {
  return request({
    url: '/system/jst_order_main',
    method: 'post',
    data: data
  })
}

// 修改订单主
export function updateJst_order_main(data) {
  return request({
    url: '/system/jst_order_main',
    method: 'put',
    data: data
  })
}

// 删除订单主
export function delJst_order_main(orderId) {
  return request({
    url: '/system/jst_order_main/' + orderId,
    method: 'delete'
  })
}
