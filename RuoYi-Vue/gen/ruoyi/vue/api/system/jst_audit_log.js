import request from '@/utils/request'

// 查询操作审计日志列表
export function listJst_audit_log(query) {
  return request({
    url: '/system/jst_audit_log/list',
    method: 'get',
    params: query
  })
}

// 查询操作审计日志详细
export function getJst_audit_log(auditId) {
  return request({
    url: '/system/jst_audit_log/' + auditId,
    method: 'get'
  })
}

// 新增操作审计日志
export function addJst_audit_log(data) {
  return request({
    url: '/system/jst_audit_log',
    method: 'post',
    data: data
  })
}

// 修改操作审计日志
export function updateJst_audit_log(data) {
  return request({
    url: '/system/jst_audit_log',
    method: 'put',
    data: data
  })
}

// 删除操作审计日志
export function delJst_audit_log(auditId) {
  return request({
    url: '/system/jst_audit_log/' + auditId,
    method: 'delete'
  })
}
