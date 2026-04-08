import request from '@/utils/request'

// 查询风险工单列表
export function listJst_risk_case(query) {
  return request({
    url: '/jst/risk/jst_risk_case/list',
    method: 'get',
    params: query
  })
}

// 查询风险工单详细
export function getJst_risk_case(caseId) {
  return request({
    url: '/jst/risk/jst_risk_case/' + caseId,
    method: 'get'
  })
}

// 新增风险工单
export function addJst_risk_case(data) {
  return request({
    url: '/jst/risk/jst_risk_case',
    method: 'post',
    data: data
  })
}

// 修改风险工单
export function updateJst_risk_case(data) {
  return request({
    url: '/jst/risk/jst_risk_case',
    method: 'put',
    data: data
  })
}

// 删除风险工单
export function delJst_risk_case(caseId) {
  return request({
    url: '/jst/risk/jst_risk_case/' + caseId,
    method: 'delete'
  })
}
