<!-- 中文注释: 渠道认证 · 状态查询
     调用接口: GET /jst/wx/channel/auth/my
               POST /jst/wx/channel/auth/cancel/{id} -->
<template>
  <view class="as-page" :style="{ paddingTop: navPaddingTop }">
    <view v-if="apply" class="as-hero" :class="'as-hero--' + apply.applyStatus">
      <text class="as-hero__icon">{{ statusIcon }}</text>
      <text class="as-hero__title">{{ statusTitle }}</text>
      <text v-if="apply.applyStatus === 'rejected'" class="as-hero__reason">驳回原因：{{ apply.auditRemark || '未说明' }}</text>
      <text v-if="isLocked" class="as-hero__reason">该申请已被锁定，请联系客服处理</text>
    </view>

    <!-- 中文注释: 驳回原因卡（红色边框）— 仅 rejected 状态展示 -->
    <view v-if="apply && apply.applyStatus === 'rejected'" class="as-reject-card">
      <view class="as-reject-card__head">
        <text class="as-reject-card__icon">❌</text>
        <text class="as-reject-card__title">驳回原因</text>
      </view>
      <text class="as-reject-card__reason">{{ rejectReasonText }}</text>
      <view class="as-reject-card__meta">
        <view v-if="apply.auditUserName" class="as-reject-card__meta-row">
          <text class="as-reject-card__meta-key">审核人</text>
          <text class="as-reject-card__meta-value">{{ apply.auditUserName }}</text>
        </view>
        <view v-if="apply.auditTime" class="as-reject-card__meta-row">
          <text class="as-reject-card__meta-key">审核时间</text>
          <text class="as-reject-card__meta-value">{{ formatTime(apply.auditTime) }}</text>
        </view>
      </view>
    </view>

    <view v-if="apply" class="as-section">
      <view class="as-section__head">
        <text class="as-section__title">申请信息</text>
        <u-tag
          :text="statusTitle"
          :type="!apply ? 'info' : (apply.applyStatus === 'pending' ? 'warning' : (apply.applyStatus === 'approved' ? 'success' : ((apply.applyStatus === 'rejected' || apply.applyStatus === 'locked_for_manual') ? 'error' : 'info')))"
          size="mini"
          shape="circle"
        ></u-tag>
      </view>
      <u-cell-group :border="false">
        <u-cell title="申请编号" :value="apply.applyNo || '--'" :border="false"></u-cell>
        <u-cell title="认证类型" :value="typeLabel" :border="false"></u-cell>
        <u-cell title="申请名称" :value="apply.applyName || '--'" :border="false"></u-cell>
        <u-cell title="提交时间" :value="formatTime(apply.submitTime)" :border="false"></u-cell>
        <u-cell title="审核时间" :value="formatTime(apply.auditTime)" :border="false"></u-cell>
        <u-cell title="驳回次数" :value="(apply.rejectCount || 0) + ' / 3'" :border="false"></u-cell>
      </u-cell-group>
    </view>

    <view v-if="apply" class="as-actions">
      <u-button v-if="apply.applyStatus === 'pending'" class="as-actions__btn" type="primary" plain @click="onCancel">撤回申请</u-button>
      <!-- 中文注释: 驳回态重新编辑 — 红主色按钮，跳 edit 模式 -->
      <u-button v-if="apply.applyStatus === 'rejected' && !isLocked" class="as-actions__btn as-actions__btn--resubmit" type="error" @click="onResubmit">🔄 重新编辑提交</u-button>
      <u-button v-if="isLocked" class="as-actions__btn" type="info" :disabled="true">请联系客服</u-button>
      <u-button v-if="apply.applyStatus === 'approved'" class="as-actions__btn" type="success" @click="goChannelHome">进入渠道工作台</u-button>
    </view>

    <u-empty v-if="!apply && !loading" mode="data" text="暂无认证申请记录" class="as-empty"></u-empty>

    <!-- 中文注释: 认证通过庆祝 ribbon，localStorage 防重复 -->
    <jst-celebrate
      :visible.sync="celebrateShow"
      preset="ribbon"
      title="认证通过"
      subtitle="欢迎加入竞赛通渠道方"
      :duration="3000"
    />
  </view>
</template>

<script>
import { getMyChannelApply, cancelChannelApply } from '@/api/channel'
import JstCelebrate from '@/components/jst-celebrate/jst-celebrate.vue'
import { useUserStore } from '@/store/user'

const TYPE_LABEL = { teacher: '老师', organization: '机构', individual: '个人' }
const STATUS_ICON = { pending: '⏳', approved: '✅', rejected: '❌', locked_for_manual: '🔒', cancelled: '🚫' }
const STATUS_TITLE = { pending: '审核中', approved: '认证通过', rejected: '认证被驳回', locked_for_manual: '已锁定', cancelled: '已撤回' }

export default {
  components: { JstCelebrate },
  data() { return { apply: null, loading: false, skeletonShow: true /* [visual-effect] */, celebrateShow: false } },
  computed: {
    typeLabel() { return (this.apply && TYPE_LABEL[this.apply.channelType]) || '--' },
    statusIcon() { return (this.apply && STATUS_ICON[this.apply.applyStatus]) || '❓' },
    statusTitle() { return (this.apply && STATUS_TITLE[this.apply.applyStatus]) || '未知状态' },
    isLocked() {
      if (!this.apply) return false
      return this.apply.lockedForManual === 1 || this.apply.applyStatus === 'locked_for_manual' || (this.apply.rejectCount >= 3 && this.apply.applyStatus === 'rejected')
    },
    // 中文注释: 驳回原因优先取 rejectReason（后端新字段），其次 auditRemark，否则通用兜底
    rejectReasonText() {
      if (!this.apply) return ''
      return this.apply.rejectReason || this.apply.auditRemark || '审核未通过，请重新提交'
    }
  },
  onShow() { this.load() },
  onPullDownRefresh() { this.load().finally(() => uni.stopPullDownRefresh()) },
  methods: {
    async load() {
      this.loading = true
      try { this.apply = (await getMyChannelApply()) || null } catch (e) { this.apply = null }
      finally { this.loading = false }
      this.checkApproved()
    },
    // 中文注释: 认证通过首次访问弹 ribbon，localStorage 防重复
    checkApproved() {
      try {
        if (!this.apply || this.apply.applyStatus !== 'approved') return
        const store = useUserStore()
        const uid = (store.userInfo && store.userInfo.userId) || 'anon'
        const key = 'jst-celebrate-last-ribbon-' + uid + '-' + (this.apply.applyId || '0')
        if (uni.getStorageSync(key)) return
        uni.setStorageSync(key, '1')
        this.celebrateShow = true
      } catch (e) {}
    },
    onCancel() {
      uni.showModal({
        title: '撤回申请', content: '确认撤回当前认证申请？',
        success: async (res) => {
          if (!res.confirm || !this.apply) return
          try { await cancelChannelApply(this.apply.applyId); uni.showToast({ title: '已撤回', icon: 'success' }); this.load() } catch (e) {}
        }
      })
    },
    onResubmit() {
      if (!this.apply) return
      // 中文注释: 驳回后重新编辑 — mode=edit + rejectedId 让表单页加载上次提交数据回填
      uni.navigateTo({ url: `/pages-sub/channel/apply-form?channelType=${this.apply.channelType}&resubmitId=${this.apply.applyId}&mode=edit&rejectedId=${this.apply.applyId}` })
    },
    goChannelHome() { uni.navigateTo({ url: '/pages-sub/channel/home' }) },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 16) }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.as-page { min-height: 100vh; background: $jst-bg-page; padding-bottom: $jst-space-xxl; }
.as-hero { padding: 96rpx $jst-space-xl 64rpx; text-align: center; color: $jst-text-inverse; border-bottom-left-radius: 40rpx; border-bottom-right-radius: 40rpx; }
.as-hero--pending { background: linear-gradient(135deg, $jst-warning, darken($jst-warning, 5%)); }
.as-hero--approved { background: linear-gradient(135deg, darken($jst-success, 15%), $jst-success); }
.as-hero--rejected, .as-hero--locked_for_manual { background: linear-gradient(135deg, darken($jst-danger, 20%), $jst-danger); }
.as-hero--cancelled { background: linear-gradient(135deg, darken($jst-info, 20%), $jst-info); }
.as-hero__icon { display: block; font-size: 96rpx; }
.as-hero__title { display: block; margin-top: $jst-space-md; font-size: $jst-font-xl; font-weight: $jst-weight-semibold; }
.as-hero__reason { display: block; margin-top: $jst-space-md; font-size: $jst-font-sm; color: rgba(255, 255, 255, 0.76); padding: $jst-space-md $jst-space-lg; background: rgba(255, 255, 255, 0.18); border-radius: $jst-radius-md; }

.as-section { margin: $jst-space-xl $jst-space-xl 0; padding: $jst-space-lg; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; }
.as-section__head { display: flex; align-items: center; justify-content: space-between; margin-bottom: $jst-space-xs; }
.as-section__title { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; }

::v-deep .as-section .u-cell {
  padding-left: 0;
  padding-right: 0;
}

.as-actions { margin: $jst-space-xl $jst-space-xl 0; display: flex; flex-direction: column; gap: 20rpx; }
.as-actions__btn { width: 100%; }
.as-empty { margin-top: 120rpx; }

/* 驳回原因卡 */
.as-reject-card {
  margin: $jst-space-xl $jst-space-xl 0;
  padding: $jst-space-lg;
  background: $jst-danger-light;
  border: 2rpx solid $jst-danger;
  border-left-width: 8rpx;
  border-radius: $jst-radius-md;
  box-shadow: $jst-shadow-sm;
}

.as-reject-card__head {
  display: flex;
  align-items: center;
  gap: $jst-space-xs;
  margin-bottom: $jst-space-sm;
}

.as-reject-card__icon {
  font-size: $jst-font-md;
}

.as-reject-card__title {
  font-size: $jst-font-md;
  font-weight: $jst-weight-semibold;
  color: $jst-danger;
}

.as-reject-card__reason {
  display: block;
  margin-top: $jst-space-xs;
  padding: $jst-space-md;
  background: $jst-bg-card;
  border-radius: $jst-radius-sm;
  font-size: $jst-font-base;
  line-height: 1.7;
  color: $jst-text-primary;
  white-space: pre-wrap;
}

.as-reject-card__meta {
  margin-top: $jst-space-md;
  display: flex;
  flex-direction: column;
  gap: $jst-space-xs;
}

.as-reject-card__meta-row {
  display: flex;
  justify-content: space-between;
  font-size: $jst-font-xs;
}

.as-reject-card__meta-key {
  color: $jst-text-secondary;
}

.as-reject-card__meta-value {
  color: $jst-text-primary;
  font-weight: $jst-weight-medium;
}

/* 红主色重新编辑按钮 */
::v-deep .as-actions__btn--resubmit.u-button {
  background: $jst-danger !important;
  border-color: $jst-danger !important;
  color: $jst-text-inverse !important;
}
</style>
