import request from '@/utils/request'

// 查询团队预约成员明细列表
export function listJst_team_appointment_member(query) {
  return request({
    url: '/jst/order/jst_team_appointment_member/list',
    method: 'get',
    params: query
  })
}

// 查询团队预约成员明细详细
export function getJst_team_appointment_member(memberId) {
  return request({
    url: '/jst/order/jst_team_appointment_member/' + memberId,
    method: 'get'
  })
}

// 新增团队预约成员明细
export function addJst_team_appointment_member(data) {
  return request({
    url: '/jst/order/jst_team_appointment_member',
    method: 'post',
    data: data
  })
}

// 修改团队预约成员明细
export function updateJst_team_appointment_member(data) {
  return request({
    url: '/jst/order/jst_team_appointment_member',
    method: 'put',
    data: data
  })
}

// 删除团队预约成员明细
export function delJst_team_appointment_member(memberId) {
  return request({
    url: '/jst/order/jst_team_appointment_member/' + memberId,
    method: 'delete'
  })
}
