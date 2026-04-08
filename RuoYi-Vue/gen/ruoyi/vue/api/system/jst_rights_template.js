import request from '@/utils/request'

// 查询权益模板列表
export function listJst_rights_template(query) {
  return request({
    url: '/system/jst_rights_template/list',
    method: 'get',
    params: query
  })
}

// 查询权益模板详细
export function getJst_rights_template(rightsTemplateId) {
  return request({
    url: '/system/jst_rights_template/' + rightsTemplateId,
    method: 'get'
  })
}

// 新增权益模板
export function addJst_rights_template(data) {
  return request({
    url: '/system/jst_rights_template',
    method: 'post',
    data: data
  })
}

// 修改权益模板
export function updateJst_rights_template(data) {
  return request({
    url: '/system/jst_rights_template',
    method: 'put',
    data: data
  })
}

// 删除权益模板
export function delJst_rights_template(rightsTemplateId) {
  return request({
    url: '/system/jst_rights_template/' + rightsTemplateId,
    method: 'delete'
  })
}
