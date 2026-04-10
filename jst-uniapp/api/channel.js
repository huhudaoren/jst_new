/**
 * 渠道方返点 + 提现 API 封装 (WX-C2)
 * 对应后端: WxChannelWithdrawController (/jst/wx/channel/*)
 * 契约来源: test/wx-tests.http § C5a + RuoYi-Vue/jst-channel/**\/vo,dto
 * 约束: 必须经 @/api/request 统一封装, 禁止裸 uni.request
 */
import request from '@/api/request'

/** 返点中心 4 KPI: withdrawableAmount / reviewingAmount / paidAmount / rolledBackAmount */
export function getRebateSummary() {
  return request({ url: '/jst/wx/channel/rebate/summary', method: 'GET' })
}

/**
 * 返点台账分页列表
 * @param {Object} params { status?, pageNum, pageSize }
 *   status: '' | 'pending' | 'withdrawable' | 'in_review' | 'paid' | 'rejected' | 'rolled_back'
 */
export function getRebateLedgerList(params) {
  return request({
    url: '/jst/wx/channel/rebate/ledger/list',
    method: 'GET',
    data: params
  })
}

/**
 * 申请提现
 * @param {Object} body
 *   { ledgerIds: Long[], expectedAmount: Number(2dp), payeeAccount:{bankName,accountNo,accountName}, invoiceInfo?:{title,taxNo} }
 * @returns { settlementId, settlementNo, actualAmount, status }
 * 错误码: 40011 ledger 无效 / 40012 金额不一致 / 40013 状态非法 / 40014 锁超时
 */
export function applyWithdraw(body) {
  return request({ url: '/jst/wx/channel/withdraw/apply', method: 'POST', data: body })
}

/** 取消 pending 状态提现单 */
export function cancelWithdraw(settlementId) {
  return request({
    url: `/jst/wx/channel/withdraw/${settlementId}/cancel`,
    method: 'POST'
  })
}

/**
 * 我的提现单列表
 * @param {Object} params { status?, pageNum, pageSize }
 *   status: '' | 'pending' | 'in_review' | 'paid' | 'rejected' | 'cancelled'
 */
export function getWithdrawList(params) {
  return request({
    url: '/jst/wx/channel/withdraw/list',
    method: 'GET',
    data: params
  })
}

/** 提现单详情 (含 ledgerItems) */
export function getWithdrawDetail(settlementId) {
  return request({ url: `/jst/wx/channel/withdraw/${settlementId}`, method: 'GET' })
}

/* ---------------- 渠道认证 (E1-CH-1) ---------------- */

/** 查询当前用户最新认证申请 */
export function getMyChannelApply() {
  return request({ url: '/jst/wx/channel/auth/my', method: 'GET' })
}

/** 提交渠道认证申请: { channelType, applyName, materialsJson } */
export function submitChannelApply(body) {
  return request({ url: '/jst/wx/channel/auth/apply', method: 'POST', data: body })
}

/** 驳回后重提认证申请 */
export function resubmitChannelApply(id, body) {
  return request({ url: `/jst/wx/channel/auth/resubmit/${id}`, method: 'POST', data: body })
}

/** 撤回 pending 状态认证申请 */
export function cancelChannelApply(id) {
  return request({ url: `/jst/wx/channel/auth/cancel/${id}`, method: 'POST' })
}

/* ---------------- 渠道工作台 (E1-CH-2) ---------------- */

/** 渠道 dashboard 月度统计 (绑定学生/代报名/待付款) */
export function getChannelMonthly() {
  return request({ url: '/jst/wx/channel/dashboard/monthly', method: 'GET' })
}

/** 渠道 dashboard 综合统计 */
export function getChannelDashboardStats(params) {
  return request({ url: '/jst/wx/channel/dashboard/stats', method: 'GET', data: params })
}

/** 渠道权益列表 */
export function getChannelRightsMy() {
  return request({ url: '/jst/wx/channel/rights/my', method: 'GET' })
}

/** 渠道订单列表 (工作台最近3条 / 订单页分页) */
export function getChannelOrders(params) {
  return request({ url: '/jst/wx/channel/orders', method: 'GET', data: params })
}

/** 渠道订单详情 */
export function getChannelOrderDetail(orderId) {
  return request({ url: `/jst/wx/channel/orders/${orderId}`, method: 'GET' })
}

/* ---------------- 学生管理 (E1-CH-3) ---------------- */

/** 渠道绑定学生列表 */
export function getChannelStudents(params) {
  return request({ url: '/jst/wx/channel/students', method: 'GET', data: params })
}

/** 解绑学生 */
export function unbindStudent(bindingId) {
  return request({ url: `/jst/wx/channel/binding/${bindingId}/unbind`, method: 'POST' })
}

/** 学生成绩列表 */
export function getStudentScore(studentId) {
  return request({ url: `/jst/wx/channel/students/${studentId}/score`, method: 'GET' })
}

/** 学生证书列表 */
export function getStudentCert(studentId) {
  return request({ url: `/jst/wx/channel/students/${studentId}/cert`, method: 'GET' })
}

/* ---------------- 数据统计 (E1-CH-6) ---------------- */

/** 渠道统计数据 (含时间段筛选) */
export function getChannelStats(params) {
  return request({ url: '/jst/wx/channel/dashboard/stats', method: 'GET', data: params })
}

/** 热门赛事排行 TOP5 */
export function getTopContests(params) {
  return request({ url: '/jst/wx/channel/dashboard/top-contests', method: 'GET', data: params })
}

/** 活跃学生排行 TOP5 */
export function getTopStudents(params) {
  return request({ url: '/jst/wx/channel/dashboard/top-students', method: 'GET', data: params })
}
