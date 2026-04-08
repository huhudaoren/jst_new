import request from '@/utils/request'

// 查询报名动态单模板（schema_json定义字段）列表
export function listJst_enroll_form_template(query) {
  return request({
    url: '/system/jst_enroll_form_template/list',
    method: 'get',
    params: query
  })
}

// 查询报名动态单模板（schema_json定义字段）详细
export function getJst_enroll_form_template(templateId) {
  return request({
    url: '/system/jst_enroll_form_template/' + templateId,
    method: 'get'
  })
}

// 新增报名动态单模板（schema_json定义字段）
export function addJst_enroll_form_template(data) {
  return request({
    url: '/system/jst_enroll_form_template',
    method: 'post',
    data: data
  })
}

// 修改报名动态单模板（schema_json定义字段）
export function updateJst_enroll_form_template(data) {
  return request({
    url: '/system/jst_enroll_form_template',
    method: 'put',
    data: data
  })
}

// 删除报名动态单模板（schema_json定义字段）
export function delJst_enroll_form_template(templateId) {
  return request({
    url: '/system/jst_enroll_form_template/' + templateId,
    method: 'delete'
  })
}
