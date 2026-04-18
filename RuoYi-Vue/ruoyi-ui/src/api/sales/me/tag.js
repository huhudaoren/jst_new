import request from '@/utils/request'

/** 按渠道查标签列表 */
export function listTags(channelId) {
  return request({ url: '/sales/me/tags', method: 'get', params: { channelId } })
}

/**
 * 添加标签（UK 冲突时返友好错误）。
 * @param {{ channelId: number, tagCode: string, tagColor: string }} data
 */
export function addTag(data) {
  return request({ url: '/sales/me/tags', method: 'post', data })
}

/** 删除标签（仅创建者或 admin 可删） */
export function removeTag(id) {
  return request({ url: `/sales/me/tags/${id}`, method: 'delete' })
}
