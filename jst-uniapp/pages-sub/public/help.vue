<!-- 中文注释: 客服与帮助中心页；对应原型 小程序原型图/help.html + help.png；静态 FAQ 与联系方式，无直接业务接口 -->
<template>
  <view class="help-page">
    <view class="help-page__nav" :style="{ paddingTop: navPaddingTop }">
      <view class="help-page__back" @tap="handleBack">←</view>
      <text class="help-page__nav-title">客服与帮助</text>
      <view class="help-page__nav-placeholder"></view>
    </view>

    <view class="help-page__search">
      <text class="help-page__search-icon">⌕</text>
      <input
        v-model.trim="keyword"
        class="help-page__search-input"
        placeholder="搜索常见问题..."
        placeholder-class="help-page__placeholder"
      />
    </view>

    <view class="help-page__hero" @tap="contactOnlineService">
      <view class="help-page__hero-icon">💬</view>
      <view class="help-page__hero-body">
        <text class="help-page__hero-title">在线客服</text>
        <text class="help-page__hero-subtitle">平均响应时间 &lt; 3 分钟</text>
        <text class="help-page__hero-tag">在线服务中</text>
      </view>
      <text class="help-page__hero-arrow">›</text>
    </view>

    <view class="help-page__contact-grid">
      <view class="help-page__contact-card" @tap="copyContact('400-888-0000', '客服电话已复制')">
        <view class="help-page__contact-icon-wrap help-page__contact-icon-wrap--phone">
          <text class="help-page__contact-icon">📞</text>
        </view>
        <text class="help-page__contact-name">电话客服</text>
        <text class="help-page__contact-value">400-888-0000</text>
        <text class="help-page__contact-desc">工作日 9:00-18:00</text>
      </view>

      <view class="help-page__contact-card" @tap="copyContact('jst_service', '微信号已复制')">
        <view class="help-page__contact-icon-wrap help-page__contact-icon-wrap--wechat">
          <text class="help-page__contact-icon">⌘</text>
        </view>
        <text class="help-page__contact-name">微信客服</text>
        <text class="help-page__contact-value">jst_service</text>
        <text class="help-page__contact-desc">扫码添加好友</text>
      </view>

      <view class="help-page__contact-card" @tap="copyContact('service@jingsaitong.com', '邮箱已复制')">
        <view class="help-page__contact-icon-wrap help-page__contact-icon-wrap--mail">
          <text class="help-page__contact-icon">✉</text>
        </view>
        <text class="help-page__contact-name">邮件客服</text>
        <text class="help-page__contact-value">service@jingsaitong.com</text>
        <text class="help-page__contact-desc">1 个工作日回复</text>
      </view>

      <view class="help-page__contact-card" @tap="goNoticeTab">
        <view class="help-page__contact-icon-wrap help-page__contact-icon-wrap--notice">
          <text class="help-page__contact-icon">📢</text>
        </view>
        <text class="help-page__contact-name">官方公告</text>
        <text class="help-page__contact-value">查看最新通知</text>
        <text class="help-page__contact-desc">公告与资讯</text>
      </view>
    </view>

    <view class="help-page__section">
      <view class="help-page__section-head">
        <text class="help-page__section-title">常见问题</text>
      </view>

      <scroll-view class="help-page__cats" scroll-x show-scrollbar="false">
        <view class="help-page__cats-inner">
          <view
            v-for="cat in faqCategories"
            :key="cat.value"
            class="help-page__cat"
            :class="{ 'help-page__cat--active': currentCategory === cat.value }"
            @tap="currentCategory = cat.value"
          >
            {{ cat.label }}
          </view>
        </view>
      </scroll-view>

      <view class="help-page__faq-list">
        <view
          v-for="item in filteredFaqList"
          :key="item.id"
          class="help-page__faq-item"
        >
          <view class="help-page__faq-question" @tap="toggleFaq(item.id)">
            <text class="help-page__faq-question-text">{{ item.question }}</text>
            <text class="help-page__faq-icon">{{ openedFaqIds.includes(item.id) ? '−' : '+' }}</text>
          </view>
          <view v-if="openedFaqIds.includes(item.id)" class="help-page__faq-answer">
            {{ item.answer }}
          </view>
        </view>

        <jst-empty
          v-if="!filteredFaqList.length"
          icon="📭"
          text="未找到匹配的问题，请换个关键词试试"
        />
      </view>
    </view>

    <view class="help-page__section">
      <view class="help-page__section-head">
        <text class="help-page__section-title">服务承诺</text>
      </view>

      <view class="help-page__promise-item">
        <text class="help-page__promise-icon">⚡</text>
        <view class="help-page__promise-body">
          <text class="help-page__promise-title">在线客服 3 分钟响应</text>
          <text class="help-page__promise-desc">工作日 9:00-21:00，节假日 10:00-18:00</text>
        </view>
      </view>

      <view class="help-page__promise-item">
        <text class="help-page__promise-icon">🔒</text>
        <view class="help-page__promise-body">
          <text class="help-page__promise-title">信息安全加密保护</text>
          <text class="help-page__promise-desc">个人信息按平台规范存储与处理，不向第三方直接共享</text>
        </view>
      </view>

      <view class="help-page__promise-item">
        <text class="help-page__promise-icon">✅</text>
        <view class="help-page__promise-body">
          <text class="help-page__promise-title">官方授权赛事保障</text>
          <text class="help-page__promise-desc">平台展示赛事均需完成主办方资质审核后方可上线</text>
        </view>
      </view>
    </view>

    <view class="help-page__section">
      <view class="help-page__section-head">
        <text class="help-page__section-title">协议与政策</text>
      </view>

      <view class="help-page__doc-item" @tap="showDoc('用户服务协议')">
        <text class="help-page__doc-name">用户服务协议</text>
        <text class="help-page__doc-arrow">›</text>
      </view>
      <view class="help-page__doc-item" @tap="showDoc('隐私保护政策')">
        <text class="help-page__doc-name">隐私保护政策</text>
        <text class="help-page__doc-arrow">›</text>
      </view>
    </view>

    <view class="help-page__footer">
      <text class="help-page__footer-text">没找到答案？</text>
      <button class="help-page__footer-button" @tap="contactOnlineService">联系在线客服</button>
    </view>
  </view>
</template>

<script>
const FAQ_LIST = [
  {
    id: 'enroll-refund',
    category: 'enroll',
    question: '报名后还能退款吗？',
    answer: '报名成功并完成缴费后，原则上不支持退款。若因赛事方原因导致赛事取消，平台将按赛事公告执行退款或改期处理。'
  },
  {
    id: 'enroll-audit',
    category: 'enroll',
    question: '报名审核需要多久？',
    answer: '一般情况下 1 到 3 个工作日内完成审核，审核结果会通过站内通知或短信告知。'
  },
  {
    id: 'pay-method',
    category: 'pay',
    question: '支持哪些支付方式？',
    answer: '当前以微信支付为主，部分赛事支持渠道代缴或线下对公转账，具体以赛事详情页说明为准。'
  },
  {
    id: 'cert-format',
    category: 'cert',
    question: '证书支持哪些下载格式？',
    answer: '证书支持图片格式与 PDF 格式下载，具体入口会在成绩或证书模块开放后提供。'
  },
  {
    id: 'bind-channel',
    category: 'bind',
    question: '如何绑定老师或机构？',
    answer: '可以通过扫码绑定码或在个人中心手动输入绑定码完成绑定，绑定后可享受对应渠道服务。'
  },
  {
    id: 'account-mobile',
    category: 'account',
    question: '如何更换绑定手机号？',
    answer: '手机号属于高敏感登录标识，当前不支持小程序内自助更换，可联系客服协助处理。'
  },
  {
    id: 'partner-apply',
    category: 'partner',
    question: '赛事方入驻审核一般多久有结果？',
    answer: '赛事方入驻申请一般在 1 到 3 个工作日内完成初审，如需补件，审核页会展示补件说明。'
  },
  {
    id: 'partner-material',
    category: 'partner',
    question: '赛事方入驻需要准备哪些材料？',
    answer: '建议准备机构名称、联系人信息、营业执照号以及赛事相关资质材料图片，材料越完整审核越快。'
  },
  {
    id: 'privacy',
    category: 'account',
    question: '平台如何保护我的个人信息？',
    answer: '平台遵循最小必要原则收集与处理信息，敏感信息会按系统规范加密存储并限制访问。'
  }
]

export default {
  data() {
    return {
      keyword: '',
      currentCategory: 'all',
      openedFaqIds: ['enroll-refund'],
      faqCategories: [
        { label: '全部', value: 'all' },
        { label: '报名相关', value: 'enroll' },
        { label: '支付相关', value: 'pay' },
        { label: '证书相关', value: 'cert' },
        { label: '绑定相关', value: 'bind' },
        { label: '赛事方入驻', value: 'partner' },
        { label: '账号相关', value: 'account' }
      ]
    }
  },
  computed: {
    filteredFaqList() {
      const keyword = this.keyword.toLowerCase()

      return FAQ_LIST.filter((item) => {
        const categoryMatched = this.currentCategory === 'all' || item.category === this.currentCategory
        const keywordMatched = !keyword || item.question.toLowerCase().includes(keyword) || item.answer.toLowerCase().includes(keyword)
        return categoryMatched && keywordMatched
      })
    }
  },
  methods: {
    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }

      uni.switchTab({ url: '/pages/index/index' })
    },

    toggleFaq(id) {
      if (this.openedFaqIds.includes(id)) {
        this.openedFaqIds = this.openedFaqIds.filter(item => item !== id)
        return
      }

      this.openedFaqIds = this.openedFaqIds.concat(id)
    },

    copyContact(value, title) {
      uni.setClipboardData({
        data: value,
        success: () => {
          uni.showToast({
            title,
            icon: 'none'
          })
        }
      })
    },

    contactOnlineService() {
      uni.showToast({
        title: '在线客服入口待后端会话能力开放',
        icon: 'none'
      })
    },

    goNoticeTab() {
      uni.switchTab({ url: '/pages/notice/list' })
    },

    showDoc(title) {
      uni.showModal({
        title,
        content: `${title} 当前期仅提供入口占位，后续可接入正式 H5 文档或协议详情页。`,
        showCancel: false
      })
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';
.help-page {
  min-height: 100vh;
  padding-bottom: 40rpx;
  background: $jst-bg-page;
}

.help-page__nav {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 24rpx;
  background: $jst-bg-card;
  border-bottom: 2rpx solid $jst-border;
}

.help-page__back,
.help-page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.help-page__back {
  font-size: 36rpx;
  color: $jst-text-regular;
}

.help-page__nav-title {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  font-weight: 600;
  color: $jst-text-primary;
}

.help-page__search {
  display: flex;
  align-items: center;
  margin: 16rpx 24rpx 0;
  padding: 0 20rpx;
  height: 76rpx;
  border-radius: $jst-radius-round;
  background: $jst-bg-grey;
}

.help-page__search-icon {
  font-size: 26rpx;
  color: $jst-text-secondary;
}

.help-page__search-input {
  flex: 1;
  margin-left: 14rpx;
  font-size: 26rpx;
  color: $jst-text-primary;
}

.help-page__placeholder {
  color: $jst-text-secondary;
}

.help-page__hero {
  display: flex;
  align-items: center;
  gap: 18rpx;
  margin: 18rpx 24rpx 0;
  padding: 26rpx 24rpx;
  border-radius: 24rpx;
  background: linear-gradient(150deg, $jst-indigo 0%, $jst-indigo-light 100%);
  box-shadow: $jst-shadow-md;
}

.help-page__hero-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: rgba(255,255,255,0.18);
  font-size: 32rpx;
}

.help-page__hero-body {
  flex: 1;
}

.help-page__hero-title {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: $jst-bg-card;
}

.help-page__hero-subtitle {
  display: block;
  margin-top: 6rpx;
  font-size: 20rpx;
  color: rgba(255,255,255,0.78);
}

.help-page__hero-tag {
  display: inline-flex;
  align-items: center;
  margin-top: 10rpx;
  padding: 6rpx 14rpx;
  border-radius: $jst-radius-round;
  background: rgba(39, 174, 96, 0.24);
  font-size: 20rpx;
  font-weight: 600;
  color: $jst-bg-card;
}

.help-page__hero-arrow {
  font-size: 34rpx;
  color: rgba(255,255,255,0.7);
}

.help-page__contact-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin: 16rpx 24rpx 0;
}

.help-page__contact-card {
  width: calc(50% - 8rpx);
  padding: 22rpx 18rpx;
  border-radius: 24rpx;
  background: $jst-bg-card;
  box-shadow: 0 2rpx 8rpx rgba(20, 30, 60, 0.04);
  text-align: center;
}

.help-page__contact-icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 68rpx;
  height: 68rpx;
  margin: 0 auto;
  border-radius: 20rpx;
}

.help-page__contact-icon-wrap--phone {
  background: $jst-danger-light;
}

.help-page__contact-icon-wrap--wechat {
  background: $jst-info-light;
}

.help-page__contact-icon-wrap--mail {
  background: $jst-brand-light;
}

.help-page__contact-icon-wrap--notice {
  background: $jst-warning-light;
}

.help-page__contact-icon {
  display: block;
  font-size: 30rpx;
}

.help-page__contact-name {
  display: block;
  margin-top: 14rpx;
  font-size: 24rpx;
  font-weight: 600;
  color: $jst-text-primary;
}

.help-page__contact-value {
  display: block;
  margin-top: 8rpx;
  font-size: 20rpx;
  color: $jst-brand;
}

.help-page__contact-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 18rpx;
  color: $jst-text-secondary;
}

.help-page__section {
  margin: 16rpx 24rpx 0;
  padding: 24rpx;
  border-radius: 24rpx;
  background: $jst-bg-card;
  box-shadow: 0 2rpx 8rpx rgba(20, 30, 60, 0.04);
}

.help-page__section-head {
  display: flex;
  align-items: center;
  margin-bottom: 18rpx;
}

.help-page__section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $jst-text-primary;
}

.help-page__cats {
  white-space: nowrap;
}

.help-page__cats-inner {
  display: inline-flex;
  gap: 12rpx;
  min-width: 100%;
  padding-bottom: 6rpx;
}

.help-page__cat {
  flex-shrink: 0;
  padding: 10rpx 20rpx;
  border-radius: $jst-radius-round;
  background: $jst-bg-grey;
  font-size: 20rpx;
  color: $jst-text-regular;
}

.help-page__cat--active {
  background: $jst-brand-light;
  color: $jst-brand;
  font-weight: 600;
}

.help-page__faq-list {
  margin-top: 18rpx;
}

.help-page__faq-item {
  border-bottom: 2rpx solid $jst-border;
}

.help-page__faq-item:last-child {
  border-bottom: 0;
}

.help-page__faq-question {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding: 22rpx 0;
}

.help-page__faq-question-text {
  flex: 1;
  font-size: 24rpx;
  font-weight: 600;
  line-height: 1.5;
  color: $jst-text-primary;
}

.help-page__faq-icon {
  font-size: 24rpx;
  color: $jst-text-secondary;
}

.help-page__faq-answer {
  padding: 0 0 24rpx;
  font-size: 22rpx;
  line-height: 1.8;
  color: $jst-text-regular;
}

.help-page__promise-item {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
}

.help-page__promise-item + .help-page__promise-item {
  margin-top: 18rpx;
}

.help-page__promise-icon {
  font-size: 30rpx;
}

.help-page__promise-body {
  flex: 1;
}

.help-page__promise-title {
  display: block;
  font-size: 24rpx;
  font-weight: 600;
  color: $jst-text-primary;
}

.help-page__promise-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: $jst-text-secondary;
}

.help-page__doc-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 2rpx solid $jst-border;
}

.help-page__doc-item:last-child {
  border-bottom: 0;
}

.help-page__doc-name {
  font-size: 26rpx;
  color: $jst-text-primary;
}

.help-page__doc-arrow {
  font-size: 28rpx;
  color: $jst-text-secondary;
}

.help-page__footer {
  padding: 28rpx 24rpx 8rpx;
  text-align: center;
}

.help-page__footer-text {
  display: block;
  font-size: 24rpx;
  color: $jst-text-secondary;
}

.help-page__footer-button {
  width: 360rpx;
  height: 84rpx;
  margin-top: 18rpx;
  border-radius: $jst-radius-round;
  background: $jst-amber-gradient;
  font-size: 26rpx;
  font-weight: 600;
  color: $jst-bg-card;
}
</style>
