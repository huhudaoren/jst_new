import request from '@/utils/request'

// 冲突列表（分页，可按 status 过滤）
export function listConflict(params) {
  return request({ url: '/admin/sales/conflict/list', method: 'get', params })
}

// 解决冲突（裁决）
export function resolveConflict(conflictId, data) {
  return request({ url: `/admin/sales/conflict/${conflictId}/resolve`, method: 'post', data })
}
