import request from '@/utils/request'

// 查询临时参赛档案-允许无正式账号存在列表
export function listJst_participant(query) {
  return request({
    url: '/system/jst_participant/list',
    method: 'get',
    params: query
  })
}

// 查询临时参赛档案-允许无正式账号存在详细
export function getJst_participant(participantId) {
  return request({
    url: '/system/jst_participant/' + participantId,
    method: 'get'
  })
}

// 新增临时参赛档案-允许无正式账号存在
export function addJst_participant(data) {
  return request({
    url: '/system/jst_participant',
    method: 'post',
    data: data
  })
}

// 修改临时参赛档案-允许无正式账号存在
export function updateJst_participant(data) {
  return request({
    url: '/system/jst_participant',
    method: 'put',
    data: data
  })
}

// 删除临时参赛档案-允许无正式账号存在
export function delJst_participant(participantId) {
  return request({
    url: '/system/jst_participant/' + participantId,
    method: 'delete'
  })
}
