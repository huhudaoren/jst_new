import request from '@/utils/request'

// 查询用户主-学生/家长/渠道方业务账号列表
export function listJst_user(query) {
  return request({
    url: '/system/jst_user/list',
    method: 'get',
    params: query
  })
}

// 查询用户主-学生/家长/渠道方业务账号详细
export function getJst_user(userId) {
  return request({
    url: '/system/jst_user/' + userId,
    method: 'get'
  })
}

// 新增用户主-学生/家长/渠道方业务账号
export function addJst_user(data) {
  return request({
    url: '/system/jst_user',
    method: 'post',
    data: data
  })
}

// 修改用户主-学生/家长/渠道方业务账号
export function updateJst_user(data) {
  return request({
    url: '/system/jst_user',
    method: 'put',
    data: data
  })
}

// 删除用户主-学生/家长/渠道方业务账号
export function delJst_user(userId) {
  return request({
    url: '/system/jst_user/' + userId,
    method: 'delete'
  })
}
