/**
 * 首页聚合接口
 * 对应后端: jst-event/controller/wx/WxHomeController
 * 依赖: FEAT-INDEX-ENRICH-BE
 */
import request from './request'

export function getRecommendContests(options = {}) {
  return request({
    url: '/jst/wx/home/recommend-contests',
    method: 'GET',
    silent: true,
    ...options
  })
}

export function getRecommendCourses(options = {}) {
  return request({
    url: '/jst/wx/home/recommend-courses',
    method: 'GET',
    silent: true,
    ...options
  })
}

export function getHotTags(options = {}) {
  return request({
    url: '/jst/wx/home/hot-tags',
    method: 'GET',
    silent: true,
    ...options
  })
}

export function getTopics(options = {}) {
  return request({
    url: '/jst/wx/home/topics',
    method: 'GET',
    silent: true,
    ...options
  })
}
