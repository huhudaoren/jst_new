/**
 * 成绩相关接口
 * 对应后端: jst-event/controller/wx/WxScoreController
 * 文档: 架构设计/27-用户端API契约.md §3.8
 */
import request from './request'

// 我的成绩列表
export function getMyScoreList(options = {}) {
  return request({
    url: '/jst/wx/score/my',
    method: 'GET',
    silent: true,
    ...options
  })
}

// 成绩详情
export function getScoreDetail(id, options = {}) {
  return request({
    url: `/jst/wx/score/${id}`,
    method: 'GET',
    silent: true,
    ...options
  })
}

// 公开成绩查询(无需登录)
export function queryPublicScore(params = {}, options = {}) {
  return request({
    url: '/jst/public/score/query',
    method: 'GET',
    data: params,
    silent: true,
    ...options
  })
}
