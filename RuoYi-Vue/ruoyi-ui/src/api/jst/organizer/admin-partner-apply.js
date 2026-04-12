import request from '@/utils/request'

// 管理端-赛事方入驻申请列表
export function listAdminPartnerApplies(query) {
  return request({
    url: '/jst/organizer/partner/apply/list',
    method: 'get',
    params: query
  })
}

// 管理端-赛事方入驻申请详情
export function getAdminPartnerApply(applyId) {
  return request({
    url: '/jst/organizer/partner/apply/' + applyId,
    method: 'get'
  })
}

// 管理端-赛事方入驻审核通过
export function approveAdminPartnerApply(applyId, data) {
  return request({
    url: '/jst/organizer/partner/apply/' + applyId + '/approve',
    method: 'post',
    data
  })
}

// 管理端-赛事方入驻审核驳回
export function rejectAdminPartnerApply(applyId, data) {
  return request({
    url: '/jst/organizer/partner/apply/' + applyId + '/reject',
    method: 'post',
    data
  })
}
