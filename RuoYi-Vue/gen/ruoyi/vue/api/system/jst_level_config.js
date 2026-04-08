import request from '@/utils/request'

// 查询等级配置列表
export function listJst_level_config(query) {
  return request({
    url: '/system/jst_level_config/list',
    method: 'get',
    params: query
  })
}

// 查询等级配置详细
export function getJst_level_config(levelId) {
  return request({
    url: '/system/jst_level_config/' + levelId,
    method: 'get'
  })
}

// 新增等级配置
export function addJst_level_config(data) {
  return request({
    url: '/system/jst_level_config',
    method: 'post',
    data: data
  })
}

// 修改等级配置
export function updateJst_level_config(data) {
  return request({
    url: '/system/jst_level_config',
    method: 'put',
    data: data
  })
}

// 删除等级配置
export function delJst_level_config(levelId) {
  return request({
    url: '/system/jst_level_config/' + levelId,
    method: 'delete'
  })
}
