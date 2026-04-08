import request from '@/utils/request'

// 查询支付记录列表
export function listJst_payment_record(query) {
  return request({
    url: '/system/jst_payment_record/list',
    method: 'get',
    params: query
  })
}

// 查询支付记录详细
export function getJst_payment_record(paymentId) {
  return request({
    url: '/system/jst_payment_record/' + paymentId,
    method: 'get'
  })
}

// 新增支付记录
export function addJst_payment_record(data) {
  return request({
    url: '/system/jst_payment_record',
    method: 'post',
    data: data
  })
}

// 修改支付记录
export function updateJst_payment_record(data) {
  return request({
    url: '/system/jst_payment_record',
    method: 'put',
    data: data
  })
}

// 删除支付记录
export function delJst_payment_record(paymentId) {
  return request({
    url: '/system/jst_payment_record/' + paymentId,
    method: 'delete'
  })
}
