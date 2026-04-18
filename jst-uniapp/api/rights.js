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
 * 我的权益核销记录（跨权益聚合）
 * TODO(BE): 后端暂无 /jst/wx/rights/writeoff-records 统一端点。
 *   当前实现：前端按 userRightsId 循环 getRightsDetail 拉 writeoffHistory 合并。
 *   建议后端补齐 /jst/wx/rights/writeoff-records?status=&pageNum=&pageSize= 分页端点。
 * @param {Object} params { status?:'all'|'pending'|'approved'|'rejected'|'rolled_back', rightsId? }
 * @returns {Array} [{ recordId, writeoffNo, useAmount, applyRemark, status, auditRemark, writeoffTime, createTime, userRightsId, rightsName }]
 */
export async function getMyRightsWriteoffRecords(params) {
  params = params || {}
  // 1) 如指定单个权益 → 直接拉详情
  if (params.rightsId) {
    const detail = await request({ url: `/jst/wx/rights/${params.rightsId}`, method: 'GET' })
    const history = (detail && detail.writeoffHistory) || []
    return history.map(h => Object.assign({}, h, {
      userRightsId: detail.userRightsId,
      rightsName: detail.rightsName
    }))
  }
  // 2) 否则聚合所有权益的 writeoffHistory（一次拉全部状态权益）
  const myRightsRes = await request({ url: '/jst/wx/rights/my', method: 'GET', data: { pageNum: 1, pageSize: 100 } })
  const myRights = (myRightsRes && myRightsRes.rows) || []
  const allRecords = []
  for (const r of myRights) {
    try {
      const d = await request({ url: `/jst/wx/rights/${r.userRightsId}`, method: 'GET' })
      const hist = (d && d.writeoffHistory) || []
      for (const h of hist) {
        allRecords.push(Object.assign({}, h, {
          userRightsId: r.userRightsId,
          rightsName: r.rightsName
        }))
      }
    } catch (e) { /* 单条失败不影响整体 */ }
  }
  // 时间倒序
  allRecords.sort((a, b) => {
    const ta = String(a.writeoffTime || a.createTime || '')
    const tb = String(b.writeoffTime || b.createTime || '')
    return tb.localeCompare(ta)
  })
  return allRecords
}
