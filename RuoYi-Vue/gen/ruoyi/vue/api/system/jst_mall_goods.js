import request from '@/utils/request'

// 查询积分商城商品列表
export function listJst_mall_goods(query) {
  return request({
    url: '/system/jst_mall_goods/list',
    method: 'get',
    params: query
  })
}

// 查询积分商城商品详细
export function getJst_mall_goods(goodsId) {
  return request({
    url: '/system/jst_mall_goods/' + goodsId,
    method: 'get'
  })
}

// 新增积分商城商品
export function addJst_mall_goods(data) {
  return request({
    url: '/system/jst_mall_goods',
    method: 'post',
    data: data
  })
}

// 修改积分商城商品
export function updateJst_mall_goods(data) {
  return request({
    url: '/system/jst_mall_goods',
    method: 'put',
    data: data
  })
}

// 删除积分商城商品
export function delJst_mall_goods(goodsId) {
  return request({
    url: '/system/jst_mall_goods/' + goodsId,
    method: 'delete'
  })
}
