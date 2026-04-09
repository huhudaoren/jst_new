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
 */
export function applyRightsWriteoff(userRightsId, body) {
  return request({
    url: `/jst/wx/rights/${userRightsId}/apply-writeoff`,
    method: 'POST',
    data: body
  })
}
