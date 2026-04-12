<!-- 中文注释: 公告详情页；对应原型 小程序原型图/notice-detail.html；对应接口 GET /jst/wx/notice/{id} -->
<template>
  <view class="notice-detail">
    <view class="notice-detail__header" :style="{ paddingTop: navPaddingTop }">
      <view class="notice-detail__back" @tap="goBack">←</view>
      <text class="notice-detail__header-title">公告详情</text>
      <button class="notice-detail__more" open-type="share">⋯</button>
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
        <button class="notice-detail__button notice-detail__button--primary" open-type="share">
          分享
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
      safeContent: '',
      noticeId: ''
    }
  },
  onLoad(query) {
    this.noticeId = (query && query.id) || ''
    if (!this.noticeId) {
      return
    }
    this.loadDetail(this.noticeId)
  },
  onShareAppMessage() {
    return {
      title: this.notice.title || '竞赛通公告',
      path: `/pages-sub/notice/detail?id=${this.noticeId}`
    }
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

      const date = new Date(String(value).replace(/ /g, 'T'))
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

  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';
.notice-detail {
  min-height: 100vh;
  padding: 24rpx 24rpx 180rpx;
  background: #F7F8FA;
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
  background: $jst-bg-card;
  box-shadow: 0 2rpx 8rpx rgba(20, 30, 60, 0.04);
  font-size: 30rpx;
  color: $jst-text-primary;
  padding: 0;
  border: none;
  line-height: 72rpx;
}
.notice-detail__more::after { display: none; }

.notice-detail__header-title {
  flex: 1;
  margin: 0 16rpx;
  font-size: 32rpx;
  font-weight: 600;
  color: $jst-text-primary;
  text-align: center;
}

.notice-detail__article {
  overflow: hidden;
  margin-top: 24rpx;
  border-radius: 32rpx;
  background: $jst-bg-card;
  box-shadow: 0 2rpx 8rpx rgba(20, 30, 60, 0.04);
}

.notice-detail__cover {
  width: 100%;
  height: 320rpx;
  background: $jst-border;
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
  padding: 6rpx 16rpx;
  border-radius: 999rpx;
  background: #EAF4FC;
  font-size: 22rpx;
  font-weight: 500;
  color: #283593;
}

.notice-detail__tag--top {
  background: #FFF3EE;
  color: #ff6b35;
}

.notice-detail__title {
  display: block;
  margin-top: 20rpx;
  font-size: 40rpx;
  font-weight: 600;
  line-height: 1.45;
  color: $jst-text-primary;
}

.notice-detail__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 24rpx;
  margin-top: 18rpx;
}

.notice-detail__meta-item {
  font-size: 22rpx;
  color: $jst-text-secondary;
}

.notice-detail__content {
  margin-top: 28rpx;
  font-size: 28rpx;
  line-height: 1.85;
  color: $jst-text-regular;
}

.notice-detail__bottom {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: 24rpx;
  display: flex;
  gap: 16rpx;
  padding: 18rpx;
  border-radius: 32rpx;
  background: $jst-bg-card;
  box-shadow: 0 4rpx 16rpx rgba(20, 30, 60, 0.06);
}

.notice-detail__button {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
  height: 84rpx;
  border-radius: $jst-radius-xl;
  font-size: 28rpx;
  font-weight: 600;
}

.notice-detail__button--secondary {
  border: 2rpx solid $jst-border;
  color: $jst-text-regular;
  background: $jst-bg-card;
}

.notice-detail__button--primary {
  color: #fff;
  background: linear-gradient(135deg, #283593, #3949AB);
  box-shadow: 0 4rpx 12rpx rgba(40, 53, 147, 0.2);
}
</style>
