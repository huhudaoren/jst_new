import request from '@/utils/request'

// 查询课程列表
export function listJst_course(query) {
  return request({
    url: '/jst/event/jst_course/list',
    method: 'get',
    params: query
  })
}

// 查询课程详细
export function getJst_course(courseId) {
  return request({
    url: '/jst/event/jst_course/' + courseId,
    method: 'get'
  })
}

// 新增课程
export function addJst_course(data) {
  return request({
    url: '/jst/event/jst_course',
    method: 'post',
    data: data
  })
}

// 修改课程
export function updateJst_course(data) {
  return request({
    url: '/jst/event/jst_course',
    method: 'put',
    data: data
  })
}

// 删除课程
export function delJst_course(courseId) {
  return request({
    url: '/jst/event/jst_course/' + courseId,
    method: 'delete'
  })
}
