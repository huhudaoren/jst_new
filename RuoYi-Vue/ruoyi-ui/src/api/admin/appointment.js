import request from '@/utils/request'

// 预约记录列表
export function listAppointment(query) {
  return request({ url: '/jst/order/jst_appointment_record/list', method: 'get', params: query })
}

// 预约记录详情
export function getAppointment(appointmentId) {
  return request({ url: '/jst/order/jst_appointment_record/' + appointmentId, method: 'get' })
}

// 删除/取消预约
export function delAppointment(appointmentId) {
  return request({ url: '/jst/order/jst_appointment_record/' + appointmentId, method: 'delete' })
}
