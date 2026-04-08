import request from '@/utils/request'

// 查询核销子项（团队部分核销/到场/礼品/仪式独立流转）列表
export function listJst_appointment_writeoff_item(query) {
  return request({
    url: '/system/jst_appointment_writeoff_item/list',
    method: 'get',
    params: query
  })
}

// 查询核销子项（团队部分核销/到场/礼品/仪式独立流转）详细
export function getJst_appointment_writeoff_item(writeoffItemId) {
  return request({
    url: '/system/jst_appointment_writeoff_item/' + writeoffItemId,
    method: 'get'
  })
}

// 新增核销子项（团队部分核销/到场/礼品/仪式独立流转）
export function addJst_appointment_writeoff_item(data) {
  return request({
    url: '/system/jst_appointment_writeoff_item',
    method: 'post',
    data: data
  })
}

// 修改核销子项（团队部分核销/到场/礼品/仪式独立流转）
export function updateJst_appointment_writeoff_item(data) {
  return request({
    url: '/system/jst_appointment_writeoff_item',
    method: 'put',
    data: data
  })
}

// 删除核销子项（团队部分核销/到场/礼品/仪式独立流转）
export function delJst_appointment_writeoff_item(writeoffItemId) {
  return request({
    url: '/system/jst_appointment_writeoff_item/' + writeoffItemId,
    method: 'delete'
  })
}
