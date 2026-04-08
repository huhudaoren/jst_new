import request from '@/utils/request'

// 查询积分/成长值规则列表
export function listJst_points_rule(query) {
  return request({
    url: '/jst/points/jst_points_rule/list',
    method: 'get',
    params: query
  })
}

// 查询积分/成长值规则详细
export function getJst_points_rule(ruleId) {
  return request({
    url: '/jst/points/jst_points_rule/' + ruleId,
    method: 'get'
  })
}

// 新增积分/成长值规则
export function addJst_points_rule(data) {
  return request({
    url: '/jst/points/jst_points_rule',
    method: 'post',
    data: data
  })
}

// 修改积分/成长值规则
export function updateJst_points_rule(data) {
  return request({
    url: '/jst/points/jst_points_rule',
    method: 'put',
    data: data
  })
}

// 删除积分/成长值规则
export function delJst_points_rule(ruleId) {
  return request({
    url: '/jst/points/jst_points_rule/' + ruleId,
    method: 'delete'
  })
}
