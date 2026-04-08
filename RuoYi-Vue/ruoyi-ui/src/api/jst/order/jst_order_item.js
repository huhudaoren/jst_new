import request from '@/utils/request'

// 查询订单明细（最小核算单元，承载分摊与回滚）列表
export function listJst_order_item(query) {
  return request({
    url: '/jst/order/jst_order_item/list',
    method: 'get',
    params: query
  })
}

// 查询订单明细（最小核算单元，承载分摊与回滚）详细
export function getJst_order_item(itemId) {
  return request({
    url: '/jst/order/jst_order_item/' + itemId,
    method: 'get'
  })
}

// 新增订单明细（最小核算单元，承载分摊与回滚）
export function addJst_order_item(data) {
  return request({
    url: '/jst/order/jst_order_item',
    method: 'post',
    data: data
  })
}

// 修改订单明细（最小核算单元，承载分摊与回滚）
export function updateJst_order_item(data) {
  return request({
    url: '/jst/order/jst_order_item',
    method: 'put',
    data: data
  })
}

// 删除订单明细（最小核算单元，承载分摊与回滚）
export function delJst_order_item(itemId) {
  return request({
    url: '/jst/order/jst_order_item/' + itemId,
    method: 'delete'
  })
}
