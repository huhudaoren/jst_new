import request from '@/utils/request'

// 查询风控规则列表
export function listJst_risk_rule(query) {
  return request({
    url: '/system/jst_risk_rule/list',
    method: 'get',
    params: query
  })
}

// 查询风控规则详细
export function getJst_risk_rule(riskRuleId) {
  return request({
    url: '/system/jst_risk_rule/' + riskRuleId,
    method: 'get'
  })
}

// 新增风控规则
export function addJst_risk_rule(data) {
  return request({
    url: '/system/jst_risk_rule',
    method: 'post',
    data: data
  })
}

// 修改风控规则
export function updateJst_risk_rule(data) {
  return request({
    url: '/system/jst_risk_rule',
    method: 'put',
    data: data
  })
}

// 删除风控规则
export function delJst_risk_rule(riskRuleId) {
  return request({
    url: '/system/jst_risk_rule/' + riskRuleId,
    method: 'delete'
  })
}
