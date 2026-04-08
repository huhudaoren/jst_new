import request from '@/utils/request'

// 查询发票记录列表
export function listJst_invoice_record(query) {
  return request({
    url: '/system/jst_invoice_record/list',
    method: 'get',
    params: query
  })
}

// 查询发票记录详细
export function getJst_invoice_record(invoiceId) {
  return request({
    url: '/system/jst_invoice_record/' + invoiceId,
    method: 'get'
  })
}

// 新增发票记录
export function addJst_invoice_record(data) {
  return request({
    url: '/system/jst_invoice_record',
    method: 'post',
    data: data
  })
}

// 修改发票记录
export function updateJst_invoice_record(data) {
  return request({
    url: '/system/jst_invoice_record',
    method: 'put',
    data: data
  })
}

// 删除发票记录
export function delJst_invoice_record(invoiceId) {
  return request({
    url: '/system/jst_invoice_record/' + invoiceId,
    method: 'delete'
  })
}
