/**
 * 赛事相关接口
 * 文档：架构设计/27-用户端API契约.md §3.2
 */
import request from './request'

export function getContestList(params, options = {}) {
  return request({
    url: '/jst/wx/contest/list',
    method: 'GET',
    data: params,
    ...options
  })
}

export function getContestDetail(id, options = {}) {
  return request({
    url: `/jst/wx/contest/${id}`,
    method: 'GET',
    ...options
  })
}

export function getContestHot(limit = 6, options = {}) {
  return request({
    url: '/jst/wx/contest/hot',
    method: 'GET',
    data: { limit },
    ...options
  })
}

export function getContestRecommend(contestId, options = {}) {
  return request({
    url: `/jst/wx/contest/${contestId}/recommend`,
    method: 'GET',
    ...options
  })
}

export function getContestCategories(options = {}) {
  return request({
    url: '/jst/wx/contest/categories',
    method: 'GET',
    ...options
  })
}
