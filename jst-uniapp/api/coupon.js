/**
 * 优惠券 API (WX-C4 · F-MARKETING-RIGHTS-BE)
 * 对应后端: /jst/wx/coupon/*
 */
import request from '@/api/request'

/** 可领券模板列表 */
export function getClaimableCoupons() {
  return request({ url: '/jst/wx/coupon/template/claimable', method: 'GET' })
}

/** 领券 */
export function claimCoupon(templateId) {
  return request({ url: '/jst/wx/coupon/claim', method: 'POST', data: { templateId } })
}

/**
 * 我的券
 * @param {Object} params { status?:'unused'|'used'|'expired', pageNum, pageSize }
 */
export function getMyCoupons(params) {
  return request({ url: '/jst/wx/coupon/my', method: 'GET', data: params })
}

/**
 * 选券试算 (下单前挑选最优券)
 * @param {Object} body { orderAmount, contestId?, goodsId?, candidateCouponIds? }
 * @returns 后端应返回推荐券 + 每张券试算后的 netPayAmount
 */
export function selectCoupon(body) {
  return request({ url: '/jst/wx/coupon/select', method: 'POST', data: body })
}
