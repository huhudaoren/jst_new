import request from '@/utils/request'

// 平台运营聚合接口（F-ANALYSIS-BE 专用 Controller）

// 总览 KPI（6 指标）
export function getOverview() {
  return request({ url: '/jst/admin/dashboard/overview', method: 'get' })
}

// 近 N 天趋势（days=7 或 30）
export function getTrend(days) {
  return request({ url: '/jst/admin/dashboard/trend', method: 'get', params: { days } })
}

// 报名 Top N 赛事
export function getTopContests(limit) {
  return request({ url: '/jst/admin/dashboard/top-contests', method: 'get', params: { limit: limit || 5 } })
}

// 返点 Top N 渠道
export function getTopChannels(limit) {
  return request({ url: '/jst/admin/dashboard/top-channels', method: 'get', params: { limit: limit || 5 } })
}

// 待办事项计数（6 项）
export function getTodo() {
  return request({ url: '/jst/admin/dashboard/todo', method: 'get' })
}
