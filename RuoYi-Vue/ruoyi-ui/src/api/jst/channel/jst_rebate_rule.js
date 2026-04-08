import request from '@/utils/request'

// 查询渠道返点规则列表
export function listJst_rebate_rule(query) {
  return request({
    url: '/jst/channel/jst_rebate_rule/list',
    method: 'get',
    params: query
  })
}

// 查询渠道返点规则详细
export function getJst_rebate_rule(ruleId) {
  return request({
    url: '/jst/channel/jst_rebate_rule/' + ruleId,
    method: 'get'
  })
}

// 新增渠道返点规则
export function addJst_rebate_rule(data) {
  return request({
    url: '/jst/channel/jst_rebate_rule',
    method: 'post',
    data: data
  })
}

// 修改渠道返点规则
export function updateJst_rebate_rule(data) {
  return request({
    url: '/jst/channel/jst_rebate_rule',
    method: 'put',
    data: data
  })
}

// 删除渠道返点规则
export function delJst_rebate_rule(ruleId) {
  return request({
    url: '/jst/channel/jst_rebate_rule/' + ruleId,
    method: 'delete'
  })
}
