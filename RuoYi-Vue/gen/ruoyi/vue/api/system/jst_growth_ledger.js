import request from '@/utils/request'

// 查询成长值流水列表
export function listJst_growth_ledger(query) {
  return request({
    url: '/system/jst_growth_ledger/list',
    method: 'get',
    params: query
  })
}

// 查询成长值流水详细
export function getJst_growth_ledger(ledgerId) {
  return request({
    url: '/system/jst_growth_ledger/' + ledgerId,
    method: 'get'
  })
}

// 新增成长值流水
export function addJst_growth_ledger(data) {
  return request({
    url: '/system/jst_growth_ledger',
    method: 'post',
    data: data
  })
}

// 修改成长值流水
export function updateJst_growth_ledger(data) {
  return request({
    url: '/system/jst_growth_ledger',
    method: 'put',
    data: data
  })
}

// 删除成长值流水
export function delJst_growth_ledger(ledgerId) {
  return request({
    url: '/system/jst_growth_ledger/' + ledgerId,
    method: 'delete'
  })
}
