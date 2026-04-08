import request from '@/utils/request'

// 查询退款/售后单列表
export function listJst_refund_record(query) {
  return request({
    url: '/jst/order/jst_refund_record/list',
    method: 'get',
    params: query
  })
}

// 查询退款/售后单详细
export function getJst_refund_record(refundId) {
  return request({
    url: '/jst/order/jst_refund_record/' + refundId,
    method: 'get'
  })
}

// 新增退款/售后单
export function addJst_refund_record(data) {
  return request({
    url: '/jst/order/jst_refund_record',
    method: 'post',
    data: data
  })
}

// 修改退款/售后单
export function updateJst_refund_record(data) {
  return request({
    url: '/jst/order/jst_refund_record',
    method: 'put',
    data: data
  })
}

// 删除退款/售后单
export function delJst_refund_record(refundId) {
  return request({
    url: '/jst/order/jst_refund_record/' + refundId,
    method: 'delete'
  })
}
