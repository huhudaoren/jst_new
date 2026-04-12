import request from '@/utils/request'

// 管理端-赛事列表
export function listAdminContests(query) {
  return request({
    url: '/jst/event/contest/list',
    method: 'get',
    params: query
  })
}

// 管理端-赛事详情
export function getAdminContest(contestId) {
  return request({
    url: '/jst/event/contest/' + contestId,
    method: 'get'
  })
}

// 管理端-赛事编辑
export function updateAdminContest(data) {
  return request({
    url: '/jst/event/contest/edit',
    method: 'put',
    data
  })
}

// 管理端-赛事审核通过
export function approveAdminContest(contestId, data) {
  return request({
    url: '/jst/event/contest/' + contestId + '/audit/approve',
    method: 'post',
    data
  })
}

// 管理端-赛事审核驳回
export function rejectAdminContest(contestId, data) {
  return request({
    url: '/jst/event/contest/' + contestId + '/audit/reject',
    method: 'post',
    data
  })
}

// 管理端-赛事上线
export function onlineAdminContest(contestId) {
  return request({
    url: '/jst/event/contest/' + contestId + '/online',
    method: 'post'
  })
}

// 管理端-赛事下线
export function offlineAdminContest(contestId) {
  return request({
    url: '/jst/event/contest/' + contestId + '/offline',
    method: 'post'
  })
}
