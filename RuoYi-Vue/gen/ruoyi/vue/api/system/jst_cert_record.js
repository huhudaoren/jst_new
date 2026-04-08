import request from '@/utils/request'

// 查询证书记录列表
export function listJst_cert_record(query) {
  return request({
    url: '/system/jst_cert_record/list',
    method: 'get',
    params: query
  })
}

// 查询证书记录详细
export function getJst_cert_record(certId) {
  return request({
    url: '/system/jst_cert_record/' + certId,
    method: 'get'
  })
}

// 新增证书记录
export function addJst_cert_record(data) {
  return request({
    url: '/system/jst_cert_record',
    method: 'post',
    data: data
  })
}

// 修改证书记录
export function updateJst_cert_record(data) {
  return request({
    url: '/system/jst_cert_record',
    method: 'put',
    data: data
  })
}

// 删除证书记录
export function delJst_cert_record(certId) {
  return request({
    url: '/system/jst_cert_record/' + certId,
    method: 'delete'
  })
}
