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

export function onlinePartnerContest(id) {
  return request({
    url: `/jst/partner/contest/${id}/online`,
    method: 'put'
  })
}

export function copyPartnerContest(id) {
  return request({
    url: `/jst/partner/contest/copy/${id}`,
    method: 'post'
  })
}

export function listPartnerFormTemplates(params) {
  return request({
    url: '/jst/partner/form-template/list',
    method: 'get',
    params
  })
}

export function createFormTemplate(data) {
  return request({
    url: '/jst/event/jst_enroll_form_template',
    method: 'post',
    data
  })
}

export function listContestReviewers(contestId) {
  return request({
    url: `/jst/partner/contest/${contestId}/reviewers`,
    method: 'get'
  })
}

export function saveContestReviewers(contestId, data) {
  return request({
    url: `/jst/partner/contest/${contestId}/reviewers`,
    method: 'post',
    data
  })
}

export function searchSystemUsers(query) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: { userName: query, pageNum: 1, pageSize: 20 }
  })
}
