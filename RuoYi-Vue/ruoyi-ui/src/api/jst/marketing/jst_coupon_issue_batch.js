import request from '@/utils/request'

// 查询发券批次列表
export function listJst_coupon_issue_batch(query) {
  return request({
    url: '/jst/marketing/jst_coupon_issue_batch/list',
    method: 'get',
    params: query
  })
}

// 查询发券批次详细
export function getJst_coupon_issue_batch(batchId) {
  return request({
    url: '/jst/marketing/jst_coupon_issue_batch/' + batchId,
    method: 'get'
  })
}

// 新增发券批次
export function addJst_coupon_issue_batch(data) {
  return request({
    url: '/jst/marketing/jst_coupon_issue_batch',
    method: 'post',
    data: data
  })
}

// 修改发券批次
export function updateJst_coupon_issue_batch(data) {
  return request({
    url: '/jst/marketing/jst_coupon_issue_batch',
    method: 'put',
    data: data
  })
}

// 删除发券批次
export function delJst_coupon_issue_batch(batchId) {
  return request({
    url: '/jst/marketing/jst_coupon_issue_batch/' + batchId,
    method: 'delete'
  })
}
