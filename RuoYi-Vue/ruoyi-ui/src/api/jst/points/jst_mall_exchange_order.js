import request from '@/utils/request'

// 查询积分商城兑换订单列表
export function listJst_mall_exchange_order(query) {
  return request({
    url: '/jst/points/jst_mall_exchange_order/list',
    method: 'get',
    params: query
  })
}

// 查询积分商城兑换订单详细
export function getJst_mall_exchange_order(exchangeId) {
  return request({
    url: '/jst/points/jst_mall_exchange_order/' + exchangeId,
    method: 'get'
  })
}

// 新增积分商城兑换订单
export function addJst_mall_exchange_order(data) {
  return request({
    url: '/jst/points/jst_mall_exchange_order',
    method: 'post',
    data: data
  })
}

// 修改积分商城兑换订单
export function updateJst_mall_exchange_order(data) {
  return request({
    url: '/jst/points/jst_mall_exchange_order',
    method: 'put',
    data: data
  })
}

// 删除积分商城兑换订单
export function delJst_mall_exchange_order(exchangeId) {
  return request({
    url: '/jst/points/jst_mall_exchange_order/' + exchangeId,
    method: 'delete'
  })
}
