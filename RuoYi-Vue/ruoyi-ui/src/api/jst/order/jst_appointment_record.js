import request from '@/utils/request'

// 查询个人预约记录列表
export function listJst_appointment_record(query) {
  return request({
    url: '/jst/order/jst_appointment_record/list',
    method: 'get',
    params: query
  })
}

// 查询个人预约记录详细
export function getJst_appointment_record(appointmentId) {
  return request({
    url: '/jst/order/jst_appointment_record/' + appointmentId,
    method: 'get'
  })
}

// 新增个人预约记录
export function addJst_appointment_record(data) {
  return request({
    url: '/jst/order/jst_appointment_record',
    method: 'post',
    data: data
  })
}

// 修改个人预约记录
export function updateJst_appointment_record(data) {
  return request({
    url: '/jst/order/jst_appointment_record',
    method: 'put',
    data: data
  })
}

// 删除个人预约记录
export function delJst_appointment_record(appointmentId) {
  return request({
    url: '/jst/order/jst_appointment_record/' + appointmentId,
    method: 'delete'
  })
}
