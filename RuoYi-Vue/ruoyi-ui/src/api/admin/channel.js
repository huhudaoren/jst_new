import request from '@/utils/request'

// 渠道列表
export function listChannel(query) {
  return request({ url: '/jst/user/jst_channel/list', method: 'get', params: query })
}

// 渠道详情
export function getChannel(channelId) {
  return request({ url: '/jst/user/jst_channel/' + channelId, method: 'get' })
}

// 修改渠道（暂停/恢复等）
export function updateChannel(data) {
  return request({ url: '/jst/user/jst_channel', method: 'put', data })
}
