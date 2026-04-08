import request from '@/utils/request'

// 查询参赛档案-正式用户认领映射列表
export function listJst_participant_user_map(query) {
  return request({
    url: '/system/jst_participant_user_map/list',
    method: 'get',
    params: query
  })
}

// 查询参赛档案-正式用户认领映射详细
export function getJst_participant_user_map(mapId) {
  return request({
    url: '/system/jst_participant_user_map/' + mapId,
    method: 'get'
  })
}

// 新增参赛档案-正式用户认领映射
export function addJst_participant_user_map(data) {
  return request({
    url: '/system/jst_participant_user_map',
    method: 'post',
    data: data
  })
}

// 修改参赛档案-正式用户认领映射
export function updateJst_participant_user_map(data) {
  return request({
    url: '/system/jst_participant_user_map',
    method: 'put',
    data: data
  })
}

// 删除参赛档案-正式用户认领映射
export function delJst_participant_user_map(mapId) {
  return request({
    url: '/system/jst_participant_user_map/' + mapId,
    method: 'delete'
  })
}
