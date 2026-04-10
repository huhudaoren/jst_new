/**
 * 证书相关接口
 * 对应后端: jst-event/controller/wx/WxCertController
 * 文档: 架构设计/27-用户端API契约.md §3.8
 */
import request from './request'

// 我的证书列表
export function getMyCertList(options = {}) {
  return request({
    url: '/jst/wx/cert/my',
    method: 'GET',
    silent: true,
    ...options
  })
}

// 证书详情
export function getCertDetail(id, options = {}) {
  return request({
    url: `/jst/wx/cert/${id}`,
    method: 'GET',
    silent: true,
    ...options
  })
}

// 证书分享链接
export function shareCert(id, options = {}) {
  return request({
    url: `/jst/wx/cert/${id}/share`,
    method: 'POST',
    ...options
  })
}

// 公开证书验真(无需登录)
export function verifyPublicCert(params = {}, options = {}) {
  return request({
    url: '/jst/public/cert/verify',
    method: 'GET',
    data: params,
    silent: true,
    ...options
  })
}
