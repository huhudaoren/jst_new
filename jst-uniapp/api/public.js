/**
 * 公开页相关接口
 * 对应后端: jst-organizer/controller/public_/PublicPartnerApplyController
 * 文档: 子任务 P5《公开页面集》
 */
import request from './request'

export function submitPartnerApply(data, options = {}) {
  return request({
    url: '/jst/public/organizer/partner/apply',
    method: 'POST',
    data,
    ...options
  })
}

export function queryApplyStatus(applyNo, options = {}) {
  return request({
    url: `/jst/public/organizer/partner/apply/${applyNo}/status`,
    method: 'GET',
    silent: true,
    ...options
  })
}
