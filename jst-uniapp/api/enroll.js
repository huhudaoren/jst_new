/**
 * 报名相关接口
 * 对应后端: jst-event/controller/wx/WxEnrollController
 */
import request from './request'

export function getEnrollTemplate(contestId, options = {}) {
  return request({
    url: '/jst/wx/enroll/template',
    method: 'GET',
    data: { contestId },
    ...options
  })
}

export function saveEnrollDraft(data, options = {}) {
  return request({
    url: '/jst/wx/enroll/draft',
    method: 'POST',
    data,
    ...options
  })
}

export function submitEnroll(data, options = {}) {
  return request({
    url: '/jst/wx/enroll/submit',
    method: 'POST',
    data,
    ...options
  })
}

export function getEnrollDetail(id, options = {}) {
  return request({
    url: `/jst/wx/enroll/${id}`,
    method: 'GET',
    ...options
  })
}

export function supplementEnroll(id, data, options = {}) {
  return request({
    url: `/jst/wx/enroll/${id}/supplement`,
    method: 'POST',
    data,
    ...options
  })
}

export function getMyEnrolls(params = {}, options = {}) {
  return request({
    url: '/jst/wx/enroll/my',
    method: 'GET',
    data: params,
    ...options
  })
}
