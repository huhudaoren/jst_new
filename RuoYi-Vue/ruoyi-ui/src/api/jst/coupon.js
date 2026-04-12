import request from '@/utils/request'

// ---- 券模板 ----
export function listCouponTemplate(query) {
  return request({ url: '/jst/marketing/jst_coupon_template/list', method: 'get', params: query })
}

export function getCouponTemplate(id) {
  return request({ url: '/jst/marketing/jst_coupon_template/' + id, method: 'get' })
}

export function addCouponTemplate(data) {
  return request({ url: '/jst/marketing/jst_coupon_template', method: 'post', data })
}

export function updateCouponTemplate(data) {
  return request({ url: '/jst/marketing/jst_coupon_template', method: 'put', data })
}

export function delCouponTemplate(ids) {
  return request({ url: '/jst/marketing/jst_coupon_template/' + ids, method: 'delete' })
}

// ---- 已发券（只读）----
export function listUserCoupon(query) {
  return request({ url: '/jst/marketing/jst_user_coupon/list', method: 'get', params: query })
}
