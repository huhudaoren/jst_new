import request from '@/utils/request'

// 平台月度概览（销售在职数/本月订单/GMV/提成成本）
export function getDashboardOverview(month) {
  return request({ url: '/admin/sales/dashboard/overview', method: 'get', params: { month } })
}

// 单销售月度详情
export function getSalesDetail(salesId, month) {
  return request({ url: `/admin/sales/dashboard/sales/${salesId}`, method: 'get', params: { month } })
}

// 各销售跟进活动汇总（echarts bar chart 数据）
export function getFollowupActivity(month) {
  return request({ url: '/admin/sales/dashboard/followup-activity', method: 'get', params: { month } })
}

// 提成成本趋势
export function getCommissionTrend(params) {
  return request({ url: '/admin/sales/dashboard/commission-trend', method: 'get', params })
}

// J 上限压缩统计
export function getCompressionStats(params) {
  return request({ url: '/admin/sales/dashboard/compression-stats', method: 'get', params })
}

// 渠道业绩热力图
export function getChannelHeatmap(params) {
  return request({ url: '/admin/sales/dashboard/channel-heatmap', method: 'get', params })
}
