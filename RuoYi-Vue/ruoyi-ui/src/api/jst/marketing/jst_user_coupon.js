import request from '@/utils/request'

// 查询用户持有优惠券列表
export function listJst_user_coupon(query) {
  return request({
    url: '/jst/marketing/jst_user_coupon/list',
    method: 'get',
    params: query
  })
}

// 查询用户持有优惠券详细
export function getJst_user_coupon(userCouponId) {
  return request({
    url: '/jst/marketing/jst_user_coupon/' + userCouponId,
    method: 'get'
  })
}

// 新增用户持有优惠券
export function addJst_user_coupon(data) {
  return request({
    url: '/jst/marketing/jst_user_coupon',
    method: 'post',
    data: data
  })
}

// 修改用户持有优惠券
export function updateJst_user_coupon(data) {
  return request({
    url: '/jst/marketing/jst_user_coupon',
    method: 'put',
    data: data
  })
}

// 删除用户持有优惠券
export function delJst_user_coupon(userCouponId) {
  return request({
    url: '/jst/marketing/jst_user_coupon/' + userCouponId,
    method: 'delete'
  })
}
