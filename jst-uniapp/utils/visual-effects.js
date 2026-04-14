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

// ═══════════════════════════════════════════
// REFACTOR-10 追加 — 视觉重塑工具函数
// ═══════════════════════════════════════════

/**
 * 滚动视差封装 — 根据 scrollTop 计算视差偏移/透明度
 * [visual-effect]
 * @param {number} scrollTop 当前滚动距离
 * @param {number} heroHeight Hero 区域高度(rpx)
 * @param {Object} options 可选参数
 * @returns {Object} { opacity, translateY, progress, isPastHero }
 */
export function scrollParallax(scrollTop, heroHeight = 400, options = {}) {
  const { parallaxRatio = 0.4, fadeStart = 0, fadeEnd = heroHeight * 0.6 } = options
  const progress = Math.min(scrollTop / heroHeight, 1)
  const translateY = scrollTop * parallaxRatio
  let opacity = 1
  if (scrollTop > fadeStart) {
    opacity = Math.max(0, 1 - (scrollTop - fadeStart) / (fadeEnd - fadeStart))
  }
  return {
    opacity: Math.max(0, Math.min(1, opacity)),
    translateY,
    progress,
    isPastHero: scrollTop >= heroHeight
  }
}

/**
 * 粘性导航状态计算 — 判断是否显示粘性导航
 * [visual-effect]
 * @param {number} scrollTop 当前滚动距离
 * @param {number} threshold 触发阈值(rpx)
 * @returns {Object} { show, opacity }
 */
export function stickyNavState(scrollTop, threshold = 300) {
  const show = scrollTop >= threshold
  const fadeRange = 60
  let opacity = 0
  if (scrollTop >= threshold) {
    opacity = Math.min(1, (scrollTop - threshold) / fadeRange)
  }
  return { show, opacity }
}

/**
 * 批量 countUp — 对多个字段同时执行数字滚动
 * [visual-effect]
 * @param {Object} targets { key: targetValue } 映射
 * @param {Function} setter (key, value) => void 回调
 * @param {number} duration 动画时长(ms)
 * @returns {Function} cancel 函数
 */
export function batchCountUp(targets, setter, duration = 800) {
  const cancels = []
  Object.entries(targets).forEach(function(entry) {
    const key = entry[0]
    const to = entry[1]
    if (typeof to === 'number' && to > 0) {
      cancels.push(countUp(0, to, duration, function(val) { setter(key, val) }))
    } else {
      setter(key, to)
    }
  })
  return function cancelAll() {
    cancels.forEach(function(c) { if (typeof c === 'function') c() })
  }
}
