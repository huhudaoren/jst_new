import request from '@/utils/request'

// ---- 积分商城商品 ----
export function listMallGoods(query) {
  return request({ url: '/jst/points/jst_mall_goods/list', method: 'get', params: query })
}

export function getMallGoods(id) {
  return request({ url: '/jst/points/jst_mall_goods/' + id, method: 'get' })
}

export function addMallGoods(data) {
  return request({ url: '/jst/points/jst_mall_goods', method: 'post', data })
}

export function updateMallGoods(data) {
  return request({ url: '/jst/points/jst_mall_goods', method: 'put', data })
}

export function delMallGoods(ids) {
  return request({ url: '/jst/points/jst_mall_goods/' + ids, method: 'delete' })
}

// ---- 兑换订单 ----
export function listExchangeOrder(query) {
  return request({ url: '/jst/points/mall/exchange/list', method: 'get', params: query })
}

export function getExchangeOrder(id) {
  return request({ url: '/jst/points/mall/exchange/' + id, method: 'get' })
}

export function shipExchangeOrder(id, data) {
  return request({ url: '/jst/points/mall/exchange/' + id + '/ship', method: 'post', data })
}

export function completeExchangeOrder(id) {
  return request({ url: '/jst/points/mall/exchange/' + id + '/complete', method: 'post' })
}
