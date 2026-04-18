import request from '@/utils/request'

/**
 * 销售本人跟进记录列表（支持 channelId / followupType / dateFrom / dateTo 过滤）。
 */
export function listMine(query) {
  return request({ url: '/sales/me/followup/list', method: 'get', params: query })
}

/** 按渠道查跟进记录（时间倒序，用于 FollowupTimeline） */
export function listByChannel(channelId) {
  return request({ url: `/sales/me/followup/by-channel/${channelId}`, method: 'get' })
}

/**
 * 新建跟进记录。
 * @param {{ channelId, followupType, followupAt, content, mood, attachmentUrls, nextFollowupAt }} data
 */
export function create(data) {
  return request({ url: '/sales/me/followup', method: 'post', data })
}

/**
 * 更新跟进记录（24h 内可改）。
 */
export function update(recordId, data) {
  return request({ url: `/sales/me/followup/${recordId}`, method: 'put', data })
}

/** 删除跟进记录（24h 内可删） */
export function remove(recordId) {
  return request({ url: `/sales/me/followup/${recordId}`, method: 'delete' })
}
