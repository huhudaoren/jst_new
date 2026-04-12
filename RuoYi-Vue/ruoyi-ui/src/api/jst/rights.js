import request from '@/utils/request'

// ---- 权益模板 ----
export function listRightsTemplate(query) {
  return request({ url: '/jst/marketing/jst_rights_template/list', method: 'get', params: query })
}

export function getRightsTemplate(id) {
  return request({ url: '/jst/marketing/jst_rights_template/' + id, method: 'get' })
}

export function addRightsTemplate(data) {
  return request({ url: '/jst/marketing/jst_rights_template', method: 'post', data })
}

export function updateRightsTemplate(data) {
  return request({ url: '/jst/marketing/jst_rights_template', method: 'put', data })
}

export function delRightsTemplate(ids) {
  return request({ url: '/jst/marketing/jst_rights_template/' + ids, method: 'delete' })
}

// ---- 已发权益（只读）----
export function listUserRights(query) {
  return request({ url: '/jst/marketing/jst_user_rights/list', method: 'get', params: query })
}
