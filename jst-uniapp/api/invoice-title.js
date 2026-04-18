/**
 * 发票抬头管理 API (Round 2B · B3)
 * 对应后端: jst-user/controller/wx/WxInvoiceTitleController (分支 feat/backend-round-2b-b1-b3)
 * 路径约定与地址管理一致 /jst/wx/user/invoice-title/*
 * 字段: { titleId, userId, titleType(personal|company), titleName, taxNo, isDefault, createTime }
 */
import request from './request'

/** 当前用户的发票抬头列表 (按 is_default DESC, create_time DESC) */
export function listInvoiceTitles(options = {}) {
  return request({
    url: '/jst/wx/user/invoice-title/list',
    method: 'GET',
    ...options
  })
}

/** 新增或更新: 带 titleId 为更新, 无则新增 */
export function saveInvoiceTitle(data, options = {}) {
  return request({
    url: '/jst/wx/user/invoice-title',
    method: 'POST',
    data,
    ...options
  })
}

/** 删除发票抬头 */
export function deleteInvoiceTitle(id, options = {}) {
  return request({
    url: `/jst/wx/user/invoice-title/${id}`,
    method: 'DELETE',
    ...options
  })
}

/** 设为默认抬头 */
export function setDefaultInvoiceTitle(id, options = {}) {
  return request({
    url: `/jst/wx/user/invoice-title/${id}/default`,
    method: 'POST',
    ...options
  })
}
