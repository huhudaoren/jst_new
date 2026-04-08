import request from '@/utils/request'

// 查询成绩记录列表
export function listJst_score_record(query) {
  return request({
    url: '/jst/event/jst_score_record/list',
    method: 'get',
    params: query
  })
}

// 查询成绩记录详细
export function getJst_score_record(scoreId) {
  return request({
    url: '/jst/event/jst_score_record/' + scoreId,
    method: 'get'
  })
}

// 新增成绩记录
export function addJst_score_record(data) {
  return request({
    url: '/jst/event/jst_score_record',
    method: 'post',
    data: data
  })
}

// 修改成绩记录
export function updateJst_score_record(data) {
  return request({
    url: '/jst/event/jst_score_record',
    method: 'put',
    data: data
  })
}

// 删除成绩记录
export function delJst_score_record(scoreId) {
  return request({
    url: '/jst/event/jst_score_record/' + scoreId,
    method: 'delete'
  })
}
