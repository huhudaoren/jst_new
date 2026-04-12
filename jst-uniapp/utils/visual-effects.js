/**
 * 竞赛通 — 可复用视觉效果工具函数
 * 所有函数均为纯视觉用途，删除后不影响业务功能
 */

/**
 * 节流函数 — 限制 onPageScroll 等高频回调的执行频率
 * @param {Function} fn 目标函数
 * @param {number} delay 最小间隔(ms)，默认 16ms (~60fps)
 * @returns {Function}
 */
export function throttle(fn, delay = 16) {
  let last = 0
  return function (...args) {
    const now = Date.now()
    if (now - last >= delay) {
      last = now
      fn.apply(this, args)
    }
  }
}

/**
 * 数字滚动动画 — 从 from 滚动到 to
 * @param {number} from 起始值
 * @param {number} to 目标值
 * @param {number} duration 动画时长(ms)，默认 800
 * @param {Function} callback 每帧回调，参数为当前值
 * @returns {Function} cancel 函数，调用可中止动画
 */
export function countUp(from, to, duration = 800, callback) {
  const start = Date.now()
  const diff = to - from
  let raf = null

  function step() {
    const elapsed = Date.now() - start
    const progress = Math.min(elapsed / duration, 1)
    // ease-out cubic
    const eased = 1 - Math.pow(1 - progress, 3)
    const current = Math.round(from + diff * eased)
    callback(current)
    if (progress < 1) {
      raf = setTimeout(step, 16)
    }
  }

  step()
  return function cancel() {
    if (raf) clearTimeout(raf)
  }
}

/**
 * IntersectionObserver 封装 — 监听元素进入视口，触发入场动画
 * @param {Object} component Vue 组件实例 (this)
 * @param {string} selector CSS 选择器
 * @param {Function} callback 回调，参数 { intersectionRatio }
 * @param {Object} options { thresholds, initialRatio, observeAll }
 * @returns {Object|null} observer 实例，调用 .disconnect() 可销毁
 */
export function createObserver(component, selector, callback, options = {}) {
  const { thresholds = [0.1], observeAll = false } = options
  try {
    const observer = uni.createIntersectionObserver(component, {
      thresholds,
      observeAll
    })
    observer.relativeToViewport().observe(selector, callback)
    return observer
  } catch (e) {
    // 降级：直接触发回调
    callback({ intersectionRatio: 1 })
    return null
  }
}

/**
 * 计算交错动画延迟 — 用于列表入场编排
 * @param {number} index 列表索引
 * @param {number} base 基础延迟(ms)，默认 60
 * @param {number} max 最大延迟(ms)，默认 500
 * @returns {string} 可直接用于 :style 的 animation-delay 值
 */
export function staggerDelay(index, base = 60, max = 500) {
  const delay = Math.min(index * base, max)
  return `${delay}ms`
}
