/**
 * iOS 兼容日期解析
 * iOS 不支持 "2026-04-11 19:48:47" 格式（空格分隔），需替换为 T 分隔
 */
export function safeParseDate(value) {
  if (!value) return null
  const d = new Date(String(value).replace(/ /g, 'T'))
  return isNaN(d.getTime()) ? null : d
}
