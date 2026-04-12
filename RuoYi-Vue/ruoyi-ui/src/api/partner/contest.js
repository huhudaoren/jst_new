import request from '@/utils/request'

export function listPartnerContests(params) {
  return request({
    url: '/jst/partner/contest/list',
    method: 'get',
    params
  })
}

export function getPartnerContest(id) {
  return request({
    url: `/jst/partner/contest/${id}`,
    method: 'get'
  })
}

export function createPartnerContest(data) {
  return request({
    url: '/jst/partner/contest',
    method: 'post',
    data
  })
}

export function updatePartnerContest(id, data) {
  return request({
    url: `/jst/partner/contest/${id}`,
    method: 'put',
    data
  })
}

export function submitPartnerContest(id) {
  return request({
    url: `/jst/partner/contest/${id}/submit`,
    method: 'put'
  })
}

export function offlinePartnerContest(id) {
  return request({
    url: `/jst/partner/contest/${id}/offline`,
    method: 'put'
  })
}

export function listPartnerFormTemplates(params) {
  return request({
    url: '/jst/partner/form-template/list',
    method: 'get',
    params
  })
}
