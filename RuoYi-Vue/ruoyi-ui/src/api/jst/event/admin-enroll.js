import request from '@/utils/request'

// 管理端-报名列表
export function listAdminEnrolls(query) {
  return request({
    url: '/jst/event/enroll/list',
    method: 'get',
    params: query
  })
}

// 管理端-报名详情
export function getAdminEnrollDetail(enrollId) {
  return request({
    url: '/jst/event/enroll/' + enrollId,
    method: 'get'
  })
}

// 管理端-报名审核
export function auditAdminEnroll(enrollId, data) {
  return request({
    url: '/jst/event/enroll/' + enrollId + '/audit',
    method: 'post',
    data
  })
}
