import request from '@/utils/request'

// 查询统一打款记录列表
export function listJst_payment_pay_record(query) {
  return request({
    url: '/jst/finance/jst_payment_pay_record/list',
    method: 'get',
    params: query
  })
}

// 查询统一打款记录详细
export function getJst_payment_pay_record(payRecordId) {
  return request({
    url: '/jst/finance/jst_payment_pay_record/' + payRecordId,
    method: 'get'
  })
}

// 新增统一打款记录
export function addJst_payment_pay_record(data) {
  return request({
    url: '/jst/finance/jst_payment_pay_record',
    method: 'post',
    data: data
  })
}

// 修改统一打款记录
export function updateJst_payment_pay_record(data) {
  return request({
    url: '/jst/finance/jst_payment_pay_record',
    method: 'put',
    data: data
  })
}

// 删除统一打款记录
export function delJst_payment_pay_record(payRecordId) {
  return request({
    url: '/jst/finance/jst_payment_pay_record/' + payRecordId,
    method: 'delete'
  })
}
