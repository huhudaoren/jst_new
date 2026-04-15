import request from '@/utils/request'

// 管理端-参赛档案列表
export function listAdminParticipants(query) {
  return request({
    url: '/jst/admin/participant/list',
    method: 'get',
    params: query
  })
}

// 管理端-参赛档案详情
export function getAdminParticipant(participantId) {
  return request({
    url: '/jst/admin/participant/' + participantId,
    method: 'get'
  })
}

// 管理端-手动认领
export function claimParticipantByAdmin(data) {
  return request({
    url: '/jst/user/participant/claim/admin',
    method: 'post',
    data
  })
}

// 管理端-撤销认领
export function revokeParticipantClaim(participantId, reason) {
  return request({
    url: '/jst/user/participant/claim/revoke',
    method: 'post',
    params: {
      participantId,
      reason
    }
  })
}

// 管理端-认领映射列表
export function listParticipantUserMaps(query) {
  return request({
    url: '/jst/admin/participant-user-map/list',
    method: 'get',
    params: query
  })
}
