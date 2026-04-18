import request from '@/utils/request'

/**
 * 团队成员列表（含当月简要业绩）。
 * 销售主管：返下属销售；admin / jst_operator：返全量销售。
 * @param {string} [month] 格式 YYYY-MM（缺省=当月）
 */
export function listTeam(month) {
  return request({ url: '/sales/manager/team', method: 'get', params: { month } })
}
