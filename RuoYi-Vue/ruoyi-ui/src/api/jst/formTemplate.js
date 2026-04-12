import request from '@/utils/request'

export function listFormTemplate(query) {
  return request({ url: '/jst/event/form/template/list', method: 'get', params: query })
}

export function getFormTemplate(templateId) {
  return request({ url: '/jst/event/form/template/' + templateId, method: 'get' })
}

export function saveFormTemplate(data) {
  return request({ url: '/jst/event/form/template/save', method: 'post', data })
}

export function submitFormTemplate(templateId) {
  return request({ url: '/jst/event/form/template/' + templateId + '/submit', method: 'post' })
}

export function approveFormTemplate(templateId, data) {
  return request({ url: '/jst/event/form/template/' + templateId + '/audit/approve', method: 'post', data })
}

export function rejectFormTemplate(templateId, data) {
  return request({ url: '/jst/event/form/template/' + templateId + '/audit/reject', method: 'post', data })
}
