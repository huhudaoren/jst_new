/**
 * 用户权益 API (WX-C4 · F-MARKETING-RIGHTS-BE)
 * 对应后端: /jst/wx/rights/*
 */
import request from '@/api/request'

/**
 * 我的权益列表
 * @param {Object} params { status?:'available'|'applying'|'used'|'expired', pageNum, pageSize }
 */
export function getMyRights(params) {
  return request({ url: '/jst/wx/rights/my', method: 'GET', data: params })
}

/** 权益详情 (含核销历史) */
export function getRightsDetail(userRightsId) {
  return request({ url: `/jst/wx/rights/${userRightsId}`, method: 'GET' })
}

/**
 * 申请权益核销
 * @param {Number|String} userRightsId
 * @param {Object} body { writeoffAmount?, writeoffCount?, remark? }
 * @returns {Object} { recordId, writeoffNo, remainQuota, status }  recordId 即 anchor
 */
export function applyRightsWriteoff(userRightsId, body) {
  return request({
    url: `/jst/wx/rights/${userRightsId}/apply-writeoff`,
    method: 'POST',
    data: body
  })
}

/**
 * 我的权益核销记录（跨权益聚合） — 2026-04-18 起走后端统一端点。
 *
 * 端点: GET /jst/wx/rights/writeoff-records
 * Query: { rightsId?, status? (含 'invalid' = used+expired+rolled_back), pageNum, pageSize }
 * 返回字段: { recordId, rightsId, rightsTemplateId, rightsName, rightsType,
 *            rightsIconEmoji, writeoffNo, amount, remark, status,
 *            auditUserId, auditRemark, auditTime, applyTime }
 *
 * @param {Object} params { status?, rightsId?, pageNum?, pageSize? }
 * @returns {Object} TableDataInfo { rows:[...], total }
 */
export function getMyRightsWriteoffRecords(params) {
  return request({
    url: '/jst/wx/rights/writeoff-records',
    method: 'GET',
    data: params || {}
  })
}
