/**
 * 积分中心 API (WX-C4 · F-POINTS-CENTER-BE)
 * 对应后端: /jst/wx/points/*
 * 契约来源: subagent/tasks/C9+F-POINTS-CENTER-BE-商城售后与积分中心.md Part B
 * 注意: 后端并行开发, 本封装基于任务卡契约; 合并后若字段不符需联调修正
 */
import request from '@/api/request'

/** 积分中心 summary: {availablePoints,frozenPoints,totalEarn,totalSpend,growthValue,currentLevel,nextLevel,pointsToNextLevel} */
export function getPointsSummary() {
  return request({ url: '/jst/wx/points/center/summary', method: 'GET' })
}

/** 等级体系 (role: student|channel|teacher) */
export function getPointsLevels(role) {
  return request({ url: '/jst/wx/points/center/levels', method: 'GET', data: { role: role || 'student' } })
}

/**
 * 积分流水
 * @param {Object} params { changeType?:'earn'|'spend'|'freeze'|'unfreeze', pageNum, pageSize }
 */
export function getPointsLedger(params) {
  return request({ url: '/jst/wx/points/ledger', method: 'GET', data: params })
}

/** 成长值流水 */
export function getGrowthLedger(params) {
  return request({ url: '/jst/wx/points/growth/ledger', method: 'GET', data: params })
}

/** 赚积分任务 (mock 5 条) */
export function getPointsTasks() {
  return request({ url: '/jst/wx/points/tasks', method: 'GET' })
}
