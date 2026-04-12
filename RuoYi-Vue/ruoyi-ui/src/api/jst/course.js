import request from '@/utils/request'

export function listCourse(query) {
  return request({ url: '/jst/event/course/list', method: 'get', params: query })
}

export function getCourse(courseId) {
  return request({ url: '/jst/event/course/' + courseId, method: 'get' })
}

export function addCourse(data) {
  return request({ url: '/jst/event/course/add', method: 'post', data })
}

export function editCourse(data) {
  return request({ url: '/jst/event/course/edit', method: 'put', data })
}

export function submitCourse(courseId) {
  return request({ url: '/jst/event/course/' + courseId + '/submit', method: 'post' })
}

export function approveCourse(courseId) {
  return request({ url: '/jst/event/course/' + courseId + '/audit/approve', method: 'post' })
}

export function rejectCourse(courseId) {
  return request({ url: '/jst/event/course/' + courseId + '/audit/reject', method: 'post' })
}

export function onCourse(courseId) {
  return request({ url: '/jst/event/course/' + courseId + '/on', method: 'post' })
}

export function offCourse(courseId) {
  return request({ url: '/jst/event/course/' + courseId + '/off', method: 'post' })
}
