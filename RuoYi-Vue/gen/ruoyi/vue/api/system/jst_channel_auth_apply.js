import request from '@/utils/request'

// 查询渠道认证申请列表
export function listJst_channel_auth_apply(query) {
  return request({
    url: '/system/jst_channel_auth_apply/list',
    method: 'get',
    params: query
  })
}

// 查询渠道认证申请详细
export function getJst_channel_auth_apply(applyId) {
  return request({
    url: '/system/jst_channel_auth_apply/' + applyId,
    method: 'get'
  })
}

// 新增渠道认证申请
export function addJst_channel_auth_apply(data) {
  return request({
    url: '/system/jst_channel_auth_apply',
    method: 'post',
    data: data
  })
}

// 修改渠道认证申请
export function updateJst_channel_auth_apply(data) {
  return request({
    url: '/system/jst_channel_auth_apply',
    method: 'put',
    data: data
  })
}

// 删除渠道认证申请
export function delJst_channel_auth_apply(applyId) {
  return request({
    url: '/system/jst_channel_auth_apply/' + applyId,
    method: 'delete'
  })
}
