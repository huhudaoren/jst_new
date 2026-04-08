import request from '@/utils/request'

// 查询权益核销记录列表
export function listJst_rights_writeoff_record(query) {
  return request({
    url: '/jst/marketing/jst_rights_writeoff_record/list',
    method: 'get',
    params: query
  })
}

// 查询权益核销记录详细
export function getJst_rights_writeoff_record(recordId) {
  return request({
    url: '/jst/marketing/jst_rights_writeoff_record/' + recordId,
    method: 'get'
  })
}

// 新增权益核销记录
export function addJst_rights_writeoff_record(data) {
  return request({
    url: '/jst/marketing/jst_rights_writeoff_record',
    method: 'post',
    data: data
  })
}

// 修改权益核销记录
export function updateJst_rights_writeoff_record(data) {
  return request({
    url: '/jst/marketing/jst_rights_writeoff_record',
    method: 'put',
    data: data
  })
}

// 删除权益核销记录
export function delJst_rights_writeoff_record(recordId) {
  return request({
    url: '/jst/marketing/jst_rights_writeoff_record/' + recordId,
    method: 'delete'
  })
}
