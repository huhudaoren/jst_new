<!-- 中文注释: 公告详情页；对应原型 小程序原型图/notice-detail.html；对应接口 GET /jst/wx/notice/{id} -->
<template>
  <view class="notice-detail">
    <view class="notice-detail__header">
      <view class="notice-detail__back" @tap="goBack">←</view>
      <text class="notice-detail__header-title">公告详情</text>
      <view class="notice-detail__more" @tap="showShareComingSoon">⋯</view>
    </view>

    <jst-loading :loading="loading" text="公告详情加载中..." />

    <template v-if="notice.noticeId">
      <view class="notice-detail__article">
        <image
          v-if="notice.coverImage"
          class="notice-detail__cover"
          :src="notice.coverImage"
          mode="aspectFill"
        />

        <view class="notice-detail__article-body">
          <view class="notice-detail__tags">
            <text v-if="notice.topFlag" class="notice-detail__tag notice-detail__tag--top">置顶公告</text>
            <text class="notice-detail__tag">{{ notice.categoryLabel || '公告资讯' }}</text>
          </view>

          <text class="notice-detail__title">{{ notice.title }}</text>

          <view class="notice-detail__meta">
            <text class="notice-detail__meta-item">📅 {{ formatDate(notice.publishTime) }}</text>
            <text class="notice-detail__meta-item">🏛️ 竞赛通官方</text>
          </view>

          <view class="notice-detail__content">
            <rich-text :nodes="safeContent"></rich-text>
          </view>
        </view>
      </view>

      <view class="notice-detail__bottom">
        <button class="notice-detail__button notice-detail__button--secondary" @tap="goBack">
          返回公告
        </button>
        <button class="notice-detail__button notice-detail__button--primary" @tap="showShareComingSoon">
          分享占位
        </button>
      </view>
    </template>

    <jst-empty
      v-else-if="!loading"
      text="公告不存在或已下线"
      icon="📄"
    />
  </view>
</template>

<script>
import { getNoticeDetail } from '@/api/notice'

export default {
  data() {
    return {
      loading: false,
      notice: {},
      safeContent: ''
    }
  },
  onLoad(query) {
    const noticeId = query && query.id
    if (!noticeId) {
      return
    }
    this.loadDetail(noticeId)
  },
  methods: {
    async loadDetail(noticeId) {
      this.loading = true

      try {
        const detail = await getNoticeDetail(noticeId)
        this.notice = this.normalizeNotice(detail || {})
        this.safeContent = this.sanitizeHtml(this.notice.content)
      } catch (error) {
        this.notice = {}
        this.safeContent = ''
      } finally {
        this.loading = false
      }
    },

    normalizeNotice(detail = {}) {
      return {
        noticeId: detail.noticeId || detail.id || '',
        title: detail.title || '',
        category: detail.category || '',
        categoryLabel: detail.categoryLabel || this.getCategoryLabel(detail.category),
        publishTime: detail.publishTime || detail.createTime || '',
        coverImage: detail.coverImage || detail.image || '',
        content: detail.content || '',
        topFlag: Boolean(detail.topFlag)
      }
    },

    getCategoryLabel(category) {
      const labelMap = {
        platform: '平台公告',
        contest: '赛事通知',
        score: '成绩发布',
        certificate: '证书通知'
      }
      return labelMap[category] || '公告资讯'
    },

    sanitizeHtml(html) {
      if (!html) {
        return '<p>暂无正文内容</p>'
      }

      return String(html)
        .replace(/<script[\s\S]*?>[\s\S]*?<\/script>/gi, '')
        .replace(/<style[\s\S]*?>[\s\S]*?<\/style>/gi, '')
        .replace(/\son\w+=(['"]).*?\1/gi, '')
        .replace(/\son\w+=([^\s>]+)/gi, '')
        .replace(/javascript:/gi, '')
    },

    formatDate(value) {
      if (!value) {
        return '--'
      }

      const date = new Date(value)
      if (Number.isNaN(date.getTime())) {
        return value
      }

      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },

    goBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }
      uni.switchTab({ url: '/pages/notice/list' })
    },

    showShareComingSoon() {
      uni.showToast({
        title: '分享功能后续开放',
        icon: 'none'
      })
    }
  }
}
</script>

<style scoped lang="scss">
.notice-detail {
  min-height: 100vh;
  padding: 24rpx 24rpx 180rpx;
  background: var(--jst-color-page-bg);
}

.notice-detail__header {
  display: flex;
  align-items: center;
}

.notice-detail__back,
.notice-detail__more {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72rpx;
  height: 72rpx;
  border-radius: 22rpx;
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
  font-size: 30rpx;
  color: var(--jst-color-text);
}

.notice-detail__header-title {
  flex: 1;
  margin: 0 16rpx;
  font-size: 32rpx;
  font-weight: 800;
  color: var(--jst-color-text);
  text-align: center;
}

.notice-detail__article {
  overflow: hidden;
  margin-top: 24rpx;
  border-radius: var(--jst-radius-lg);
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
}

.notice-detail__cover {
  width: 100%;
  height: 320rpx;
  background: var(--jst-color-border);
}

.notice-detail__article-body {
  padding: 32rpx 28rpx;
}

.notice-detail__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.notice-detail__tag {
  padding: 8rpx 18rpx;
  border-radius: var(--jst-radius-full);
  background: var(--jst-color-brand-soft);
  font-size: 22rpx;
  color: var(--jst-color-brand);
}

.notice-detail__tag--top {
  background: var(--jst-color-primary-soft);
  color: var(--jst-color-primary);
}

.notice-detail__title {
  display: block;
  margin-top: 20rpx;
  font-size: 40rpx;
  font-weight: 800;
  line-height: 1.45;
  color: var(--jst-color-text);
}

.notice-detail__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 24rpx;
  margin-top: 18rpx;
}

.notice-detail__meta-item {
  font-size: 22rpx;
  color: var(--jst-color-text-tertiary);
}

.notice-detail__content {
  margin-top: 28rpx;
  font-size: 28rpx;
  line-height: 1.85;
  color: var(--jst-color-text-secondary);
}

.notice-detail__bottom {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: 24rpx;
  display: flex;
  gap: 16rpx;
  padding: 18rpx;
  border-radius: var(--jst-radius-lg);
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-strong);
}

.notice-detail__button {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
  height: 84rpx;
  border-radius: var(--jst-radius-md);
  font-size: 28rpx;
  font-weight: 700;
}

.notice-detail__button--secondary {
  border: 2rpx solid var(--jst-color-border);
  color: var(--jst-color-text-secondary);
  background: var(--jst-color-card-bg);
}

.notice-detail__button--primary {
  color: var(--jst-color-card-bg);
  background: linear-gradient(135deg, var(--jst-color-primary) 0%, var(--jst-color-primary-light) 100%);
}
</style>
