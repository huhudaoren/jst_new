/**
 * 个人预约 + 扫码核销 API (WX-C3)
 * 对应后端: WxAppointmentController (/jst/wx/appointment/*)
 *           WxWriteoffController (/jst/wx/writeoff/*)
 * 契约来源: RuoYi-Vue/jst-event/**\/controller/wx + dto/vo + test/wx-tests.http § C6/C7
 */
import request from '@/api/request'

/**
 * 申请预约
 * @param {Object} body { contestId, sessionCode, appointmentDate:'yyyy-MM-dd', participantId?, couponId?, pointsToUse? }
 * @returns { appointmentId, appointmentNo, orderId?, orderNo?, orderStatus?, appointmentStatus, qrCodes:string[] }
 */
export function applyAppointment(body) {
  return request({ url: '/jst/wx/appointment/apply', method: 'POST', data: body })
}

/** 取消预约（学生自己的单） */
export function cancelAppointment(appointmentId) {
  return request({ url: `/jst/wx/appointment/${appointmentId}/cancel`, method: 'POST' })
}

/**
 * 查询场次剩余名额
 * @param {Number|String} contestId
 * @param {Object} params { appointmentDate:'yyyy-MM-dd', sessionCode }
 */
export function getRemaining(contestId, params) {
  return request({
    url: `/jst/wx/appointment/contest/${contestId}/remaining`,
    method: 'GET',
    data: params
  })
}

/** 我的预约列表 (返回 List，非分页) */
export function getMyAppointments() {
  return request({ url: '/jst/wx/appointment/my', method: 'GET' })
}

/** 预约详情 (含 writeoffItems + 可能的 top-level qrCode) */
export function getAppointmentDetail(appointmentId) {
  return request({ url: `/jst/wx/appointment/${appointmentId}`, method: 'GET' })
}

/**
 * 扫码核销
 * 权限: jst_partner / jst_platform_op
 * @param {Object} body { qrCode, scanType?, terminal? }
 */
export function scanWriteoff(body) {
  return request({ url: '/jst/wx/writeoff/scan', method: 'POST', data: body })
}

/**
 * 核销记录（分页）
 * @param {Object} params { pageNum, pageSize, contestId?, ... }
 */
export function getWriteoffRecords(params) {
  return request({ url: '/jst/wx/writeoff/records', method: 'GET', data: params })
}

/* ---------------- 团队预约 (E1-CH-7) ---------------- */

/**
 * 创建团队预约
 * @param {Object} body { contestId, sessionCode, appointmentDate, studentIds:[], extraCount?, extraRemark? }
 */
export function createTeamAppointment(body) {
  return request({ url: '/jst/wx/team-appointment/apply', method: 'POST', data: body })
}

/** 团队预约详情 */
export function getTeamAppointmentDetail(id) {
  return request({ url: `/jst/wx/team-appointment/${id}`, method: 'GET' })
}
