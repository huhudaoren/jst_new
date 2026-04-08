import request from '@/utils/request'

// 查询课程学习记录列表
export function listJst_course_learn_record(query) {
  return request({
    url: '/system/jst_course_learn_record/list',
    method: 'get',
    params: query
  })
}

// 查询课程学习记录详细
export function getJst_course_learn_record(learnId) {
  return request({
    url: '/system/jst_course_learn_record/' + learnId,
    method: 'get'
  })
}

// 新增课程学习记录
export function addJst_course_learn_record(data) {
  return request({
    url: '/system/jst_course_learn_record',
    method: 'post',
    data: data
  })
}

// 修改课程学习记录
export function updateJst_course_learn_record(data) {
  return request({
    url: '/system/jst_course_learn_record',
    method: 'put',
    data: data
  })
}

// 删除课程学习记录
export function delJst_course_learn_record(learnId) {
  return request({
    url: '/system/jst_course_learn_record/' + learnId,
    method: 'delete'
  })
}
