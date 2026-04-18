/**
 * 金额与脱敏格式化工具
 *
 * 用法示例:
 *   import { formatMoney, formatMoneyK, formatCount, maskPhone, maskEmail, maskIdCard } from '@/utils/format'
 */

/**
 * 格式化金额
 * @param {number|string} num      金额数值（元）
 * @param {object} opts
 * @param {number} opts.decimals   小数位数，默认 2
 * @param {string} opts.currency   货币符号，默认 '¥'；传 '' 则不带符号
 * @param {boolean} opts.thousand  是否千分位，默认 true
 * @returns {string}
 *
 * 示例:
 *   formatMoney(1234.5)                       // "¥1,234.50"
 *   formatMoney(1234.5, { decimals: 0 })      // "¥1,235"
 *   formatMoney(99, { currency: '' })         // "99.00"
 *   formatMoney(null)                         // "¥0.00"
 */
export function formatMoney(num, opts) {
  const o = opts || {}
  const decimals = typeof o.decimals === 'number' ? o.decimals : 2
  const currency = typeof o.currency === 'string' ? o.currency : '¥'
  const thousand = o.thousand !== false
  let n = Number(num)
  if (!isFinite(n)) n = 0
  let s = n.toFixed(decimals)
  if (thousand) {
    const parts = s.split('.')
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    s = parts.join('.')
  }
  return currency + s
}

/**
 * 紧凑金额：≥1 万转 k、≥1 千万转 kw
 * @param {number|string} num
 * @returns {string}
 *
 * 示例:
 *   formatMoneyK(99)        // "¥99.00"
 *   formatMoneyK(24000)     // "¥2.4k"
 *   formatMoneyK(18500000)  // "¥1.9kw"
 */
export function formatMoneyK(num) {
  let n = Number(num)
  if (!isFinite(n)) n = 0
  const sign = n < 0 ? '-' : ''
  const abs = Math.abs(n)
  if (abs >= 10000000) {
    return sign + '¥' + (abs / 10000000).toFixed(1) + 'kw'
  }
  if (abs >= 10000) {
    return sign + '¥' + (abs / 10000).toFixed(1) + 'k'
  }
  return formatMoney(n)
}

/**
 * 计数格式化：超过 999 显示 "999+"
 * @param {number|string} num
 * @returns {string}
 *
 * 示例:
 *   formatCount(12)    // "12"
 *   formatCount(999)   // "999"
 *   formatCount(1200)  // "999+"
 *   formatCount(null)  // "0"
 */
export function formatCount(num) {
  const n = Number(num)
  if (!isFinite(n) || n <= 0) return '0'
  if (n > 999) return '999+'
  return String(Math.floor(n))
}

/**
 * 手机号脱敏（前 3 + **** + 后 4）
 * @param {string|number} phone
 * @returns {string}
 *
 * 示例:
 *   maskPhone('13800008001')  // "138****8001"
 *   maskPhone('')             // ""
 *   maskPhone('123')          // "123"（长度不足不脱敏）
 */
export function maskPhone(phone) {
  const s = phone == null ? '' : String(phone)
  if (s.length < 7) return s
  return s.slice(0, 3) + '****' + s.slice(-4)
}

/**
 * 邮箱脱敏（保留前 2 字符 + **** + @域名）
 * @param {string} email
 * @returns {string}
 *
 * 示例:
 *   maskEmail('zhangsan@example.com')  // "zh****@example.com"
 *   maskEmail('a@b.com')               // "a****@b.com"
 *   maskEmail('')                      // ""
 */
export function maskEmail(email) {
  const s = email == null ? '' : String(email)
  const idx = s.indexOf('@')
  if (idx <= 0) return s
  const name = s.slice(0, idx)
  const domain = s.slice(idx)
  const keep = Math.min(2, name.length)
  return name.slice(0, keep) + '****' + domain
}

/**
 * 身份证号脱敏（前 6 + 星号 + 后 4，共 18 位）
 * @param {string|number} idCard
 * @returns {string}
 *
 * 示例:
 *   maskIdCard('110101200001011234')  // "110101********1234"
 *   maskIdCard('123')                 // "123"（长度不足不脱敏）
 */
export function maskIdCard(idCard) {
  const s = idCard == null ? '' : String(idCard)
  if (s.length < 10) return s
  const head = s.slice(0, 6)
  const tail = s.slice(-4)
  const midLen = Math.max(0, s.length - 10)
  return head + '*'.repeat(midLen) + tail
}

export default {
  formatMoney,
  formatMoneyK,
  formatCount,
  maskPhone,
  maskEmail,
  maskIdCard
}
