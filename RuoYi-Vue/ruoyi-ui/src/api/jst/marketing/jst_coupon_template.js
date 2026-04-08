import request from '@/utils/request'

// 查询优惠券模板列表
export function listJst_coupon_template(query) {
  return request({
    url: '/jst/marketing/jst_coupon_template/list',
    method: 'get',
    params: query
  })
}

// 查询优惠券模板详细
export function getJst_coupon_template(couponTemplateId) {
  return request({
    url: '/jst/marketing/jst_coupon_template/' + couponTemplateId,
    method: 'get'
  })
}

// 新增优惠券模板
export function addJst_coupon_template(data) {
  return request({
    url: '/jst/marketing/jst_coupon_template',
    method: 'post',
    data: data
  })
}

// 修改优惠券模板
export function updateJst_coupon_template(data) {
  return request({
    url: '/jst/marketing/jst_coupon_template',
    method: 'put',
    data: data
  })
}

// 删除优惠券模板
export function delJst_coupon_template(couponTemplateId) {
  return request({
    url: '/jst/marketing/jst_coupon_template/' + couponTemplateId,
    method: 'delete'
  })
}
