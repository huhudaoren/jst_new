import request from '@/utils/request'

export function listNotice(query) {
  return request({ url: '/jst/message/notice/list', method: 'get', params: query })
}

export function addNotice(data) {
  return request({ url: '/jst/message/notice/add', method: 'post', data })
}

export function editNotice(data) {
  return request({ url: '/jst/message/notice/edit', method: 'put', data })
}

export function publishNotice(noticeId) {
  return request({ url: '/jst/message/notice/' + noticeId + '/publish', method: 'post' })
}

export function offlineNotice(noticeId) {
  return request({ url: '/jst/message/notice/' + noticeId + '/offline', method: 'post' })
}
