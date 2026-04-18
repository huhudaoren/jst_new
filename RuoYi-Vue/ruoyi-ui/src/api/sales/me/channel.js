import request from '@/utils/request'

/**
 * 我的渠道列表（通过 binding 查销售名下当前渠道）。
 * 后端基于 jst_sales_channel_binding LEFT JOIN jst_channel。
 * plan-04 会补 /sales/me/channels 专属端点；此处先走 admin 渠道列表 + salesId 过滤。
 */
export function listMyChannels(query) {
  return request({ url: '/sales/me/channels/list', method: 'get', params: query })
}

/** 渠道详情（复用 admin 渠道详情接口）*/
export function getChannelDetail(channelId) {
  return request({ url: '/jst/user/jst_channel/' + channelId, method: 'get' })
}

/**
 * 查看完整手机号（脱敏解密）。
 * plan-04 补 /sales/me/channels/{id}/phone-full；此处暂返回 channelDetail 中的 contactMobile。
 */
export function getPhoneFull(channelId) {
  return request({ url: '/sales/me/channels/' + channelId + '/phone-full', method: 'get' })
}
