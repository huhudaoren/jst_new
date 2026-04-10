import request from '@/utils/request'

export function importPartnerScores(data) {
  return request({
    url: '/jst/partner/score/import',
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function listPartnerScores(params) {
  return request({
    url: '/jst/partner/score/list',
    method: 'get',
    params
  })
}

export function getPartnerScoreStats(params) {
  return request({
    url: '/jst/partner/score/stats',
    method: 'get',
    params
  })
}

export function savePartnerScore(id, data) {
  return request({
    url: `/jst/partner/score/${id || 0}`,
    method: 'put',
    data
  })
}

export function submitPartnerScoreReview(data) {
  return request({
    url: '/jst/partner/score/submit-review',
    method: 'put',
    data
  })
}

export function listScoreCorrections(params) {
  return request({
    url: '/jst/partner/score/correction/apply',
    method: 'get',
    params
  })
}

export function applyScoreCorrection(data) {
  return request({
    url: '/jst/partner/score/correction/apply',
    method: 'post',
    data
  })
}
