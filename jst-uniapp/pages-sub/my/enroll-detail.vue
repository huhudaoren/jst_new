<template>
  <view class="enroll-detail-page">
    <jst-loading :loading="pageLoading" text="报名详情加载中..." />
    <u-skeleton v-if="pageLoading" :loading="true" :rows="8" title :avatar="false" class="jst-page-skeleton" />

    <view class="enroll-detail-page__nav">
      <view class="enroll-detail-page__back" @tap="handleBack">返回</view>
      <text class="enroll-detail-page__nav-title">报名详情</text>
      <view class="enroll-detail-page__nav-placeholder"></view>
    </view>

    <template v-if="detail.enrollId">
      <view class="enroll-detail-page__status-card">
        <jst-status-badge :text="statusText" :tone="statusTone" />
        <text class="enroll-detail-page__status-title">{{ detail.contestName || '报名记录' }}</text>
        <text class="enroll-detail-page__status-desc">
          报名号：{{ detail.enrollNo || '--' }} · 提交时间：{{ formatDateTime(detail.submitTime || detail.updateTime) }}
        </text>
        <text v-if="detail.auditRemark" class="enroll-detail-page__status-remark">
          审核说明：{{ detail.auditRemark }}
        </text>
      </view>

      <view class="enroll-detail-page__card">
        <text class="enroll-detail-page__card-title">赛事信息</text>
        <view class="enroll-detail-page__info-row">
          <text class="enroll-detail-page__info-key">赛事名称</text>
          <text class="enroll-detail-page__info-value">{{ detail.contestName || '--' }}</text>
        </view>
        <view class="enroll-detail-page__info-row">
          <text class="enroll-detail-page__info-key">模板版本</text>
          <text class="enroll-detail-page__info-value">V{{ detail.templateVersion || '--' }}</text>
        </view>
      </view>

      <view class="enroll-detail-page__card">
        <text class="enroll-detail-page__card-title">参赛人信息</text>
        <view class="enroll-detail-page__info-row">
          <text class="enroll-detail-page__info-key">参赛人</text>
          <text class="enroll-detail-page__info-value">{{ detail.participantName || '--' }}</text>
        </view>
        <view class="enroll-detail-page__info-row">
          <text class="enroll-detail-page__info-key">监护人</text>
          <text class="enroll-detail-page__info-value">{{ detail.guardianName || '--' }}</text>
        </view>
        <view class="enroll-detail-page__info-row">
          <text class="enroll-detail-page__info-key">手机</text>
          <text class="enroll-detail-page__info-value">{{ detail.guardianMobileMasked || '--' }}</text>
        </view>
        <view class="enroll-detail-page__info-row">
          <text class="enroll-detail-page__info-key">学校</text>
          <text class="enroll-detail-page__info-value">{{ detail.school || '--' }}</text>
        </view>
      </view>

      <view class="enroll-detail-page__card">
        <text class="enroll-detail-page__card-title">表单回显</text>
        <jst-dynamic-form
          :schema="displaySchema"
          :value="resolvedFormValue"
          :readonly="true"
        />
      </view>

      <view v-if="attachments.length" class="enroll-detail-page__card">
        <text class="enroll-detail-page__card-title">附件列表</text>
        <view
          v-for="(item, index) in attachments"
          :key="index"
          class="enroll-detail-page__attachment"
        >
          <text class="enroll-detail-page__attachment-text">{{ item }}</text>
        </view>
      </view>

      <!-- 成绩与证书入口 -->
      <view v-if="detail.scorePublished" class="enroll-detail-page__card">
        <text class="enroll-detail-page__card-title">成绩与证书</text>
        <view class="enroll-detail-page__info-row" @tap="goScoreDetail">
          <text class="enroll-detail-page__info-key">查看成绩</text>
          <text class="enroll-detail-page__info-value">{{ detail.totalScore != null ? (detail.totalScore + ' 分') : '查看 ›' }}</text>
        </view>
        <view v-if="detail.hasCert" class="enroll-detail-page__info-row" @tap="goCertDetail">
          <text class="enroll-detail-page__info-key">查看证书</text>
          <text class="enroll-detail-page__info-value">{{ detail.awardLevel || '' }} ›</text>
        </view>
      </view>
    </template>

    <jst-empty
      v-else-if="!pageLoading"
      icon="📝"
      text="暂无报名详情"
    />

    <!-- POLISH-BATCH2 B: 优惠券选择 (仅已通过待支付时可见) -->
    <view v-if="actionButton && actionButton.action === 'pay'" class="enroll-detail-page__card">
      <text class="enroll-detail-page__card-title">优惠券</text>
      <view class="enroll-detail-page__info-row" @tap="goPickCoupon">
        <text class="enroll-detail-page__info-key">选择优惠券</text>
        <text class="enroll-detail-page__info-value">
          {{ selectedCouponId ? ('已选 #' + selectedCouponId + (couponNetPay != null ? (' · 实付 ¥' + couponNetPay) : '')) : '不使用 ›' }}
        </text>
      </view>
    </view>

    <view v-if="actionButton" class="enroll-detail-page__bottom">
      <u-button class="enroll-detail-page__bottom-btn" @click="handleAction">
        {{ actionButton.text }}
      </u-button>
    </view>
  </view>
</template>

<script>
import { createOrder } from '@/api/order'
import { getEnrollDetail, getEnrollTemplate } from '@/api/enroll'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstDynamicForm from '@/components/jst-dynamic-form/jst-dynamic-form.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'
import JstStatusBadge from '@/components/jst-status-badge/jst-status-badge.vue'

export default {
  components: {
    JstEmpty,
    JstDynamicForm,
    JstLoading,
    JstStatusBadge
  },
  data() {
    return {
      pageLoading: false,
      enrollId: '',
      detail: {},
      templateInfo: null,
      displayFormValue: {},
      attachments: [],
      // POLISH-BATCH2 B: 优惠券选择 (通过 coupon/select 页 storage 回传)
      selectedCouponId: null,
      couponNetPay: null
    }
  },
  computed: {
    displaySchema() {
      if (this.templateInfo && this.getSchemaFields(this.templateInfo).length) {
        return this.templateInfo
      }
      return this.buildFallbackSchema(this.displayFormValue, this.attachments)
    },
    resolvedFormValue() {
      if (this.templateInfo && this.getSchemaFields(this.templateInfo).length) {
        return this.displayFormValue
      }
      if (!this.attachments.length) {
        return this.displayFormValue
      }
      return Object.assign({}, this.displayFormValue, {
        __attachments__: this.attachments
      })
    },
    statusText() {
      const mapper = {
        pending: '待审核',
        approved: '已通过',
        supplement: '待补充',
        rejected: '已驳回',
        draft: '草稿'
      }
      return mapper[this.detail.auditStatus] || '处理中'
    },
    statusTone() {
      const mapper = {
        pending: 'accent',
        approved: 'success',
        supplement: 'warning',
        rejected: 'danger',
        draft: 'gray'
      }
      return mapper[this.detail.auditStatus] || 'primary'
    },
    actionButton() {
      if (this.detail.auditStatus === 'supplement') {
        return { text: '补充材料', action: 'supplement' }
      }
      if (this.detail.auditStatus === 'approved') {
        return { text: '去支付', action: 'pay' }
      }
      return null
    }
  },
  onLoad(query) {
    this.enrollId = query.id || ''
    this.fetchDetail()
  },
  onShow() {
    // POLISH-BATCH2 B: 从 coupon/select 页回传优惠券
    const picked = uni.getStorageSync('cs_selected_coupon_id')
    if (picked !== undefined && picked !== '') {
      this.selectedCouponId = picked || null
      const net = uni.getStorageSync('cs_selected_coupon_net')
      if (net != null && net !== '') this.couponNetPay = net
      uni.removeStorageSync('cs_selected_coupon_id')
      uni.removeStorageSync('cs_selected_coupon_net')
    }
  },
  methods: {
    // POLISH-BATCH2 B: 跳转选券页, 金额暂用 0 兜底 (enroll-detail 未暴露 price 字段, 后端可据 contestId 兜算)
    goPickCoupon() {
      const contestId = this.detail && this.detail.contestId
      uni.navigateTo({
        url: `/pages-sub/coupon/select?contestId=${contestId || ''}&orderAmount=0`
      })
    },
    async fetchDetail() {
      if (!this.enrollId) {
        return
      }

      this.pageLoading = true
      try {
        const data = await getEnrollDetail(this.enrollId, { silent: true })
        this.detail = data || {}
        const snapshot = this.normalizeSnapshot(data && data.formSnapshotJson)
        this.displayFormValue = snapshot.formData
        this.attachments = snapshot.attachments
        await this.fetchTemplate()
      } catch (error) {
        this.detail = {}
        this.displayFormValue = {}
        this.attachments = []
      } finally {
        this.pageLoading = false
      }
    },

    async fetchTemplate() {
      if (!this.detail.contestId) {
        this.templateInfo = null
        return
      }

      try {
        const template = await getEnrollTemplate(this.detail.contestId, { silent: true })
        this.templateInfo = template && template.schemaJson ? template.schemaJson : null
      } catch (error) {
        this.templateInfo = null
      }
    },

    normalizeSnapshot(raw) {
      if (raw && raw.formData) {
        return {
          formData: raw.formData || {},
          attachments: Array.isArray(raw.attachments) ? raw.attachments : []
        }
      }
      return {
        formData: raw || {},
        attachments: []
      }
    },

    getSchemaFields(schema) {
      if (Array.isArray(schema)) {
        return schema
      }
      return Array.isArray(schema && schema.fields) ? schema.fields : []
    },

    buildFallbackSchema(formData, attachments) {
      const fields = Object.keys(formData || {}).map((key) => ({
        key,
        label: key,
        type: Array.isArray(formData[key]) ? 'checkbox' : 'text',
        required: false
      }))

      if (attachments && attachments.length) {
        fields.push({
          key: '__attachments__',
          label: '附件',
          type: 'file',
          required: false
        })
      }

      return { fields }
    },

    async handleAction() {
      if (!this.actionButton) {
        return
      }

      if (this.actionButton.action === 'supplement') {
        uni.navigateTo({
          url: `/pages-sub/contest/enroll?contestId=${this.detail.contestId}&enrollId=${this.detail.enrollId}&mode=supplement`
        })
        return
      }

      uni.showLoading({ title: '创建订单中...' })
      try {
        const result = await createOrder({
          enrollId: this.detail.enrollId,
          pointsToUse: 0,
          payMethod: 'wechat',
          couponId: this.selectedCouponId || undefined
        })
        uni.hideLoading()
        uni.navigateTo({
          url: `/pages-sub/my/order-detail?orderId=${result.orderId}&enrollId=${this.detail.enrollId}`
        })
      } catch (error) {
        uni.hideLoading()
      }
    },

    // 跳转成绩详情
    goScoreDetail() {
      var id = this.detail.scoreId || this.detail.enrollId
      if (id) {
        uni.navigateTo({ url: '/pages-sub/my/score-detail?id=' + id })
      }
    },
    // 跳转证书详情
    goCertDetail() {
      if (this.detail.certId) {
        uni.navigateTo({ url: '/pages-sub/my/cert-detail?id=' + this.detail.certId })
      } else {
        uni.navigateTo({ url: '/pages-sub/my/cert' })
      }
    },

    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }
      uni.navigateTo({ url: '/pages-sub/my/enroll' })
    },

    formatDateTime(value) {
      if (!value) {
        return '--'
      }
      return `${value}`.slice(0, 16).replace('T', ' ')
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.enroll-detail-page {
  min-height: 100vh;
  padding-bottom: calc(132rpx + env(safe-area-inset-bottom));
  background: $jst-bg-page;
}

.jst-page-skeleton {
  margin: $jst-space-lg;
}

.enroll-detail-page__nav {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 24rpx;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.enroll-detail-page__back,
.enroll-detail-page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.enroll-detail-page__back {
  font-size: 34rpx;
  color: $jst-text-secondary;
}

.enroll-detail-page__nav-title {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  font-weight: 700;
  color: $jst-text-primary;
}

.enroll-detail-page__status-card,
.enroll-detail-page__card {
  margin: 20rpx 24rpx 0;
  padding: 28rpx 24rpx;
  border-radius: 28rpx;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
}

.enroll-detail-page__status-card {
  background: linear-gradient(135deg, $jst-brand-dark 0%, $jst-brand 100%);
}

.enroll-detail-page__status-title {
  display: block;
  margin-top: 18rpx;
  font-size: 34rpx;
  font-weight: 800;
  line-height: 1.45;
  color: $jst-text-inverse;
}

.enroll-detail-page__status-desc,
.enroll-detail-page__status-remark {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: rgba($jst-text-inverse, 0.78);
}

.enroll-detail-page__card-title {
  display: block;
  margin-bottom: 18rpx;
  font-size: 30rpx;
  font-weight: 700;
  color: $jst-text-primary;
}

.enroll-detail-page__info-row {
  display: flex;
  justify-content: space-between;
  gap: 24rpx;
  padding: 18rpx 0;
  border-top: 2rpx solid $jst-border;
}

.enroll-detail-page__info-row:first-of-type {
  border-top: none;
  padding-top: 0;
}

.enroll-detail-page__info-key,
.enroll-detail-page__info-value {
  font-size: 24rpx;
  line-height: 1.6;
}

.enroll-detail-page__info-key {
  color: $jst-text-secondary;
}

.enroll-detail-page__info-value {
  flex: 1;
  text-align: right;
  color: $jst-text-primary;
}

.enroll-detail-page__attachment {
  min-height: 84rpx;
  display: flex;
  align-items: center;
  margin-top: 12rpx;
  padding: 0 22rpx;
  border-radius: 20rpx;
  background: $jst-bg-grey;
}

.enroll-detail-page__attachment-text {
  font-size: 24rpx;
  line-height: 1.6;
  color: $jst-text-secondary;
  word-break: break-all;
}

.enroll-detail-page__bottom {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx 24rpx calc(20rpx + env(safe-area-inset-bottom));
  background: rgba($jst-bg-card, 0.98);
  box-shadow: $jst-shadow-md;
}

.enroll-detail-page__bottom-btn {
  width: 100%;
  height: 88rpx;
  border-radius: 28rpx;
  background: $jst-brand-gradient;
  color: $jst-text-inverse;
  font-size: 28rpx;
  font-weight: 700;
}

::v-deep .enroll-detail-page__bottom-btn.u-button {
  border: none;
}
</style>
