/**
 * 微信小程序自定义导航栏高度计算
 *
 * 当 navigationStyle: "custom" 时，原生导航栏被隐藏，但状态栏和右上角胶囊按钮仍在。
 * 页面需要自行预留这部分高度，否则内容会被遮挡。
 *
 * 计算公式：
 *   navBarHeight = statusBarHeight + capsuleHeight + (capsuleTop - statusBarHeight) * 2
 *   其中 capsuleTop - statusBarHeight 是胶囊按钮相对导航区顶部的偏移
 */

let cachedNavBarInfo = null

export function getNavBarInfo() {
  if (cachedNavBarInfo) return cachedNavBarInfo

  const sysInfo = uni.getSystemInfoSync()
  const statusBarHeight = sysInfo.statusBarHeight || 0

  // #ifdef MP-WEIXIN
  try {
    const menuBtn = uni.getMenuButtonBoundingClientRect()
    const capsuleHeight = menuBtn.height || 32
    const capsuleTop = menuBtn.top || statusBarHeight + 4
    // 胶囊到状态栏的间距，上下对称
    const gap = capsuleTop - statusBarHeight
    cachedNavBarInfo = {
      statusBarHeight,
      navBarHeight: statusBarHeight + capsuleHeight + gap * 2,
      capsuleHeight,
      capsuleTop,
      capsuleRight: menuBtn.right || 0,
      capsuleWidth: menuBtn.width || 87
    }
    return cachedNavBarInfo
  } catch (e) {
    // fallback
  }
  // #endif

  // H5 / App 不需要预留胶囊高度，用固定值
  cachedNavBarInfo = {
    statusBarHeight,
    navBarHeight: statusBarHeight + 44,
    capsuleHeight: 32,
    capsuleTop: statusBarHeight + 4,
    capsuleRight: 0,
    capsuleWidth: 0
  }
  return cachedNavBarInfo
}

/**
 * Vue mixin: 在 data() 中注入 navBarInfo，在 computed 中提供常用样式
 * 用法: mixins: [navBarMixin]
 * 然后在 template 中用 :style="{ paddingTop: navPaddingTop }"
 */
export const navBarMixin = {
  data() {
    return {
      navBarInfo: getNavBarInfo()
    }
  },
  computed: {
    // 状态栏高度 (px)
    statusBarHeightPx() {
      return this.navBarInfo.statusBarHeight + 'px'
    },
    // 整个导航栏高度 (px)，包含状态栏
    navBarHeightPx() {
      return this.navBarInfo.navBarHeight + 'px'
    },
    // 常用：页面顶部 padding-top
    navPaddingTop() {
      return this.navBarInfo.navBarHeight + 'px'
    }
  }
}
