import request from '@/utils/request'

// 查询学生-渠道方绑定关系（同一user同时仅1条active）列表
export function listJst_student_channel_binding(query) {
  return request({
    url: '/jst/user/jst_student_channel_binding/list',
    method: 'get',
    params: query
  })
}

// 查询学生-渠道方绑定关系（同一user同时仅1条active）详细
export function getJst_student_channel_binding(bindingId) {
  return request({
    url: '/jst/user/jst_student_channel_binding/' + bindingId,
    method: 'get'
  })
}

// 新增学生-渠道方绑定关系（同一user同时仅1条active）
export function addJst_student_channel_binding(data) {
  return request({
    url: '/jst/user/jst_student_channel_binding',
    method: 'post',
    data: data
  })
}

// 修改学生-渠道方绑定关系（同一user同时仅1条active）
export function updateJst_student_channel_binding(data) {
  return request({
    url: '/jst/user/jst_student_channel_binding',
    method: 'put',
    data: data
  })
}

// 删除学生-渠道方绑定关系（同一user同时仅1条active）
export function delJst_student_channel_binding(bindingId) {
  return request({
    url: '/jst/user/jst_student_channel_binding/' + bindingId,
    method: 'delete'
  })
}
