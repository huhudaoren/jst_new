/**
 * 渠道邀请 & 分销 API 封装 (plan-04)
 * 对应后端: WxChannelInviteController (/jst/wx/channel/invite/*, /jst/wx/channel/distribution/*)
 * 约束: 必须经 @/api/request 统一封装, 禁止裸 uni.request
 */
import request from '@/api/request'

/**
 * 获取我的邀请码（首次调用自动生成）
 * @returns { inviteCode, qrcodeUrl }
 */
export function getMyInviteCode() {
  return request({ url: '/jst/wx/channel/invite/code', method: 'GET' })
}

/**
 * 我邀请的渠道列表
 * @returns Array<{ channelId, channelName, inviteTime, status, commissionRate }>
 */
export function listInvited() {
  return request({ url: '/jst/wx/channel/invite/list', method: 'GET' })
}

/**
 * 分销台账列表（分页）
 * @param {Object} params { status?, pageNum, pageSize }
 */
export function listDistributionLedger(params) {
  return request({ url: '/jst/wx/channel/distribution/ledger', method: 'GET', data: params })
}

/**
 * 分销收益汇总
 * @returns { totalAmount, pendingAmount, accruedAmount }
 */
export function getDistributionSummary() {
  return request({ url: '/jst/wx/channel/distribution/summary', method: 'GET' })
}
