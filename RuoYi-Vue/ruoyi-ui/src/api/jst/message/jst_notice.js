import request from '@/utils/request'

// 查询公告列表
export function listJst_notice(query) {
  return request({
    url: '/jst/message/jst_notice/list',
    method: 'get',
    params: query
  })
}

// 查询公告详细
export function getJst_notice(noticeId) {
  return request({
    url: '/jst/message/jst_notice/' + noticeId,
    method: 'get'
  })
}

// 新增公告
export function addJst_notice(data) {
  return request({
    url: '/jst/message/jst_notice',
    method: 'post',
    data: data
  })
}

// 修改公告
export function updateJst_notice(data) {
  return request({
    url: '/jst/message/jst_notice',
    method: 'put',
    data: data
  })
}

// 删除公告
export function delJst_notice(noticeId) {
  return request({
    url: '/jst/message/jst_notice/' + noticeId,
    method: 'delete'
  })
}
