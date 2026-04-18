<!--
  ═══════════════════════════════════════════════════════════════
  详情页模板 — 复制我, 重命名, 替换 __PageName__
  ═══════════════════════════════════════════════════════════════

  覆盖场景:
    - Hero 区 (渐变背景 + 标题 + 状态) + 下沉主卡片
    - navPaddingTop (自定义导航栏留白, 全局 navBarMixin 提供)
    - 三态切换: 加载中(骨架屏) / 加载失败(错误态带重试) / 成功(内容)
    - 底部固定 CTA + safeAreaBottom 安全区

  替换 checklist:
    1) 把 __PageName__ 换成实际组件名 (如 OrderDetail)
    2) 改 loadDetail 调用真实 API
    3) 若不需要底部 CTA, 删除 <view class="page__cta-bar">
    4) 若不需要 Hero, 直接把 template 第一块删掉
-->
<template>
  <view class="page">
    <!-- 自定义导航 (透明浮层, Hero 滚动时透出底色) -->
    <view class="page__nav" :style="{ paddingTop: navPaddingTop }">
      <view class="page__back" @tap="handleBack">←</view>
      <text class="page__title">__详情标题__</text>
      <view class="page__nav-placeholder"></view>
    </view>

    <!-- 首屏骨架 -->
    <jst-skeleton-plus v-if="pageLoading" mode="hero" />

    <!-- 错误态 (网络失败) -->
    <view v-else-if="loadError" class="page__error-wrap">
      <jst-empty
        icon="📡"
        text="网络开了个小差"
        desc="检查网络后点击重试"
        button-text="重新加载"
        @action="loadDetail"
      />
    </view>

    <!-- 正文 -->
    <block v-else-if="detail">
      <!-- Hero 区 -->
      <view class="page__hero" :style="{ paddingTop: navBarHeightPx }">
        <text class="page__hero-title">{{ detail.title || '' }}</text>
        <text class="page__hero-sub">{{ detail.subtitle || '' }}</text>
        <view class="page__hero-tags">
          <text class="page__hero-tag">{{ detail.statusText || '进行中' }}</text>
        </view>
      </view>

      <!-- 主内容卡片 -->
      <view class="page__body">
        <view class="page__card">
          <view class="page__card-title">基础信息</view>
          <view class="page__row">
            <text class="page__row-key">编号</text>
            <text class="page__row-val">{{ detail.no || '--' }}</text>
          </view>
          <view class="page__row">
            <text class="page__row-key">创建时间</text>
            <text class="page__row-val">{{ formatTime(detail.createTime) }}</text>
          </view>
        </view>

        <!-- TODO: 更多卡片 -->
      </view>

      <!-- 底部固定 CTA -->
      <view class="page__cta-bar" :style="{ paddingBottom: safeAreaBottom }">
        <u-button
          class="page__cta-btn page__cta-btn--ghost"
          :disabled="submitting"
          @click="handleSecondary"
        >次要操作</u-button>
        <u-button
          class="page__cta-btn page__cta-btn--primary"
          :loading="submitting"
          :disabled="!canPrimary"
          @click="handlePrimary"
        >主要操作</u-button>
      </view>
    </block>
  </view>
</template>

<script>
import jstPageHelper from '@/mixins/jst-page-helper'
import { retryableRequest } from '@/utils/request-interceptor'
// import { __getDetail__ } from '@/api/__module__'

export default {
  name: '__PageName__',
  mixins: [jstPageHelper],
  data() {
    return {
      id: '',
      detail: null,
      pageLoading: false,
      loadError: false,
      submitting: false
    }
  },
  computed: {
    canPrimary() {
      return !!this.detail && !this.submitting
    }
  },
  onLoad(query) {
    this.id = query && query.id
    this.loadDetail()
  },
  methods: {
    async loadDetail() {
      this.pageLoading = true
      this.loadError = false
      try {
        // TODO: 用真实 API
        // this.detail = await retryableRequest(
        //   { url: '/jst/wx/xxx/detail', method: 'GET', data: { id: this.id }, silent: true },
        //   { maxRetries: 2 }
        // )
        this.detail = { id: this.id, title: '示例', no: 'N001', createTime: new Date().toISOString() }
      } catch (e) {
        this.loadError = true
      } finally {
        this.pageLoading = false
      }
    },

    async handlePrimary() {
      const ok = await this.confirmAsync({
        title: '确认操作',
        content: '请确认以下操作',
        destructive: false
      })
      if (!ok) return

      this.submitting = true
      try {
        // TODO: 调用提交 API
        // await api.submit(this.id)
        this.showToastSuccess('操作成功')
        setTimeout(() => uni.navigateBack(), 400)
      } catch (e) {
        this.showToastError('操作失败，请重试')
      } finally {
        this.submitting = false
      }
    },

    handleSecondary() {
      // TODO
    },

    formatTime(v) {
      return v ? String(v).replace('T', ' ').slice(0, 16) : '--'
    },

    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
      } else {
        uni.switchTab({ url: '/pages/my/index' })
      }
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.page {
  min-height: 100vh;
  background: $jst-bg-page;
  padding-bottom: 180rpx;  // 底部 CTA 占位
}

.page__nav {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 $jst-page-padding;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 40;
}

.page__back,
.page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.page__back {
  font-size: 36rpx;
  color: $jst-text-inverse;
}

.page__title {
  flex: 1;
  text-align: center;
  font-size: $jst-font-lg;
  font-weight: $jst-weight-bold;
  color: $jst-text-inverse;
}

.page__hero {
  padding: $jst-space-xl $jst-page-padding $jst-space-xxl;
  background: $jst-hero-gradient;
  color: $jst-text-inverse;
}

.page__hero-title {
  display: block;
  font-size: $jst-font-xxl;
  font-weight: $jst-weight-bold;
  line-height: 1.3;
}

.page__hero-sub {
  display: block;
  margin-top: $jst-space-sm;
  font-size: $jst-font-base;
  color: rgba(255, 255, 255, 0.82);
}

.page__hero-tags {
  margin-top: $jst-space-lg;
  display: flex;
  gap: $jst-space-sm;
}

.page__hero-tag {
  padding: $jst-space-xs $jst-space-md;
  border-radius: $jst-radius-round;
  background: rgba(255, 255, 255, 0.18);
  font-size: $jst-font-xs;
  color: $jst-text-inverse;
}

.page__body {
  margin-top: -$jst-space-lg;
  padding: 0 $jst-page-padding $jst-space-xl;
}

.page__card {
  padding: $jst-space-lg;
  margin-bottom: $jst-space-md;
  border-radius: $jst-radius-card;
  background: $jst-bg-card;
  box-shadow: $jst-card-glow;
}

.page__card-title {
  font-size: $jst-font-md;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
  margin-bottom: $jst-space-md;
}

.page__row {
  display: flex;
  justify-content: space-between;
  padding: $jst-space-sm 0;
}

.page__row-key {
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.page__row-val {
  font-size: $jst-font-sm;
  color: $jst-text-primary;
}

.page__error-wrap {
  padding-top: 200rpx;
}

/* 底部固定 CTA */
.page__cta-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  gap: $jst-space-md;
  padding: $jst-space-md $jst-page-padding;
  background: $jst-bg-card;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.06);
  z-index: 30;
}

::v-deep .page__cta-btn.u-button {
  flex: 1;
  height: 88rpx;
  border: none;
  border-radius: $jst-radius-button;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
}

::v-deep .page__cta-btn--ghost.u-button {
  background: $jst-bg-grey;
  color: $jst-text-regular;
}

::v-deep .page__cta-btn--primary.u-button {
  background: $jst-brand-gradient;
  color: $jst-text-inverse;
}
</style>
