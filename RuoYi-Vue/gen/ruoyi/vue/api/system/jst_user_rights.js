import request from '@/utils/request'

// 查询用户权益持有列表
export function listJst_user_rights(query) {
  return request({
    url: '/system/jst_user_rights/list',
    method: 'get',
    params: query
  })
}

// 查询用户权益持有详细
export function getJst_user_rights(userRightsId) {
  return request({
    url: '/system/jst_user_rights/' + userRightsId,
    method: 'get'
  })
}

// 新增用户权益持有
export function addJst_user_rights(data) {
  return request({
    url: '/system/jst_user_rights',
    method: 'post',
    data: data
  })
}

// 修改用户权益持有
export function updateJst_user_rights(data) {
  return request({
    url: '/system/jst_user_rights',
    method: 'put',
    data: data
  })
}

// 删除用户权益持有
export function delJst_user_rights(userRightsId) {
  return request({
    url: '/system/jst_user_rights/' + userRightsId,
    method: 'delete'
  })
}
