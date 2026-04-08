/**
 * 课程相关接口
 * 对应后端: jst-event/controller/wx/WxCourseController
 * 文档: 架构设计/27-用户端API契约.md §3.3
 */
import request from './request'

/**
 * 课程列表
 * @param {Object} params { courseType, pageNum, pageSize }
 * @param {Object} options request 扩展参数，如 silent
 */
export function getCourseList(params, options = {}) {
  return request({
    url: '/jst/wx/course/list',
    method: 'GET',
    data: params,
    ...options
  })
}

/**
 * 课程详情
 * @param {string|number} id 课程 ID
 * @param {Object} options request 扩展参数，如 silent
 */
export function getCourseDetail(id, options = {}) {
  return request({
    url: `/jst/wx/course/${id}`,
    method: 'GET',
    ...options
  })
}

/**
 * 我的课程
 * @param {Object} options request 扩展参数，如 silent
 */
export function getMyCourses(options = {}) {
  return request({
    url: '/jst/wx/course/my',
    method: 'GET',
    ...options
  })
}
