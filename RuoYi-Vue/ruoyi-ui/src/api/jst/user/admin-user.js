import request from '@/utils/request'

// 管理端-用户列表
export function listAdminUsers(query) {
  return request({
    url: '/jst/user/jst_user/list',
    method: 'get',
    params: query
  })
}

// 管理端-用户详情
export function getAdminUser(userId) {
  return request({
    url: '/jst/user/jst_user/' + userId,
    method: 'get'
  })
}

// 管理端-更新用户（用于启用/禁用）
export function updateAdminUser(data) {
  return request({
    url: '/jst/user/jst_user',
    method: 'put',
    data
  })
}

// 管理端-学生渠道绑定列表
export function listUserBindings(query) {
  return request({
    url: '/jst/user/binding/list',
    method: 'get',
    params: query
  })
}

// 管理端-强制解绑
export function forceUnbindBinding(bindingId, reason) {
  return request({
    url: '/jst/user/binding/' + bindingId + '/force-unbind',
    method: 'post',
    params: { reason }
  })
}

// 管理端-积分流水列表
export function listUserPointsLedger(query) {
  return request({
    url: '/jst/points/jst_points_ledger/list',
    method: 'get',
    params: query
  })
}

// 管理端-参赛档案认领映射列表
export function listParticipantMaps(query) {
  return request({
    url: '/system/jst_participant_user_map/list',
    method: 'get',
    params: query
  })
}
