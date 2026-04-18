/**
 * 倒计时工具
 *
 * 用法示例:
 *   import { formatCountdown } from '@/utils/countdown'
 *   const c = formatCountdown('2026-05-01 00:00:00')
 *   // { d: 13, hh: '05', mm: '42', ss: '08', expired: false, urgent: false }
 */

/**
 * 把 'yyyy-MM-dd HH:mm:ss' 或 ISO 字符串或 Date 转成毫秒时间戳
 * 兼容 iOS（iOS 不识别空格分隔的日期字符串）
 * @param {string|number|Date} value
 * @returns {number} 时间戳；非法返回 NaN
 */
function toTimestamp(value) {
  if (value == null || value === '') return NaN
  if (value instanceof Date) return value.getTime()
  if (typeof value === 'number') return value
  const s = String(value).replace(/-/g, '/').replace('T', ' ').replace(/\.\d+Z?$/, '')
  const t = new Date(s).getTime()
  return isNaN(t) ? NaN : t
}

/**
 * 两位数补零
 * @param {number} n
 * @returns {string}
 */
function pad(n) {
  const x = Math.max(0, Math.floor(Number(n) || 0))
  return x < 10 ? '0' + x : '' + x
}

/**
 * 格式化倒计时
 * @param {string|number|Date} endTime 结束时间
 * @param {object} [opts]
 * @param {number} [opts.urgentThresholdMs] 进入"急迫"状态的阈值（默认 10 分钟 = 600000ms）
 * @returns {{d:number, hh:string, mm:string, ss:string, expired:boolean, urgent:boolean, totalMs:number}}
 *
 * 示例:
 *   formatCountdown('2026-05-01 00:00:00')
 *   // { d: 13, hh: '05', mm: '42', ss: '08', expired: false, urgent: false, totalMs: ... }
 *
 *   formatCountdown(Date.now() - 1000)
 *   // { d: 0, hh: '00', mm: '00', ss: '00', expired: true, urgent: false, totalMs: 0 }
 */
export function formatCountdown(endTime, opts) {
  const urgentThreshold = (opts && opts.urgentThresholdMs) || 10 * 60 * 1000
  const end = toTimestamp(endTime)
  if (!isFinite(end)) {
    return { d: 0, hh: '00', mm: '00', ss: '00', expired: true, urgent: false, totalMs: 0 }
  }
  const diff = end - Date.now()
  if (diff <= 0) {
    return { d: 0, hh: '00', mm: '00', ss: '00', expired: true, urgent: false, totalMs: 0 }
  }
  const d = Math.floor(diff / 86400000)
  const hh = Math.floor((diff % 86400000) / 3600000)
  const mm = Math.floor((diff % 3600000) / 60000)
  const ss = Math.floor((diff % 60000) / 1000)
  return {
    d,
    hh: pad(hh),
    mm: pad(mm),
    ss: pad(ss),
    expired: false,
    urgent: diff <= urgentThreshold,
    totalMs: diff
  }
}

export default { formatCountdown }
