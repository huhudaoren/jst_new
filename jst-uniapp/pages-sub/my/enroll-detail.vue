<template>
  <view class="enroll-detail-page">
    <jst-loading :loading="pageLoading" text="报名详情加载中..." />

    <view class="enroll-detail-page__nav">
      <view class="enroll-detail-page__back" @tap="handleBack"><</view>
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
        <jst-form-render
          :schema="displaySchema"
          :value="resolvedFormValue"
          readonly
        />
      </view>

      <view v-if="attachments.length" class="enroll-detail-page__card">
        <text class="enroll-detail-page__card-title">附件列表</text>
        <view
          v-for="(item, index) in attachments"
          :key="`${item}-${index}`"
          class="enroll-detail-page__attachment"
        >
          <text class="enroll-detail-page__attachment-text">{{ item }}</text>
        </view>
      </view>
    </template>

    <jst-empty
      v-else-if="!pageLoading"
      icon="📝"
      text="报名详情暂未返回。"
    />

    <view v-if="actionButton" class="enroll-detail-page__bottom">
      <button class="enroll-detail-page__bottom-btn" @tap="handleAction">
        {{ actionButton.text }}
      </button>
    </view>
  </view>
</template>

<script>
import { createOrder } from '@/api/order'
import { getEnrollDetail, getEnrollTemplate } from '@/api/enroll'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstFormRender from '@/components/jst-form-render/jst-form-render.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'
import JstStatusBadge from '@/components/jst-status-badge/jst-status-badge.vue'

export default {
  components: {
    JstEmpty,
    JstFormRender,
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
      attachments: []
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
  methods: {
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
          payMethod: 'wechat'
        })
        uni.hideLoading()
        uni.navigateTo({
          url: `/pages-sub/my/order-detail?orderId=${result.orderId}&enrollId=${this.detail.enrollId}`
        })
      } catch (error) {
        uni.hideLoading()
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
.enroll-detail-page {
  min-height: 100vh;
  padding-bottom: calc(132rpx + env(safe-area-inset-bottom));
  background: #f4f7fc;
}

.enroll-detail-page__nav {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 24rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 22rpx rgba(14, 58, 113, 0.05);
}

.enroll-detail-page__back,
.enroll-detail-page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.enroll-detail-page__back {
  font-size: 34rpx;
  color: #66768f;
}

.enroll-detail-page__nav-title {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  font-weight: 700;
  color: #1f2937;
}

.enroll-detail-page__status-card,
.enroll-detail-page__card {
  margin: 20rpx 24rpx 0;
  padding: 28rpx 24rpx;
  border-radius: 28rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 28rpx rgba(14, 58, 113, 0.06);
}

.enroll-detail-page__status-card {
  background: linear-gradient(135deg, #0c3d6b 0%, #2f7de1 100%);
}

.enroll-detail-page__status-title {
  display: block;
  margin-top: 18rpx;
  font-size: 34rpx;
  font-weight: 800;
  line-height: 1.45;
  color: #ffffff;
}

.enroll-detail-page__status-desc,
.enroll-detail-page__status-remark {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.78);
}

.enroll-detail-page__card-title {
  display: block;
  margin-bottom: 18rpx;
  font-size: 30rpx;
  font-weight: 700;
  color: #1f2937;
}

.enroll-detail-page__info-row {
  display: flex;
  justify-content: space-between;
  gap: 24rpx;
  padding: 18rpx 0;
  border-top: 2rpx solid #edf1f7;
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
  color: #7a869d;
}

.enroll-detail-page__info-value {
  flex: 1;
  text-align: right;
  color: #1f2937;
}

.enroll-detail-page__attachment {
  min-height: 84rpx;
  display: flex;
  align-items: center;
  margin-top: 12rpx;
  padding: 0 22rpx;
  border-radius: 20rpx;
  background: #f6f9ff;
}

.enroll-detail-page__attachment-text {
  font-size: 24rpx;
  line-height: 1.6;
  color: #5c6b84;
  word-break: break-all;
}

.enroll-detail-page__bottom {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx 24rpx calc(20rpx + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 -12rpx 28rpx rgba(14, 58, 113, 0.08);
}

.enroll-detail-page__bottom-btn {
  width: 100%;
  height: 88rpx;
  border-radius: 28rpx;
  background: linear-gradient(135deg, #ff6a3d 0%, #ff8a3d 100%);
  color: #ffffff;
  font-size: 28rpx;
  font-weight: 700;
}
</style>
