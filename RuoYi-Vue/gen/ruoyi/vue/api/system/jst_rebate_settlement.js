import request from '@/utils/request'

// 查询渠道提现/结算单列表
export function listJst_rebate_settlement(query) {
  return request({
    url: '/system/jst_rebate_settlement/list',
    method: 'get',
    params: query
  })
}

// 查询渠道提现/结算单详细
export function getJst_rebate_settlement(settlementId) {
  return request({
    url: '/system/jst_rebate_settlement/' + settlementId,
    method: 'get'
  })
}

// 新增渠道提现/结算单
export function addJst_rebate_settlement(data) {
  return request({
    url: '/system/jst_rebate_settlement',
    method: 'post',
    data: data
  })
}

// 修改渠道提现/结算单
export function updateJst_rebate_settlement(data) {
  return request({
    url: '/system/jst_rebate_settlement',
    method: 'put',
    data: data
  })
}

// 删除渠道提现/结算单
export function delJst_rebate_settlement(settlementId) {
  return request({
    url: '/system/jst_rebate_settlement/' + settlementId,
    method: 'delete'
  })
}
