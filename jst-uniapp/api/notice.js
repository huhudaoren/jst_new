/**
 * 公告相关接口
 * 对应后端: jst-message/controller/wx/WxNoticeController + WxIndexController + WxDictController
 * 文档: 架构设计/27-用户端API契约.md §3.2 / §3.9
 */
import request from './request'

export function getNoticeList(params = {}, options = {}) {
  return request({
    url: '/jst/wx/notice/list',
    method: 'GET',
    data: params,
    silent: true,
    ...options
  })
}

export function getNoticeDetail(id, options = {}) {
  return request({
    url: `/jst/wx/notice/${id}`,
    method: 'GET',
    silent: true,
    ...options
  })
}

export function getBanner(options = {}) {
  return request({
    url: '/jst/wx/index/banner',
    method: 'GET',
    silent: true,
    ...options
  })
}

export function getDict(dictType, options = {}) {
  return request({
    url: `/jst/wx/dict/${dictType}`,
    method: 'GET',
    silent: true,
    ...options
  })
}
