import request from '@/utils/request'

export function listPartnerEnrolls(params) {
  return request({
    url: '/jst/partner/enroll/list',
    method: 'get',
    params
  })
}

export function getPartnerEnrollDetail(enrollId) {
  return request({
    url: `/jst/partner/enroll/${enrollId}`,
    method: 'get'
  })
}

export function auditPartnerEnroll(enrollId, data) {
  return request({
    url: `/jst/partner/enroll/${enrollId}/audit`,
    method: 'put',
    data
  })
}

export function batchAuditEnrolls(data) {
  return request({
    url: '/jst/partner/enroll/batch-audit',
    method: 'put',
    data
  })
}

export function listScoreItems(contestId) {
  return request({
    url: `/jst/partner/contest/${contestId}/score-items`,
    method: 'get'
  })
}
