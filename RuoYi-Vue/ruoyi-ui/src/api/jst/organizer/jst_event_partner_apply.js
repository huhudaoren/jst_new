import request from '@/utils/request'

// 查询赛事方入驻申请列表
export function listJst_event_partner_apply(query) {
  return request({
    url: '/jst/organizer/jst_event_partner_apply/list',
    method: 'get',
    params: query
  })
}

// 查询赛事方入驻申请详细
export function getJst_event_partner_apply(applyId) {
  return request({
    url: '/jst/organizer/jst_event_partner_apply/' + applyId,
    method: 'get'
  })
}

// 新增赛事方入驻申请
export function addJst_event_partner_apply(data) {
  return request({
    url: '/jst/organizer/jst_event_partner_apply',
    method: 'post',
    data: data
  })
}

// 修改赛事方入驻申请
export function updateJst_event_partner_apply(data) {
  return request({
    url: '/jst/organizer/jst_event_partner_apply',
    method: 'put',
    data: data
  })
}

// 删除赛事方入驻申请
export function delJst_event_partner_apply(applyId) {
  return request({
    url: '/jst/organizer/jst_event_partner_apply/' + applyId,
    method: 'delete'
  })
}
