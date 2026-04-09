/**
 * 积分商城 API (WX-C3 · C8)
 * 对应后端: WxMallGoodsController (/jst/wx/mall/goods/*)
 *           WxMallExchangeController (/jst/wx/mall/exchange/*)
 */
import request from '@/api/request'

/**
 * 商品列表（分页）
 * @param {Object} params { goodsType?, roleLimit?, pageNum, pageSize }
 */
export function getMallGoodsList(params) {
  return request({ url: '/jst/wx/mall/goods/list', method: 'GET', data: params })
}

/** 商品详情 */
export function getMallGoodsDetail(goodsId) {
  return request({ url: `/jst/wx/mall/goods/${goodsId}`, method: 'GET' })
}

/**
 * 兑换下单
 * @param {Object} body
 *   { goodsId, quantity, payMethod:'points'|'mixed'|..., addressSnapshot?:{consignee,phone,province,city,district,detail}, addressId? }
 * 三条分支（后端根据商品类型走不同路径）:
 *   - 纯积分虚拟: 直接 paid / completed
 *   - 积分+现金: 返回 orderId/pending_pay，需走 C2 mock 支付
 *   - 实物: 必须传 addressSnapshot
 */
export function applyExchange(body) {
  return request({ url: '/jst/wx/mall/exchange/apply', method: 'POST', data: body })
}

/** mock 支付成功（积分+现金分支 / 开发期） */
export function mockPayExchange(exchangeId) {
  return request({ url: `/jst/wx/mall/exchange/${exchangeId}/pay/mock-success`, method: 'POST' })
}

/** 取消兑换单（仅 pending_pay） */
export function cancelExchange(exchangeId) {
  return request({ url: `/jst/wx/mall/exchange/${exchangeId}/cancel`, method: 'POST' })
}

/**
 * 我的兑换单列表（分页）
 * @param {Object} params { status?, pageNum, pageSize }
 */
export function getMyExchanges(params) {
  return request({ url: '/jst/wx/mall/exchange/my', method: 'GET', data: params })
}

/** 兑换单详情 */
export function getExchangeDetail(exchangeId) {
  return request({ url: `/jst/wx/mall/exchange/${exchangeId}`, method: 'GET' })
}

/* ---------------- 商城售后 (C9) ---------------- */

/**
 * 申请售后
 * @param {Object} body { exchangeId, reason, refundType }
 */
export function applyAftersale(body) {
  return request({ url: '/jst/wx/mall/aftersale/apply', method: 'POST', data: body })
}

/**
 * 我的售后单列表
 * @param {Object} params { status?, pageNum, pageSize }
 */
export function getMyAftersales(params) {
  return request({ url: '/jst/wx/mall/aftersale/my', method: 'GET', data: params })
}

/** 售后单详情 */
export function getAftersaleDetail(refundId) {
  return request({ url: `/jst/wx/mall/aftersale/${refundId}`, method: 'GET' })
}
