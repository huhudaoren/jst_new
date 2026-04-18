/**
 * ═══════════════════════════════════════════════════════════════
 * global-actions — 非组件作用域下的通用 UI 动作
 * ═══════════════════════════════════════════════════════════════
 *
 * 场景: api / store / utils 等非 Vue 组件上下文里, 没有 this.$jst,
 *       但需要 toast / modal / loading。import 本文件直接用。
 *
 * 与 jst-page-helper mixin 的区别:
 *   - mixin 只在组件 setup 后通过 this.xxx 调用
 *   - global-actions 是纯函数, 任意地方 import 即可用
 *   - 全局可注入: Vue.prototype.$jst = globalActions (由 main.js 自行决定是否挂)
 *
 * ▼ 使用示例
 *   import { showToast, showModalAsync, withLoading } from '@/utils/global-actions'
 *
 *   await showModalAsync({ title: '确认离开?', destructive: true })
 *   showToast('success', '保存成功')
 *   await withLoading('上传中...', async () => {
 *     await api.upload(file)
 *   })
 */

// ─── Toast ───

const ICON_MAP = {
  success: 'success',
  error: 'none',     // uni 没有 error icon, 用无 icon + 文字足够
  warning: 'none',
  none: 'none',
  loading: 'loading'
}

/**
 * @param {'success'|'error'|'warning'|'none'|'loading'} type
 * @param {string} text
 * @param {number} [duration=2000]
 */
export function showToast(type, text, duration = 2000) {
  const icon = ICON_MAP[type] || 'none'
  uni.showToast({
    title: text || (type === 'success' ? '操作成功' : '操作失败'),
    icon,
    duration,
    mask: type === 'success' || type === 'loading'
  })
}

// ─── Modal ───

/**
 * Promise 化的 uni.showModal
 * @param {Object} opts
 * @param {string} opts.title
 * @param {string} [opts.content='']
 * @param {string} [opts.confirmText='确认']
 * @param {string} [opts.cancelText='取消']
 * @param {boolean} [opts.destructive=false]
 * @param {boolean} [opts.showCancel=true]
 * @returns {Promise<boolean>}
 */
export function showModalAsync(opts = {}) {
  const {
    title = '提示',
    content = '',
    confirmText = '确认',
    cancelText = '取消',
    destructive = false,
    showCancel = true
  } = opts
  return new Promise((resolve) => {
    uni.showModal({
      title,
      content,
      confirmText,
      cancelText,
      showCancel,
      confirmColor: destructive ? '#FF4D4F' : '#2B6CFF',
      success: (res) => resolve(!!res.confirm),
      fail: () => resolve(false)
    })
  })
}

// ─── Loading (嵌套计数, 防闪烁) ───

let loadingCount = 0

/**
 * 显示 loading (支持嵌套; 调多少次就要 hideLoading 多少次)
 */
export function showLoading(text = '处理中...') {
  loadingCount += 1
  if (loadingCount === 1) {
    uni.showLoading({ title: text, mask: true })
  }
}

/**
 * 关闭 loading (配对调用)
 */
export function hideLoading() {
  loadingCount = Math.max(0, loadingCount - 1)
  if (loadingCount === 0) {
    uni.hideLoading()
  }
}

/**
 * 语法糖: 在 async fn 周围自动 show/hide
 * @param {string} text
 * @param {Function} fn async 函数
 */
export async function withLoading(text, fn) {
  showLoading(text)
  try {
    return await fn()
  } finally {
    hideLoading()
  }
}

// ─── 兜底导出 (可挂 Vue.prototype.$jst) ───

export default {
  showToast,
  showModalAsync,
  showLoading,
  hideLoading,
  withLoading
}
