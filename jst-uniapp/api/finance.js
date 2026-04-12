/**
 * 合同 + 发票 API 封装 (F-CONTRACT-INVOICE-FE)
 * 对应后端: WxContractController / WxInvoiceController
 * 约束: 必须经 @/api/request 统一封装, 禁止裸 uni.request
 */
import request from '@/api/request'

// ────── 合同 ──────

/** 合同列表 (分页) */
export function getContractList(params) {
  return request({ url: '/jst/wx/contract/list', method: 'GET', data: params })
}

/** 合同详情 */
export function getContractDetail(contractId) {
  return request({ url: `/jst/wx/contract/${contractId}`, method: 'GET' })
}

// ────── 发票 ──────

/** 发票列表 (分页) */
export function getInvoiceList(params) {
  return request({ url: '/jst/wx/invoice/list', method: 'GET', data: params })
}

/** 发票详情 */
export function getInvoiceDetail(invoiceId) {
  return request({ url: `/jst/wx/invoice/${invoiceId}`, method: 'GET' })
}

/** 申请开票 */
export function applyInvoice(data) {
  return request({ url: '/jst/wx/invoice/apply', method: 'POST', data })
}
