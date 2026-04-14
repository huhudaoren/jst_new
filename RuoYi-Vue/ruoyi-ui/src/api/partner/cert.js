import request from '@/utils/request'

export function saveCertTemplate(data) {
  return request({
    url: '/jst/partner/cert/template',
    method: 'post',
    data
  })
}

export function uploadCertTemplate(data) {
  return request({
    url: '/jst/partner/cert/template',
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function listCertTemplates() {
  return request({
    url: '/jst/partner/cert/template/list',
    method: 'get'
  })
}

export function listPartnerCerts(params) {
  return request({
    url: '/jst/partner/cert/list',
    method: 'get',
    params
  })
}

export function batchGrantCerts(data) {
  return request({
    url: '/jst/partner/cert/batch-grant',
    method: 'post',
    data
  })
}

export function submitCertReview(data) {
  return request({
    url: '/jst/partner/cert/submit-review',
    method: 'post',
    data
  })
}

export function previewCert(certId) {
  return request({
    url: `/jst/partner/cert/${certId}/preview`,
    method: 'get'
  })
}

export function getCertTemplate(templateId) {
  return request({
    url: `/jst/partner/cert/template/${templateId}`,
    method: 'get'
  })
}

export function updateCertTemplate(templateId, data) {
  return request({
    url: `/jst/partner/cert/template/${templateId}`,
    method: 'put',
    data
  })
}
