import request from '@/utils/request'

// 查询团队预约主记录列表
export function listJst_team_appointment(query) {
  return request({
    url: '/jst/order/jst_team_appointment/list',
    method: 'get',
    params: query
  })
}

// 查询团队预约主记录详细
export function getJst_team_appointment(teamAppointmentId) {
  return request({
    url: '/jst/order/jst_team_appointment/' + teamAppointmentId,
    method: 'get'
  })
}

// 新增团队预约主记录
export function addJst_team_appointment(data) {
  return request({
    url: '/jst/order/jst_team_appointment',
    method: 'post',
    data: data
  })
}

// 修改团队预约主记录
export function updateJst_team_appointment(data) {
  return request({
    url: '/jst/order/jst_team_appointment',
    method: 'put',
    data: data
  })
}

// 删除团队预约主记录
export function delJst_team_appointment(teamAppointmentId) {
  return request({
    url: '/jst/order/jst_team_appointment/' + teamAppointmentId,
    method: 'delete'
  })
}
