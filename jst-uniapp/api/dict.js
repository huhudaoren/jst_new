/**
 * 字典分类接口
 * 对应后端: jst-message/controller/wx/WxDictController
 */
import request from './request'

/**
 * 获取赛事分类字典
 * @returns {Promise<Array<{label: string, value: string}>>}
 */
export function getContestCategories(options = {}) {
  return request({
    url: '/jst/wx/dict/contest-categories',
    method: 'GET',
    ...options
  })
}

/**
 * 获取课程分类字典
 * @returns {Promise<Array<{label: string, value: string}>>}
 */
export function getCourseCategories(options = {}) {
  return request({
    url: '/jst/wx/dict/course-categories',
    method: 'GET',
    ...options
  })
}
