import request from '@/utils/request'

// 查询赛事方结算单列表
export function listJst_event_settlement(query) {
  return request({
    url: '/jst/channel/jst_event_settlement/list',
    method: 'get',
    params: query
  })
}

// 查询赛事方结算单详细
export function getJst_event_settlement(eventSettlementId) {
  return request({
    url: '/jst/channel/jst_event_settlement/' + eventSettlementId,
    method: 'get'
  })
}

// 新增赛事方结算单
export function addJst_event_settlement(data) {
  return request({
    url: '/jst/channel/jst_event_settlement',
    method: 'post',
    data: data
  })
}

// 修改赛事方结算单
export function updateJst_event_settlement(data) {
  return request({
    url: '/jst/channel/jst_event_settlement',
    method: 'put',
    data: data
  })
}

// 删除赛事方结算单
export function delJst_event_settlement(eventSettlementId) {
  return request({
    url: '/jst/channel/jst_event_settlement/' + eventSettlementId,
    method: 'delete'
  })
}
