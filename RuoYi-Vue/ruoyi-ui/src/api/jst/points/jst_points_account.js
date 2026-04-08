import request from '@/utils/request'

// 查询积分账户列表
export function listJst_points_account(query) {
  return request({
    url: '/jst/points/jst_points_account/list',
    method: 'get',
    params: query
  })
}

// 查询积分账户详细
export function getJst_points_account(accountId) {
  return request({
    url: '/jst/points/jst_points_account/' + accountId,
    method: 'get'
  })
}

// 新增积分账户
export function addJst_points_account(data) {
  return request({
    url: '/jst/points/jst_points_account',
    method: 'post',
    data: data
  })
}

// 修改积分账户
export function updateJst_points_account(data) {
  return request({
    url: '/jst/points/jst_points_account',
    method: 'put',
    data: data
  })
}

// 删除积分账户
export function delJst_points_account(accountId) {
  return request({
    url: '/jst/points/jst_points_account/' + accountId,
    method: 'delete'
  })
}
