import request from '@/utils/request'

// 查询风险预警列表
export function listJst_risk_alert(query) {
  return request({
    url: '/system/jst_risk_alert/list',
    method: 'get',
    params: query
  })
}

// 查询风险预警详细
export function getJst_risk_alert(alertId) {
  return request({
    url: '/system/jst_risk_alert/' + alertId,
    method: 'get'
  })
}

// 新增风险预警
export function addJst_risk_alert(data) {
  return request({
    url: '/system/jst_risk_alert',
    method: 'post',
    data: data
  })
}

// 修改风险预警
export function updateJst_risk_alert(data) {
  return request({
    url: '/system/jst_risk_alert',
    method: 'put',
    data: data
  })
}

// 删除风险预警
export function delJst_risk_alert(alertId) {
  return request({
    url: '/system/jst_risk_alert/' + alertId,
    method: 'delete'
  })
}
