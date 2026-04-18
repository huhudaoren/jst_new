import request from '@/utils/request'

/**
 * 销售本人业绩聚合视图。
 * 金额字段含 @Sensitive：销售本人查询时 MaskSalaryAspect 自动置 null；主管/admin 返完整数值。
 * @param {string} [month] 月份，格式 'yyyy-MM'，不传则当月
 */
export function getMyPerformance(month) {
  return request({ url: '/sales/me/performance', method: 'get', params: { month } })
}
