import request from '@/utils/request'

// The task card defines /jst/partner/contest/* routes. The current F7 backend
// exposes the same domain capability under /jst/event/contest/*, so these
// partner-facing methods bridge to the existing routes until a wrapper lands.
export function listPartnerContests(params) {
  return request({
    url: '/jst/event/contest/list',
    method: 'get',
    params
  })
}

export function getPartnerContest(id) {
  return request({
    url: `/jst/event/contest/${id}`,
    method: 'get'
  })
}

export function createPartnerContest(data) {
  return request({
    url: '/jst/event/contest/add',
    method: 'post',
    data
  })
}

export function updatePartnerContest(id, data) {
  return request({
    url: '/jst/event/contest/edit',
    method: 'put',
    data: {
      ...data,
      contestId: id
    }
  })
}

export function submitPartnerContest(id) {
  return request({
    url: `/jst/event/contest/${id}/submit`,
    method: 'post'
  })
}

export function offlinePartnerContest(id) {
  return request({
    url: `/jst/event/contest/${id}/offline`,
    method: 'post'
  })
}

export function listPartnerFormTemplates(params) {
  return request({
    url: '/jst/event/form/template/list',
    method: 'get',
    params
  })
}
