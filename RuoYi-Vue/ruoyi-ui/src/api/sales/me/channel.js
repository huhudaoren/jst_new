import request from '@/utils/request'

/** 我的渠道列表（binding 查当前归属渠道，admin 可见全量）*/
export function listMyChannels(query) {
  return request({ url: '/sales/me/channels/list', method: 'get', params: query })
}

/** 渠道详情 */
export function getChannelDetail(channelId) {
  return request({ url: '/sales/me/channels/' + channelId, method: 'get' })
}

/** 查看完整手机号（写审计日志） */
export function getPhoneFull(channelId) {
  return request({ url: '/sales/me/channels/' + channelId + '/phone-full', method: 'get' })
}

/** 渠道画像（跟进次数/成交笔数/业务类型/近6月趋势）*/
export function getChannelProfile(channelId) {
  return request({ url: '/sales/me/channels/' + channelId + '/profile', method: 'get' })
}
