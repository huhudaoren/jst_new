import request from '@/utils/request'

// 查询赛事主列表
export function listJst_contest(query) {
  return request({
    url: '/system/jst_contest/list',
    method: 'get',
    params: query
  })
}

// 查询赛事主详细
export function getJst_contest(contestId) {
  return request({
    url: '/system/jst_contest/' + contestId,
    method: 'get'
  })
}

// 新增赛事主
export function addJst_contest(data) {
  return request({
    url: '/system/jst_contest',
    method: 'post',
    data: data
  })
}

// 修改赛事主
export function updateJst_contest(data) {
  return request({
    url: '/system/jst_contest',
    method: 'put',
    data: data
  })
}

// 删除赛事主
export function delJst_contest(contestId) {
  return request({
    url: '/system/jst_contest/' + contestId,
    method: 'delete'
  })
}
