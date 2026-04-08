import request from '@/utils/request'

// 查询证书模板列表
export function listJst_cert_template(query) {
  return request({
    url: '/jst/event/jst_cert_template/list',
    method: 'get',
    params: query
  })
}

// 查询证书模板详细
export function getJst_cert_template(templateId) {
  return request({
    url: '/jst/event/jst_cert_template/' + templateId,
    method: 'get'
  })
}

// 新增证书模板
export function addJst_cert_template(data) {
  return request({
    url: '/jst/event/jst_cert_template',
    method: 'post',
    data: data
  })
}

// 修改证书模板
export function updateJst_cert_template(data) {
  return request({
    url: '/jst/event/jst_cert_template',
    method: 'put',
    data: data
  })
}

// 删除证书模板
export function delJst_cert_template(templateId) {
  return request({
    url: '/jst/event/jst_cert_template/' + templateId,
    method: 'delete'
  })
}
