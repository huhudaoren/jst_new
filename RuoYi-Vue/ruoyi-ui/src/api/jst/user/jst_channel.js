import request from '@/utils/request'

// 查询渠道方档案列表
export function listJst_channel(query) {
  return request({
    url: '/jst/user/jst_channel/list',
    method: 'get',
    params: query
  })
}

// 查询渠道方档案详细
export function getJst_channel(channelId) {
  return request({
    url: '/jst/user/jst_channel/' + channelId,
    method: 'get'
  })
}

// 新增渠道方档案
export function addJst_channel(data) {
  return request({
    url: '/jst/user/jst_channel',
    method: 'post',
    data: data
  })
}

// 修改渠道方档案
export function updateJst_channel(data) {
  return request({
    url: '/jst/user/jst_channel',
    method: 'put',
    data: data
  })
}

// 删除渠道方档案
export function delJst_channel(channelId) {
  return request({
    url: '/jst/user/jst_channel/' + channelId,
    method: 'delete'
  })
}
