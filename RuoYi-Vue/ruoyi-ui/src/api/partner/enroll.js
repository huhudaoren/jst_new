import request from '@/utils/request'

function auditByCurrentBackend(enrollId, data) {
  return request({
    url: `/jst/event/enroll/${enrollId}/audit`,
    method: 'post',
    data
  })
}

// Task card expects /jst/partner/enroll/*, but the current F9 backend only
// exposes /jst/event/enroll/*. Keep the partner-facing API names here so the
// page can switch to the dedicated route later with minimal changes.
export function listPartnerEnrolls(params) {
  return request({
    url: '/jst/event/enroll/list',
    method: 'get',
    params
  })
}

export function getPartnerEnrollDetail(enrollId) {
  return request({
    url: `/jst/event/enroll/${enrollId}`,
    method: 'get'
  })
}

export function auditPartnerEnroll(enrollId, data) {
  return auditByCurrentBackend(enrollId, data)
}

export function batchAuditEnrolls(data) {
  const enrollIds = Array.isArray(data && data.enrollIds)
    ? data.enrollIds.filter(item => item !== null && item !== undefined && item !== '')
    : []
  if (!enrollIds.length) {
    return Promise.resolve([])
  }
  return Promise.all(enrollIds.map(enrollId => auditByCurrentBackend(enrollId, {
    result: data.result,
    auditRemark: data.auditRemark
  })))
}
