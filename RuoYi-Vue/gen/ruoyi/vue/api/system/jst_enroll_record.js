import request from '@/utils/request'

// 查询报名记录（含动态单快照）列表
export function listJst_enroll_record(query) {
  return request({
    url: '/system/jst_enroll_record/list',
    method: 'get',
    params: query
  })
}

// 查询报名记录（含动态单快照）详细
export function getJst_enroll_record(enrollId) {
  return request({
    url: '/system/jst_enroll_record/' + enrollId,
    method: 'get'
  })
}

// 新增报名记录（含动态单快照）
export function addJst_enroll_record(data) {
  return request({
    url: '/system/jst_enroll_record',
    method: 'post',
    data: data
  })
}

// 修改报名记录（含动态单快照）
export function updateJst_enroll_record(data) {
  return request({
    url: '/system/jst_enroll_record',
    method: 'put',
    data: data
  })
}

// 删除报名记录（含动态单快照）
export function delJst_enroll_record(enrollId) {
  return request({
    url: '/system/jst_enroll_record/' + enrollId,
    method: 'delete'
  })
}
