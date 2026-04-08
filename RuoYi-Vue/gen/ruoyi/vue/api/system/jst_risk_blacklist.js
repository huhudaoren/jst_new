import request from '@/utils/request'

// 查询风控黑白名单列表
export function listJst_risk_blacklist(query) {
  return request({
    url: '/system/jst_risk_blacklist/list',
    method: 'get',
    params: query
  })
}

// 查询风控黑白名单详细
export function getJst_risk_blacklist(listId) {
  return request({
    url: '/system/jst_risk_blacklist/' + listId,
    method: 'get'
  })
}

// 新增风控黑白名单
export function addJst_risk_blacklist(data) {
  return request({
    url: '/system/jst_risk_blacklist',
    method: 'post',
    data: data
  })
}

// 修改风控黑白名单
export function updateJst_risk_blacklist(data) {
  return request({
    url: '/system/jst_risk_blacklist',
    method: 'put',
    data: data
  })
}

// 删除风控黑白名单
export function delJst_risk_blacklist(listId) {
  return request({
    url: '/system/jst_risk_blacklist/' + listId,
    method: 'delete'
  })
}
