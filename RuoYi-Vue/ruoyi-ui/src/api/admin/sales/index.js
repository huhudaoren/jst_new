import request from '@/utils/request'

// 销售列表（分页）
export function listSales(query) {
  return request({ url: '/admin/sales/list', method: 'get', params: query })
}

// 销售详情
export function getSales(id) {
  return request({ url: `/admin/sales/${id}`, method: 'get' })
}

// 新建销售档案（旧版：要求已传 sys_user_id，保留兼容）
export function createSales(data) {
  return request({ url: '/admin/sales', method: 'post', data })
}

// 一站式新建销售（PATCH-7：自动建 sys_user + sys_user_role + jst_sales）
export function createSalesOnestop(data) {
  return request({ url: '/admin/sales/onestop', method: 'post', data })
}

// 修改默认费率
export function updateRate(id, rate) {
  return request({ url: `/admin/sales/${id}/rate`, method: 'put', data: { rate } })
}

// 设置/取消主管
export function updateManager(id, managerId) {
  return request({ url: `/admin/sales/${id}/manager`, method: 'put', data: { managerId } })
}

// 申请离职
export function resignApply(id, expectedResignDate) {
  return request({ url: `/admin/sales/${id}/resign-apply`, method: 'post', data: { expectedResignDate } })
}

// 立即执行离职
export function resignExecute(id) {
  return request({ url: `/admin/sales/${id}/resign-execute`, method: 'post' })
}

// 终结过渡期
export function transitionEnd(id) {
  return request({ url: `/admin/sales/${id}/transition-end`, method: 'post' })
}
