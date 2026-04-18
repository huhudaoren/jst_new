/**
 * ═══════════════════════════════════════════════════════════════
 * jst-page-helper mixin — 页面级通用工具（toast / modal / 安全区）
 * ═══════════════════════════════════════════════════════════════
 *
 * 为什么需要: 审计显示全站 toast 文案 / icon 混乱 (7 种 "加载中..." 变体),
 * 二次确认弹窗散落各处无统一 destructive 视觉。此 mixin 提供一组可信 API。
 *
 * ▼ 与 navBarMixin 的关系
 *   main.js 已通过 Vue.mixin(navBarMixin) 全局注入 navPaddingTop / statusBarHeightPx /
 *   navBarHeightPx。本 mixin 不覆盖这些, 而是补齐 safeAreaBottom + toast/modal 语法糖。
 *
 * ▼ 使用示例
 *   <script>
 *   import jstPageHelper from '@/mixins/jst-page-helper'
 *   export default {
 *     mixins: [jstPageHelper],
 *     methods: {
 *       async onDelete(item) {
 *         const ok = await this.confirmAsync({
 *           title: '删除确认', content: '删除后不可恢复', destructive: true
 *         })
 *         if (!ok) return
 *         this.showToastLoading('删除中...')
 *         try {
 *           await api.remove(item.id)
 *           this.showToastSuccess('已删除')
 *         } catch (e) {
 *           this.showToastError('删除失败，请重试')
 *         }
 *       }
 *     }
 *   }
 *   </script>
 *
 *   <template>
 *     <view class="page" :style="{ paddingBottom: safeAreaBottom }">
 *       ...
 *     </view>
 *   </template>
 *
 * ▼ 提供的 API
 *   computed.safeAreaBottom       → 'env(safe-area-inset-bottom)' (iOS 横竖屏底部安全区)
 *   methods.showToastSuccess(text, duration=1500)
 *   methods.showToastError(text, duration=2000)
 *   methods.showToastLoading(text='处理中...')   // 注意: 需后续 hideLoading 关闭
 *   methods.showToastNone(text, duration=2000)   // 纯文字无 icon
 *   methods.confirmAsync({ title, content, confirmText='确认', cancelText='取消', destructive=false }) => Promise<boolean>
 */

export default {
  computed: {
    /**
     * iOS 刘海屏底部安全区 - 用于固定底部按钮 / 分页容器留白
     * 用法: :style="{ paddingBottom: safeAreaBottom }"
     */
    safeAreaBottom() {
      // constant() 是旧 iOS 兼容, env() 是新标准
      return 'calc(env(safe-area-inset-bottom) + 0rpx)'
    }
  },

  methods: {
    // ─── Toast 统一入口 ───

    showToastSuccess(text, duration = 1500) {
      uni.showToast({ title: text || '操作成功', icon: 'success', duration, mask: true })
    },

    showToastError(text, duration = 2000) {
      uni.showToast({ title: text || '操作失败，请重试', icon: 'none', duration, mask: false })
    },

    showToastLoading(text = '处理中...') {
      // 注意: showLoading 与 showToast 互斥; 调用方需 hideLoading 关闭
      uni.showLoading({ title: text, mask: true })
    },

    showToastNone(text, duration = 2000) {
      uni.showToast({ title: text || '', icon: 'none', duration })
    },

    // ─── Modal Promise 化 ───

    /**
     * 二次确认弹窗
     * @param {Object} opts
     * @param {string} opts.title 标题
     * @param {string} opts.content 正文
     * @param {string} [opts.confirmText='确认']
     * @param {string} [opts.cancelText='取消']
     * @param {boolean} [opts.destructive=false] true 时确认按钮变红
     * @returns {Promise<boolean>} true=用户点击确认, false=取消/关闭
     */
    confirmAsync(opts = {}) {
      const {
        title = '提示',
        content = '',
        confirmText = '确认',
        cancelText = '取消',
        destructive = false
      } = opts
      return new Promise((resolve) => {
        uni.showModal({
          title,
          content,
          confirmText,
          cancelText,
          confirmColor: destructive ? '#FF4D4F' : '#2B6CFF',
          success: (res) => resolve(!!res.confirm),
          fail: () => resolve(false)
        })
      })
    }
  }
}
