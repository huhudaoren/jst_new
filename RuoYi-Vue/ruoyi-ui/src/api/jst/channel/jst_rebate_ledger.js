import request from '@/utils/request'

// 查询返点计提台账列表
export function listJst_rebate_ledger(query) {
  return request({
    url: '/jst/channel/jst_rebate_ledger/list',
    method: 'get',
    params: query
  })
}

// 查询返点计提台账详细
export function getJst_rebate_ledger(ledgerId) {
  return request({
    url: '/jst/channel/jst_rebate_ledger/' + ledgerId,
    method: 'get'
  })
}

// 新增返点计提台账
export function addJst_rebate_ledger(data) {
  return request({
    url: '/jst/channel/jst_rebate_ledger',
    method: 'post',
    data: data
  })
}

// 修改返点计提台账
export function updateJst_rebate_ledger(data) {
  return request({
    url: '/jst/channel/jst_rebate_ledger',
    method: 'put',
    data: data
  })
}

// 删除返点计提台账
export function delJst_rebate_ledger(ledgerId) {
  return request({
    url: '/jst/channel/jst_rebate_ledger/' + ledgerId,
    method: 'delete'
  })
}
