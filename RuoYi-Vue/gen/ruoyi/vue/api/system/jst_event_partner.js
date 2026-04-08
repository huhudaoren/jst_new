import request from '@/utils/request'

// 查询赛事方档案列表
export function listJst_event_partner(query) {
  return request({
    url: '/system/jst_event_partner/list',
    method: 'get',
    params: query
  })
}

// 查询赛事方档案详细
export function getJst_event_partner(partnerId) {
  return request({
    url: '/system/jst_event_partner/' + partnerId,
    method: 'get'
  })
}

// 新增赛事方档案
export function addJst_event_partner(data) {
  return request({
    url: '/system/jst_event_partner',
    method: 'post',
    data: data
  })
}

// 修改赛事方档案
export function updateJst_event_partner(data) {
  return request({
    url: '/system/jst_event_partner',
    method: 'put',
    data: data
  })
}

// 删除赛事方档案
export function delJst_event_partner(partnerId) {
  return request({
    url: '/system/jst_event_partner/' + partnerId,
    method: 'delete'
  })
}
