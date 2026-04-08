import request from '@/utils/request'

// 查询积分流水列表
export function listJst_points_ledger(query) {
  return request({
    url: '/system/jst_points_ledger/list',
    method: 'get',
    params: query
  })
}

// 查询积分流水详细
export function getJst_points_ledger(ledgerId) {
  return request({
    url: '/system/jst_points_ledger/' + ledgerId,
    method: 'get'
  })
}

// 新增积分流水
export function addJst_points_ledger(data) {
  return request({
    url: '/system/jst_points_ledger',
    method: 'post',
    data: data
  })
}

// 修改积分流水
export function updateJst_points_ledger(data) {
  return request({
    url: '/system/jst_points_ledger',
    method: 'put',
    data: data
  })
}

// 删除积分流水
export function delJst_points_ledger(ledgerId) {
  return request({
    url: '/system/jst_points_ledger/' + ledgerId,
    method: 'delete'
  })
}
