import request from '@/utils/request'

export function listBizNoRule(query) {
  return request({ url: '/jst/admin/biz-no-rule/list', method: 'get', params: query })
}

export function getBizNoRule(ruleId) {
  return request({ url: '/jst/admin/biz-no-rule/' + ruleId, method: 'get' })
}

export function addBizNoRule(data) {
  return request({ url: '/jst/admin/biz-no-rule', method: 'post', data })
}

export function updateBizNoRule(data) {
  return request({ url: '/jst/admin/biz-no-rule', method: 'put', data })
}

export function delBizNoRule(ruleIds) {
  return request({ url: '/jst/admin/biz-no-rule/' + ruleIds, method: 'delete' })
}
