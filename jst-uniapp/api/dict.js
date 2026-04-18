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

/**
 * PATCH-5: 通用字典拉取（dict_type 需 jst_ 前缀）
 * 返回 [{label, value, cssClass, listClass, ...}]
 * @param {string} dictType 如 'jst_region_province'
 */
export function getDict(dictType, options = {}) {
  return request({
    url: `/jst/wx/dict/${dictType}`,
    method: 'GET',
    ...options
  })
}
