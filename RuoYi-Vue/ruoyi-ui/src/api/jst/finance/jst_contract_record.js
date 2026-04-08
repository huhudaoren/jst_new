import request from '@/utils/request'

// 查询合同记录列表
export function listJst_contract_record(query) {
  return request({
    url: '/jst/finance/jst_contract_record/list',
    method: 'get',
    params: query
  })
}

// 查询合同记录详细
export function getJst_contract_record(contractId) {
  return request({
    url: '/jst/finance/jst_contract_record/' + contractId,
    method: 'get'
  })
}

// 新增合同记录
export function addJst_contract_record(data) {
  return request({
    url: '/jst/finance/jst_contract_record',
    method: 'post',
    data: data
  })
}

// 修改合同记录
export function updateJst_contract_record(data) {
  return request({
    url: '/jst/finance/jst_contract_record',
    method: 'put',
    data: data
  })
}

// 删除合同记录
export function delJst_contract_record(contractId) {
  return request({
    url: '/jst/finance/jst_contract_record/' + contractId,
    method: 'delete'
  })
}
