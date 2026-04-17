<template>
  <view class="enroll-page">
    <jst-loading :loading="pageLoading" text="报名信息加载中..." />
    <u-skeleton v-if="pageLoading" :loading="true" :rows="8" title :avatar="false" class="jst-page-skeleton" />

    <view class="enroll-page__nav">
      <view class="enroll-page__back" @tap="handleBack"><</view>
      <text class="enroll-page__nav-title">{{ pageTitle }}</text>
      <view class="enroll-page__nav-placeholder"></view>
    </view>

    <view class="enroll-page__hero">
      <u-tag text="赛事报名" type="primary" plain shape="circle" size="mini" />
      <text class="enroll-page__hero-title">{{ contestInfo.contestName || '正在准备报名信息' }}</text>
      <text class="enroll-page__hero-desc">先填写报名资料，本期提交后仅进入审核，不直接创建订单。</text>
    </view>

    <view class="enroll-page__content">
      <view class="enroll-page__card enroll-page__card--summary">
        <view class="enroll-page__card-header">
          <text class="enroll-page__card-title">赛事信息</text>
          <text v-if="contestInfo.statusText" class="enroll-page__summary-status">{{ contestInfo.statusText }}</text>
        </view>

        <text class="enroll-page__summary-name">{{ contestInfo.contestName || '暂无赛事信息' }}</text>
        <view class="enroll-page__summary-meta">
          <text class="enroll-page__summary-meta-item">报名时间：{{ contestInfo.enrollPeriod || '--' }}</text>
          <text class="enroll-page__summary-meta-item">比赛时间：{{ contestInfo.eventPeriod || '--' }}</text>
          <text class="enroll-page__summary-meta-item">报名费用：{{ contestInfo.priceText }}</text>
        </view>
      </view>

      <view class="enroll-page__card">
        <view class="enroll-page__card-header">
          <text class="enroll-page__card-title">选择参赛人</text>
          <text v-if="selectedParticipant" class="enroll-page__card-subtitle">{{ selectedParticipant.name || selectedParticipant.participantName }}</text>
        </view>

        <view v-if="participants.length" class="enroll-page__participant-list">
          <view
            v-for="item in participants"
            :key="item.participantId"
            class="enroll-page__participant"
            :class="{ 'enroll-page__participant--active': selectedParticipantId === `${item.participantId}` }"
            @tap="selectParticipant(item)"
          >
            <view class="enroll-page__participant-radio"></view>
            <view class="enroll-page__participant-main">
              <text class="enroll-page__participant-name">{{ item.name || item.participantName || '未命名档案' }}</text>
              <text class="enroll-page__participant-desc">
                {{ item.school || '学校未填写' }} · {{ item.className || '班级未填写' }} · {{ item.age || '年龄未填写' }}
              </text>
            </view>
          </view>
        </view>

        <view v-else class="enroll-page__participant-empty">
          <text class="enroll-page__participant-empty-text">还没有可用的参赛档案，可自行新建或认领老师/赛事方分享的档案。</text>
          <u-button class="enroll-page__participant-empty-btn" @click="navigateParticipant">去创建档案</u-button>
        </view>
      </view>

      <view class="enroll-page__card enroll-page__card--tip">
        <text class="enroll-page__tip-title">填写提醒</text>
        <text class="enroll-page__tip-text">带 * 为必填项，请认真填写并上传所需材料。</text>
      </view>

      <view class="enroll-page__card">
        <view class="enroll-page__card-header">
          <text class="enroll-page__card-title">报名表单</text>
          <text v-if="templateInfo.templateVersion" class="enroll-page__card-subtitle">模板 V{{ templateInfo.templateVersion }}</text>
        </view>

        <jst-dynamic-form
          v-if="schemaReady"
          ref="dynamicForm"
          :schema="templateInfo.schemaJson"
          :value="formState"
          :show-validation="showValidation"
          @input="handleFormInput"
          @validate="handleValidate"
        />

        <jst-empty
          v-else
          icon="📝"
          text="暂无报名表单"
        />
      </view>
    </view>

    <view class="enroll-page__bottom">
      <u-button
        class="enroll-page__bottom-btn enroll-page__bottom-btn--ghost"
        :loading="draftLoading"
        @click="handleDraft"
      >
        暂存草稿
      </u-button>
      <u-button
        class="enroll-page__bottom-btn enroll-page__bottom-btn--primary"
        :loading="submitLoading"
        @click="handleSubmit"
      >
        {{ submitButtonText }}
      </u-button>
    </view>
  </view>
</template>

<script>
import { getContestDetail } from '@/api/contest'
import {
  getEnrollDetail,
  getEnrollTemplate,
  saveEnrollDraft,
  submitEnroll,
  supplementEnroll
} from '@/api/enroll'
import { myParticipants } from '@/api/participant'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstDynamicForm from '@/components/jst-dynamic-form/jst-dynamic-form.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'

const DRAFT_STORAGE_KEY = 'jst-enroll-draft'

export default {
  components: {
    JstEmpty,
    JstDynamicForm,
    JstLoading
  },
  data() {
    return {
      pageLoading: false,
      draftLoading: false,
      submitLoading: false,
      contestId: '',
      enrollId: '',
      mode: '',
      auditStatus: '',
      contestInfo: {
        contestName: '',
        enrollPeriod: '--',
        eventPeriod: '--',
        priceText: '待定',
        statusText: ''
      },
      templateInfo: {
        templateId: '',
        templateVersion: '',
        schemaJson: null
      },
      participants: [],
      selectedParticipantId: '',
      formState: {},
      formValid: false,
      showValidation: false,
      cachedAttachments: []
    }
  },
  computed: {
    pageTitle() {
      return this.mode === 'supplement' ? '补充材料' : '报名'
    },
    submitButtonText() {
      return this.mode === 'supplement' ? '提交补充' : '立即提交'
    },
    schemaReady() {
      return !!(this.templateInfo.schemaJson && this.getSchemaFields(this.templateInfo.schemaJson).length)
    },
    selectedParticipant() {
      return this.participants.find((item) => `${item.participantId}` === this.selectedParticipantId) || null
    }
  },
  onLoad(query) {
    this.contestId = query.contestId || query.id || ''
    this.enrollId = query.enrollId || ''
    this.mode = query.mode || ''
    this.initializePage()
  },
  methods: {
    async initializePage() {
      if (!this.contestId && !this.enrollId) {
        return
      }

      this.pageLoading = true
      let currentDetail = null

      try {
        if (this.enrollId) {
          currentDetail = await getEnrollDetail(this.enrollId, { silent: true })
          this.contestId = this.contestId || `${currentDetail.contestId || ''}`
        }

        await Promise.allSettled([
          this.fetchContestDetail(),
          this.fetchTemplate(),
          this.fetchParticipants()
        ])

        if (currentDetail) {
          this.applyEnrollDetail(currentDetail)
        }

        this.restoreDraftCache()

        if (!this.selectedParticipantId && this.participants.length) {
          this.selectedParticipantId = `${this.participants[0].participantId}`
        }
      } finally {
        this.pageLoading = false
      }
    },

    async fetchContestDetail() {
      if (!this.contestId) {
        return
      }

      try {
        const detail = await getContestDetail(this.contestId, { silent: true })
        this.contestInfo = this.normalizeContestInfo(detail || {})
      } catch (error) {
        uni.showToast({ title: '加载失败，请重试', icon: 'none' })
        this.contestInfo = this.normalizeContestInfo({})
      }
    },

    async fetchTemplate() {
      if (!this.contestId) {
        return
      }

      try {
        const data = await getEnrollTemplate(this.contestId, { silent: true })
        this.templateInfo = {
          templateId: data && data.templateId ? data.templateId : '',
          templateVersion: data && data.templateVersion ? data.templateVersion : '',
          schemaJson: data && data.schemaJson ? data.schemaJson : { fields: [] }
        }
      } catch (error) {
        uni.showToast({ title: '加载失败，请重试', icon: 'none' })
        this.templateInfo = {
          templateId: '',
          templateVersion: '',
          schemaJson: { fields: [] }
        }
      }
    },

    async fetchParticipants() {
      try {
        const list = await myParticipants()
        this.participants = Array.isArray(list) ? list : []
      } catch (error) {
        uni.showToast({ title: '加载失败，请重试', icon: 'none' })
        this.participants = []
      }
    },

    applyEnrollDetail(detail) {
      const snapshot = this.normalizeSnapshot(detail && detail.formSnapshotJson)
      this.enrollId = `${detail && detail.enrollId ? detail.enrollId : this.enrollId}`
      this.selectedParticipantId = `${detail && detail.participantId ? detail.participantId : this.selectedParticipantId}`
      this.formState = Object.assign({}, snapshot.formData || {})
      this.cachedAttachments = snapshot.attachments
      this.auditStatus = detail && detail.auditStatus ? detail.auditStatus : ''
    },

    normalizeContestInfo(detail) {
      return {
        contestName: detail.contestName || '',
        enrollPeriod: this.formatPeriod(detail.enrollStartTime, detail.enrollEndTime),
        eventPeriod: this.formatPeriod(detail.eventStartTime, detail.eventEndTime),
        priceText: this.formatPrice(detail.price),
        statusText: this.getContestStatusText(detail.status, detail.enrollOpen)
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

    handleFormInput(key, value) {
      this.formState = Object.assign({}, this.formState, { [key]: value })
    },

    handleValidate(isValid) {
      this.formValid = isValid
    },

    selectParticipant(item) {
      this.selectedParticipantId = `${item.participantId}`
    },

    async handleDraft() {
      if (this.draftLoading || this.submitLoading) {
        return
      }

      if (!this.selectedParticipantId) {
        uni.showToast({
          title: '请先选择参赛人',
          icon: 'none'
        })
        return
      }

      this.draftLoading = true
      try {
        const response = await saveEnrollDraft(this.buildBasePayload(), { silent: true })
        this.enrollId = `${response && response.enrollId ? response.enrollId : this.enrollId}`
        this.writeDraftCache()
        uni.showToast({
          title: '草稿已保存',
          icon: 'success'
        })
      } catch (error) {
        uni.showToast({
          title: '草稿保存失败',
          icon: 'none'
        })
      } finally {
        this.draftLoading = false
      }
    },

    async handleSubmit() {
      if (this.submitLoading || this.draftLoading) {
        return
      }

      if (!this.selectedParticipantId) {
        uni.showToast({
          title: '请先选择参赛人',
          icon: 'none'
        })
        return
      }

      this.showValidation = true
      const errors = this.$refs.dynamicForm ? this.$refs.dynamicForm.validateForm() : []
      if (errors.length) {
        uni.showToast({
          title: errors[0].message,
          icon: 'none'
        })
        return
      }

      this.submitLoading = true
      try {
        let response = null
        if (this.mode === 'supplement' && this.enrollId) {
          await supplementEnroll(
            this.enrollId,
            {
              formData: this.formState,
              attachments: this.collectAttachments(),
              supplementRemark: ''
            },
            { silent: true }
          )
          response = { enrollId: this.enrollId }
        } else {
          response = await submitEnroll(this.buildBasePayload(), { silent: true })
          this.enrollId = `${response && response.enrollId ? response.enrollId : this.enrollId}`
        }

        this.clearDraftCache()
        uni.showToast({
          title: this.mode === 'supplement' ? '补充已提交' : '报名已提交',
          icon: 'success'
        })

        setTimeout(() => {
          uni.redirectTo({
            url: `/pages-sub/my/enroll-detail?id=${response && response.enrollId ? response.enrollId : this.enrollId}`
          })
        }, 500)
      } catch (error) {
        uni.showToast({
          title: this.mode === 'supplement' ? '补充提交失败' : '报名提交失败',
          icon: 'none'
        })
      } finally {
        this.submitLoading = false
      }
    },

    buildBasePayload() {
      return {
        enrollId: this.enrollId ? Number(this.enrollId) : undefined,
        contestId: Number(this.contestId),
        participantId: Number(this.selectedParticipantId),
        formData: this.formState,
        attachments: this.collectAttachments()
      }
    },

    collectAttachments() {
      const result = []
      const visit = (fields) => {
        fields.forEach((field) => {
          if (field.type === 'group' || field.type === 'conditional') {
            visit(this.getSchemaFields({ fields: field.fields }))
            return
          }
          if (!['image', 'audio', 'video', 'file'].includes(field.type)) {
            return
          }
          const key = field.key || field.name || field.code || field.id
          const current = this.formState[key]
          if (Array.isArray(current)) {
            current.filter(Boolean).forEach((item) => result.push(item))
          } else if (current) {
            result.push(current)
          }
        })
      }

      visit(this.getSchemaFields(this.templateInfo.schemaJson))

      if (!result.length && Array.isArray(this.cachedAttachments)) {
        this.cachedAttachments.forEach((item) => item && result.push(item))
      }

      return Array.from(new Set(result))
    },

    getDraftStorageKey() {
      return `${DRAFT_STORAGE_KEY}-${this.contestId || 'unknown'}`
    },

    restoreDraftCache() {
      const cache = uni.getStorageSync(this.getDraftStorageKey())
      if (!cache || typeof cache !== 'object') {
        return
      }

      if (!this.enrollId && cache.enrollId) {
        this.enrollId = `${cache.enrollId}`
      }
      if (cache.participantId) {
        this.selectedParticipantId = `${cache.participantId}`
      }
      if (cache.formState && typeof cache.formState === 'object') {
        this.formState = Object.assign({}, this.formState, cache.formState)
      }
      if (Array.isArray(cache.attachments) && cache.attachments.length) {
        this.cachedAttachments = cache.attachments
      }
    },

    writeDraftCache() {
      uni.setStorageSync(this.getDraftStorageKey(), {
        enrollId: this.enrollId,
        participantId: this.selectedParticipantId,
        formState: this.formState,
        attachments: this.collectAttachments()
      })
    },

    clearDraftCache() {
      uni.removeStorageSync(this.getDraftStorageKey())
    },

    navigateParticipant() {
      uni.navigateTo({ url: '/pages-sub/my/participant' })
    },

    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }
      uni.switchTab({ url: '/pages/contest/list' })
    },

    formatPeriod(start, end) {
      if (!start && !end) {
        return '--'
      }
      return `${this.formatDateTime(start)} 至 ${this.formatDateTime(end)}`
    },

    formatDateTime(value) {
      if (!value) {
        return '--'
      }
      return `${value}`.slice(0, 16).replace('T', ' ')
    },

    formatPrice(value) {
      const amount = Number(value || 0)
      return amount > 0 ? `¥${amount.toFixed(2)}` : '免费'
    },

    getContestStatusText(status, enrollOpen) {
      if (enrollOpen === true) {
        return '报名中'
      }
      const mapper = {
        not_started: '未开始',
        enrolling: '报名中',
        closed: '已截止',
        scoring: '评审中',
        published: '已公布',
        ended: '已结束'
      }
      return mapper[status] || '进行中'
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.enroll-page {
  min-height: 100vh;
  padding-bottom: calc(164rpx + env(safe-area-inset-bottom));
  background: $jst-bg-subtle;
}

.jst-page-skeleton {
  margin: $jst-space-lg;
}

.enroll-page__nav {
  position: sticky;
  top: 0;
  z-index: 20;
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 24rpx;
  background: $jst-brand-dark;
}

.enroll-page__back,
.enroll-page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.enroll-page__back {
  font-size: 34rpx;
  color: rgba($jst-text-inverse, 0.96);
}

.enroll-page__nav-title {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  font-weight: 700;
  color: $jst-text-inverse;
}

.enroll-page__hero {
  padding: 28rpx 24rpx 180rpx;
  background: $jst-hero-gradient;
}

.enroll-page__hero-title {
  display: block;
  margin-top: 18rpx;
  font-size: 40rpx;
  font-weight: 800;
  line-height: 1.4;
  color: $jst-text-inverse;
}

.enroll-page__hero-desc {
  display: block;
  margin-top: 14rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: rgba($jst-text-inverse, 0.78);
}

.enroll-page__content {
  margin-top: -140rpx;
  padding: 0 24rpx;
}

.enroll-page__card {
  margin-bottom: $jst-space-lg;
  padding: $jst-space-xl $jst-space-lg;
  border-radius: $jst-radius-card;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-ring, $jst-shadow-ambient;
}

.enroll-page__card--summary {
  position: relative;
  overflow: hidden;
}

.enroll-page__card--summary::after {
  content: '';
  position: absolute;
  top: -40rpx;
  right: -28rpx;
  width: 180rpx;
  height: 180rpx;
  border-radius: 50%;
  background: rgba($jst-brand, 0.08);
}

.enroll-page__card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.enroll-page__card-title {
  font-size: 30rpx;
  font-weight: 700;
  color: $jst-text-primary;
}

.enroll-page__card-subtitle,
.enroll-page__summary-status {
  font-size: 22rpx;
  color: $jst-brand;
}

.enroll-page__summary-name {
  position: relative;
  z-index: 1;
  display: block;
  margin-top: 18rpx;
  font-size: 34rpx;
  font-weight: 800;
  line-height: 1.45;
  color: $jst-text-primary;
}

.enroll-page__summary-meta {
  position: relative;
  z-index: 1;
  margin-top: 18rpx;
}

.enroll-page__summary-meta-item {
  display: block;
  font-size: 24rpx;
  line-height: 1.8;
  color: $jst-text-secondary;
}

.enroll-page__participant-list {
  margin-top: 18rpx;
}

.enroll-page__participant {
  display: flex;
  align-items: center;
  gap: 18rpx;
  min-height: 108rpx;
  margin-bottom: 14rpx;
  padding: 0 22rpx;
  border: 2rpx solid $jst-border;
  border-radius: 24rpx;
  background: $jst-bg-card;
}

.enroll-page__participant:last-child {
  margin-bottom: 0;
}

.enroll-page__participant--active {
  border-color: $jst-brand;
  background: $jst-brand-light;
}

.enroll-page__participant-radio {
  width: 32rpx;
  height: 32rpx;
  border: 4rpx solid $jst-border;
  border-radius: 50%;
}

.enroll-page__participant--active .enroll-page__participant-radio {
  border-color: $jst-brand;
  background: radial-gradient(circle, $jst-brand 0, $jst-brand 44%, transparent 46%);
}

.enroll-page__participant-main {
  flex: 1;
  min-width: 0;
}

.enroll-page__participant-name {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
  color: $jst-text-primary;
}

.enroll-page__participant-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: $jst-text-secondary;
}

.enroll-page__participant-empty {
  margin-top: 18rpx;
  padding: 24rpx;
  border-radius: 24rpx;
  background: $jst-bg-grey;
}

.enroll-page__participant-empty-text,
.enroll-page__tip-text {
  display: block;
  font-size: 24rpx;
  line-height: 1.7;
  color: $jst-text-secondary;
}

.enroll-page__participant-empty-btn {
  margin-top: 18rpx;
  border-radius: 999rpx;
  background: $jst-brand;
  color: $jst-text-inverse;
  font-size: 26rpx;
  font-weight: 700;
}

.enroll-page__tip-title {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
  color: $jst-text-primary;
}

.enroll-page__bottom {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 30;
  display: flex;
  gap: $jst-space-md;
  padding: $jst-space-lg $jst-page-padding calc(#{$jst-space-lg} + env(safe-area-inset-bottom));
  background: $jst-glass-bg;
  backdrop-filter: blur($jst-glass-blur);
  -webkit-backdrop-filter: blur($jst-glass-blur);
  border-top: 1rpx solid $jst-glass-border;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.enroll-page__bottom-btn {
  flex: 1;
  height: 92rpx;
  border-radius: 32rpx;
  font-size: 28rpx;
  font-weight: 700;
}

.enroll-page__bottom-btn--ghost {
  border: 2rpx solid $jst-border;
  background: $jst-bg-card;
  color: $jst-brand;
}

.enroll-page__bottom-btn--primary {
  background: $jst-brand-gradient;
  color: $jst-text-inverse;
}

::v-deep .enroll-page__bottom-btn.u-button,
::v-deep .enroll-page__participant-empty-btn.u-button {
  min-height: 92rpx;
  border: none;
}
</style>
