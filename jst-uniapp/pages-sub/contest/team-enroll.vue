<!-- 中文注释: 团队报名页 (REFACTOR-9)
     功能: 队长信息填写 → 添加队员 → 确认提交
     调用接口: GET /jst/wx/contest/{id} (teamMinSize/teamMaxSize/teamLeaderFields)
               GET /jst/wx/enroll/template (表单模板)
               POST /jst/wx/enroll/team/submit
     防呆: 人数 min/max、手机号重复、队长不可移除、已报名检查 -->
<template>
  <view class="te-page">
    <!-- 导航 -->
    <view class="te-nav">
      <view class="te-nav__back" @tap="goBack">←</view>
      <text class="te-nav__title">团队报名</text>
      <view class="te-nav__placeholder"></view>
    </view>

    <!-- Hero -->
    <view class="te-hero">
      <text class="te-hero__name">{{ contestName || '团队报名' }}</text>
      <text class="te-hero__desc">{{ teamSizeHint }}</text>
    </view>

    <!-- 步骤指示器 -->
    <view class="te-steps">
      <view v-for="(s, i) in steps" :key="i" :class="['te-step', step >= i && 'te-step--active', step > i && 'te-step--done']">
        <view class="te-step__circle">
          <text class="te-step__num">{{ step > i ? '✓' : i + 1 }}</text>
        </view>
        <text class="te-step__label">{{ s }}</text>
      </view>
      <view class="te-steps__line"></view>
    </view>

    <!-- Step 1: 队长信息 -->
    <view v-if="step === 0" class="te-content">
      <view class="te-card">
        <view class="te-card__header">
          <text class="te-card__title">队长信息</text>
        </view>

        <view v-for="field in leaderFields" :key="field.key" class="te-form-item">
          <text class="te-form-item__label">
            <text class="te-form-item__required">*</text>
            {{ field.label }}
          </text>
          <input
            class="te-form-item__input"
            :type="field.inputType || 'text'"
            :placeholder="'请输入' + field.label"
            :value="leaderInfo[field.key]"
            @input="onLeaderInput(field.key, $event)"
          />
        </view>
      </view>

      <view class="te-footer">
        <u-button type="primary" shape="circle" :disabled="!leaderValid" @click="nextStep">
          下一步：添加队员
        </u-button>
      </view>
    </view>

    <!-- Step 2: 队员管理 -->
    <view v-if="step === 1" class="te-content">
      <view class="te-card">
        <view class="te-card__header">
          <text class="te-card__title">队员列表</text>
          <text class="te-card__badge">{{ members.length + 1 }} / {{ teamMaxSize || '?' }} 人</text>
        </view>

        <!-- 队长（不可移除） -->
        <view class="te-member te-member--leader">
          <view class="te-member__info">
            <text class="te-member__name">{{ leaderInfo.name || '队长' }}</text>
            <u-tag text="队长" type="primary" size="mini" plain shape="circle"></u-tag>
          </view>
          <text class="te-member__phone">{{ maskPhone(leaderInfo.phone) }}</text>
        </view>

        <!-- 队员列表 -->
        <view v-for="(m, idx) in members" :key="idx" class="te-member">
          <view class="te-member__info">
            <text class="te-member__name">{{ m.name || '队员' + (idx + 1) }}</text>
            <text class="te-member__phone">{{ maskPhone(m.phone) }}</text>
          </view>
          <view class="te-member__remove" @tap="removeMember(idx)">
            <text class="te-member__remove-text">移除</text>
          </view>
        </view>

        <!-- 人数不足提示 -->
        <view v-if="memberShortage > 0" class="te-hint te-hint--warn">
          <text class="te-hint__text">还需添加 {{ memberShortage }} 人才可提交</text>
        </view>

        <!-- 人数已满提示 -->
        <view v-if="isTeamFull" class="te-hint te-hint--info">
          <text class="te-hint__text">已达最大人数 {{ teamMaxSize }} 人</text>
        </view>

        <!-- 添加队员按钮组 -->
        <view v-if="!isTeamFull" class="te-add-actions">
          <view class="te-add-btn" @tap="showAddPopup = true">
            <text class="te-add-btn__icon">+</text>
            <text class="te-add-btn__text">手动添加</text>
          </view>
          <view class="te-add-btn" @tap="pickFromParticipant">
            <text class="te-add-btn__icon">📋</text>
            <text class="te-add-btn__text">从档案选择</text>
          </view>
          <button class="te-add-btn te-add-btn--share" open-type="share">
            <text class="te-add-btn__icon">📤</text>
            <text class="te-add-btn__text">微信邀请</text>
          </button>
        </view>
      </view>

      <view class="te-footer te-footer--dual">
        <u-button class="te-footer__btn te-footer__btn--ghost" shape="circle" @click="prevStep">上一步</u-button>
        <u-button
          class="te-footer__btn te-footer__btn--primary"
          type="primary"
          shape="circle"
          :disabled="memberShortage > 0"
          @click="nextStep"
        >下一步：确认提交</u-button>
      </view>
    </view>

    <!-- Step 3: 确认提交 -->
    <view v-if="step === 2" class="te-content">
      <view class="te-card">
        <view class="te-card__header">
          <text class="te-card__title">团队摘要</text>
        </view>

        <view class="te-summary-row">
          <text class="te-summary-key">赛事名称</text>
          <text class="te-summary-val">{{ contestName }}</text>
        </view>
        <view class="te-summary-row">
          <text class="te-summary-key">队长</text>
          <text class="te-summary-val">{{ leaderInfo.name }}</text>
        </view>
        <view class="te-summary-row">
          <text class="te-summary-key">队员人数</text>
          <text class="te-summary-val">{{ members.length }} 人</text>
        </view>
        <view class="te-summary-row">
          <text class="te-summary-key">团队总人数</text>
          <text class="te-summary-val te-summary-val--highlight">{{ totalCount }} 人</text>
        </view>
        <view v-if="contestPrice > 0" class="te-summary-row">
          <text class="te-summary-key">预计总费用</text>
          <text class="te-summary-val te-summary-val--price">{{ totalPriceText }}</text>
        </view>

        <!-- 成员明细 -->
        <view class="te-detail-section">
          <text class="te-detail-title">成员明细</text>
          <view class="te-detail-item">
            <text class="te-detail-name">{{ leaderInfo.name }}（队长）</text>
            <text class="te-detail-phone">{{ maskPhone(leaderInfo.phone) }}</text>
          </view>
          <view v-for="(m, idx) in members" :key="idx" class="te-detail-item">
            <text class="te-detail-name">{{ m.name }}</text>
            <text class="te-detail-phone">{{ maskPhone(m.phone) }}</text>
          </view>
        </view>
      </view>

      <view class="te-footer te-footer--dual">
        <u-button class="te-footer__btn te-footer__btn--ghost" shape="circle" @click="prevStep">上一步</u-button>
        <u-button
          class="te-footer__btn te-footer__btn--primary"
          type="primary"
          shape="circle"
          :loading="submitting"
          :disabled="submitting"
          @click="handleSubmit"
        >提交团队报名</u-button>
      </view>
    </view>

    <!-- 添加队员弹窗 -->
    <u-popup :show="showAddPopup" mode="bottom" :round="16" @close="showAddPopup = false">
      <view class="te-popup">
        <view class="te-popup__header">
          <text class="te-popup__title">添加队员</text>
          <view class="te-popup__close" @tap="showAddPopup = false">
            <text>✕</text>
          </view>
        </view>

        <view class="te-popup__body">
          <view v-for="field in memberFields" :key="field.key" class="te-form-item">
            <text class="te-form-item__label">
              <text v-if="field.required" class="te-form-item__required">*</text>
              {{ field.label }}
            </text>
            <input
              class="te-form-item__input"
              :type="field.inputType || 'text'"
              :placeholder="'请输入' + field.label"
              :value="newMember[field.key]"
              @input="onNewMemberInput(field.key, $event)"
            />
          </view>

          <!-- 手机号重复提示 -->
          <view v-if="duplicatePhoneWarn" class="te-hint te-hint--warn">
            <text class="te-hint__text">{{ duplicatePhoneWarn }}</text>
          </view>
        </view>

        <view class="te-popup__footer">
          <u-button type="primary" shape="circle" :disabled="!newMemberValid" @click="confirmAddMember">确认添加</u-button>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script>
import { getContestDetail } from '@/api/contest'
import { submitTeamEnroll } from '@/api/enroll'
import { myParticipants } from '@/api/participant'

// 默认队长字段配置
const DEFAULT_LEADER_FIELDS = [
  { key: 'name', label: '姓名', required: true },
  { key: 'phone', label: '手机号', required: true, inputType: 'number' },
  { key: 'idCard', label: '身份证号', required: false },
  { key: 'school', label: '学校', required: false }
]

// 默认队员字段
const DEFAULT_MEMBER_FIELDS = [
  { key: 'name', label: '姓名', required: true },
  { key: 'phone', label: '手机号', required: true, inputType: 'number' }
]

export default {
  data() {
    return {
      contestId: null,
      contestName: '',
      contestPrice: 0,
      teamMinSize: 2,
      teamMaxSize: 10,
      leaderFieldsConfig: '',
      memberFieldsConfig: '',
      // 步骤
      step: 0,
      steps: ['队长信息', '添加队员', '确认提交'],
      // 队长表单
      leaderInfo: {},
      // 队员列表
      members: [],
      // 添加队员弹窗
      showAddPopup: false,
      newMember: {},
      // 提交
      submitting: false
    }
  },
  computed: {
    // 解析队长字段配置
    leaderFields() {
      if (!this.leaderFieldsConfig) return DEFAULT_LEADER_FIELDS
      const keys = this.leaderFieldsConfig.split(',').map(k => k.trim()).filter(Boolean)
      return keys.map(k => {
        const def = DEFAULT_LEADER_FIELDS.find(f => f.key === k)
        return def || { key: k, label: k, required: true }
      })
    },
    // 解析队员字段配置
    memberFields() {
      if (!this.memberFieldsConfig) return DEFAULT_MEMBER_FIELDS
      const keys = this.memberFieldsConfig.split(',').map(k => k.trim()).filter(Boolean)
      return keys.map(k => {
        const def = DEFAULT_MEMBER_FIELDS.find(f => f.key === k) || DEFAULT_LEADER_FIELDS.find(f => f.key === k)
        return def || { key: k, label: k, required: true }
      })
    },
    // 总人数（含队长）
    totalCount() {
      return this.members.length + 1
    },
    // 人数不足提示
    memberShortage() {
      return Math.max(0, this.teamMinSize - this.totalCount)
    },
    // 是否达到人数上限
    isTeamFull() {
      return this.teamMaxSize > 0 && this.totalCount >= this.teamMaxSize
    },
    // 队长表单校验
    leaderValid() {
      return this.leaderFields
        .filter(f => f.required)
        .every(f => {
          const val = this.leaderInfo[f.key]
          return val && String(val).trim().length > 0
        })
    },
    // 新队员表单校验
    newMemberValid() {
      if (this.duplicatePhoneWarn) return false
      return this.memberFields
        .filter(f => f.required)
        .every(f => {
          const val = this.newMember[f.key]
          return val && String(val).trim().length > 0
        })
    },
    // 手机号重复检查
    duplicatePhoneWarn() {
      const phone = (this.newMember.phone || '').trim()
      if (!phone) return ''
      if (this.leaderInfo.phone === phone) return '该手机号与队长重复'
      if (this.members.some(m => m.phone === phone)) return '该手机号已在队伍中'
      return ''
    },
    // 团队人数提示
    teamSizeHint() {
      if (this.teamMinSize && this.teamMaxSize) {
        return '团队人数要求：' + this.teamMinSize + ' ~ ' + this.teamMaxSize + ' 人（含队长）'
      }
      if (this.teamMinSize) {
        return '至少 ' + this.teamMinSize + ' 人'
      }
      return ''
    },
    // 总费用文案
    totalPriceText() {
      const total = this.contestPrice * this.totalCount
      return total > 0 ? '¥' + total.toFixed(2) : '免费'
    }
  },
  onLoad(query) {
    this.contestId = query && query.contestId ? Number(query.contestId) : null
    this.loadContest()
  },
  onShow() {
    // 从档案选择页返回
    const picked = uni.getStorageSync('te_picked_participant')
    if (picked && picked.name) {
      this.addMemberFromPick(picked)
      uni.removeStorageSync('te_picked_participant')
    }
  },
  // 微信分享
  onShareAppMessage() {
    return {
      title: '邀请你加入团队报名：' + (this.contestName || '赛事'),
      path: '/pages-sub/contest/team-enroll?contestId=' + this.contestId + '&inviteFrom=share'
    }
  },
  methods: {
    async loadContest() {
      if (!this.contestId) return
      try {
        const detail = await getContestDetail(this.contestId, { silent: true })
        if (!detail) return
        this.contestName = detail.contestName || ''
        this.contestPrice = Number(detail.price) || 0
        this.teamMinSize = detail.teamMinSize || 2
        this.teamMaxSize = detail.teamMaxSize || 10
        this.leaderFieldsConfig = detail.teamLeaderFields || ''
        this.memberFieldsConfig = detail.teamMemberFields || ''
      } catch (e) {
        uni.showToast({ title: '加载赛事信息失败', icon: 'none' })
      }
    },

    onLeaderInput(key, e) {
      this.leaderInfo = Object.assign({}, this.leaderInfo, { [key]: e.detail.value })
    },

    onNewMemberInput(key, e) {
      this.newMember = Object.assign({}, this.newMember, { [key]: e.detail.value })
    },

    nextStep() {
      if (this.step < this.steps.length - 1) {
        this.step++
      }
    },

    prevStep() {
      if (this.step > 0) {
        this.step--
      }
    },

    // 添加队员
    confirmAddMember() {
      if (!this.newMemberValid) return
      if (this.isTeamFull) {
        uni.showToast({ title: '已达最大人数', icon: 'none' })
        return
      }
      this.members.push(Object.assign({}, this.newMember))
      this.newMember = {}
      this.showAddPopup = false
      uni.showToast({ title: '已添加', icon: 'success' })
    },

    // 从档案选择添加
    pickFromParticipant() {
      uni.navigateTo({ url: '/pages-sub/my/participant?mode=pick&back=/pages-sub/contest/team-enroll&storageKey=te_picked_participant' })
    },

    addMemberFromPick(picked) {
      if (this.isTeamFull) {
        uni.showToast({ title: '已达最大人数', icon: 'none' })
        return
      }
      const phone = picked.phone || picked.mobile || ''
      // 重复检查
      if (phone && this.leaderInfo.phone === phone) {
        uni.showToast({ title: '该档案手机号与队长重复', icon: 'none' })
        return
      }
      if (phone && this.members.some(m => m.phone === phone)) {
        uni.showToast({ title: '该档案已在队伍中', icon: 'none' })
        return
      }
      this.members.push({
        name: picked.name || picked.participantName || '',
        phone: phone,
        idCard: picked.idCard || '',
        school: picked.school || '',
        participantId: picked.participantId || picked.id || null
      })
      uni.showToast({ title: '已添加', icon: 'success' })
    },

    // 移除队员（队长不可移除）
    removeMember(idx) {
      uni.showModal({
        title: '确认移除',
        content: '确定移除队员 ' + (this.members[idx].name || '') + ' ？',
        success: (res) => {
          if (res.confirm) {
            this.members.splice(idx, 1)
          }
        }
      })
    },

    // 手机号脱敏
    maskPhone(phone) {
      if (!phone || phone.length < 7) return phone || ''
      return phone.slice(0, 3) + '****' + phone.slice(-4)
    },

    // 提交团队报名
    async handleSubmit() {
      if (this.submitting || this.memberShortage > 0) return
      this.submitting = true
      try {
        const data = {
          contestId: this.contestId,
          leaderInfo: Object.assign({}, this.leaderInfo),
          members: this.members.map(m => Object.assign({}, m))
        }
        const res = await submitTeamEnroll(data)

        uni.showToast({ title: '团队报名已提交', icon: 'success' })
        setTimeout(() => {
          if (res && res.orderId) {
            uni.redirectTo({ url: '/pages-sub/my/order-detail?id=' + res.orderId })
          } else if (res && res.enrollId) {
            uni.redirectTo({ url: '/pages-sub/my/enroll-detail?id=' + res.enrollId })
          } else {
            uni.navigateBack()
          }
        }, 600)
      } catch (e) {
        if (e && e.msg) {
          uni.showToast({ title: e.msg, icon: 'none' })
        }
      } finally {
        this.submitting = false
      }
    },

    goBack() {
      if (this.step > 0) {
        this.prevStep()
        return
      }
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
      } else {
        uni.switchTab({ url: '/pages/contest/list' })
      }
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.te-page {
  min-height: 100vh;
  padding-bottom: calc(180rpx + env(safe-area-inset-bottom));
  background: $jst-bg-page;
}

/* 导航 */
.te-nav {
  position: sticky;
  top: 0;
  z-index: 20;
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 $jst-space-lg;
  background: $jst-brand-dark;
}
.te-nav__back, .te-nav__placeholder { width: 72rpx; flex-shrink: 0; }
.te-nav__back { font-size: 34rpx; color: rgba($jst-text-inverse, 0.96); }
.te-nav__title { flex: 1; text-align: center; font-size: $jst-font-md; font-weight: $jst-weight-bold; color: $jst-text-inverse; }

/* Hero */
.te-hero {
  padding: 28rpx $jst-space-lg 40rpx;
  background: linear-gradient(180deg, $jst-brand-dark 0%, $jst-brand 100%);
}
.te-hero__name {
  display: block;
  font-size: $jst-font-xl;
  font-weight: $jst-weight-bold;
  color: $jst-text-inverse;
}
.te-hero__desc {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-sm;
  color: rgba(255, 255, 255, 0.78);
}

/* 步骤条 */
.te-steps {
  display: flex;
  justify-content: space-between;
  padding: $jst-space-lg $jst-space-xxl;
  background: $jst-bg-card;
  margin: -$jst-space-md $jst-space-lg 0;
  border-radius: $jst-radius-lg;
  box-shadow: $jst-shadow-sm;
  position: relative;
  z-index: 1;
}
.te-steps__line {
  position: absolute;
  top: 50%;
  left: 80rpx;
  right: 80rpx;
  height: 4rpx;
  background: $jst-border;
  z-index: -1;
}
.te-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $jst-space-xs;
  z-index: 1;
}
.te-step__circle {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: $jst-bg-grey;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 3rpx solid $jst-border;
  transition: all $jst-duration-fast $jst-easing;
}
.te-step--active .te-step__circle {
  background: $jst-brand;
  border-color: $jst-brand;
}
.te-step--done .te-step__circle {
  background: $jst-success;
  border-color: $jst-success;
}
.te-step__num {
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
  color: $jst-text-secondary;
}
.te-step--active .te-step__num,
.te-step--done .te-step__num {
  color: $jst-text-inverse;
}
.te-step__label {
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}
.te-step--active .te-step__label { color: $jst-brand; font-weight: $jst-weight-medium; }

/* 内容区 */
.te-content {
  padding: $jst-space-lg;
}

/* 卡片 */
.te-card {
  padding: $jst-space-lg;
  background: $jst-bg-card;
  border-radius: $jst-radius-lg;
  box-shadow: $jst-shadow-sm;
  margin-bottom: $jst-space-lg;
}
.te-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $jst-space-lg;
  padding-bottom: $jst-space-md;
  border-bottom: 2rpx solid $jst-border;
}
.te-card__title { font-size: $jst-font-md; font-weight: $jst-weight-bold; color: $jst-text-primary; }
.te-card__badge {
  font-size: $jst-font-sm;
  color: $jst-brand;
  font-weight: $jst-weight-semibold;
}

/* 表单项 */
.te-form-item {
  margin-bottom: $jst-space-md;
}
.te-form-item__label {
  display: block;
  font-size: $jst-font-sm;
  color: $jst-text-regular;
  margin-bottom: $jst-space-xs;
}
.te-form-item__required { color: $jst-danger; margin-right: 4rpx; }
.te-form-item__input {
  width: 100%;
  height: 80rpx;
  padding: 0 $jst-space-md;
  background: $jst-bg-grey;
  border-radius: $jst-radius-sm;
  font-size: $jst-font-base;
  color: $jst-text-primary;
}

/* 队员卡片 */
.te-member {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $jst-space-md 0;
  border-bottom: 2rpx solid $jst-border;
}
.te-member:last-of-type { border-bottom: none; }
.te-member--leader {
  background: $jst-brand-light;
  margin: 0 (-$jst-space-lg);
  padding: $jst-space-md $jst-space-lg;
  border-radius: $jst-radius-sm;
  border-bottom: none;
  margin-bottom: $jst-space-sm;
}
.te-member__info {
  display: flex;
  align-items: center;
  gap: $jst-space-sm;
  flex: 1;
}
.te-member__name { font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.te-member__phone { font-size: $jst-font-sm; color: $jst-text-secondary; }
.te-member__remove {
  padding: $jst-space-xs $jst-space-md;
  border-radius: $jst-radius-round;
  background: $jst-danger-light;
}
.te-member__remove-text { font-size: $jst-font-xs; color: $jst-danger; font-weight: $jst-weight-medium; }

/* 提示 */
.te-hint {
  padding: $jst-space-sm $jst-space-md;
  border-radius: $jst-radius-sm;
  margin-top: $jst-space-sm;
}
.te-hint--warn { background: $jst-warning-light; }
.te-hint--info { background: $jst-brand-light; }
.te-hint__text { font-size: $jst-font-sm; }
.te-hint--warn .te-hint__text { color: $jst-warning; }
.te-hint--info .te-hint__text { color: $jst-brand; }

/* 添加队员按钮组 */
.te-add-actions {
  display: flex;
  gap: $jst-space-sm;
  margin-top: $jst-space-lg;
}
.te-add-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $jst-space-lg $jst-space-sm;
  background: $jst-bg-grey;
  border-radius: $jst-radius-md;
  border: 2rpx dashed $jst-border;
  line-height: normal;
  transition: transform $jst-duration-fast $jst-easing;
}
.te-add-btn:active { transform: scale(0.96); }
.te-add-btn--share {
  background: $jst-bg-grey;
  border: 2rpx dashed $jst-border;
}
.te-add-btn--share::after { display: none; }
.te-add-btn__icon { font-size: $jst-font-xl; margin-bottom: $jst-space-xs; }
.te-add-btn__text { font-size: $jst-font-xs; color: $jst-text-regular; font-weight: $jst-weight-medium; }

/* 确认摘要 */
.te-summary-row {
  display: flex;
  justify-content: space-between;
  padding: $jst-space-md 0;
  border-bottom: 2rpx solid $jst-border;
}
.te-summary-row:last-of-type { border-bottom: none; }
.te-summary-key { font-size: $jst-font-sm; color: $jst-text-secondary; }
.te-summary-val { font-size: $jst-font-sm; color: $jst-text-primary; font-weight: $jst-weight-medium; }
.te-summary-val--highlight { color: $jst-brand; font-weight: $jst-weight-bold; }
.te-summary-val--price { color: $jst-danger; font-size: $jst-font-md; font-weight: $jst-weight-bold; }

.te-detail-section {
  margin-top: $jst-space-lg;
  padding-top: $jst-space-md;
  border-top: 2rpx solid $jst-border;
}
.te-detail-title {
  display: block;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
  margin-bottom: $jst-space-sm;
}
.te-detail-item {
  display: flex;
  justify-content: space-between;
  padding: $jst-space-xs 0;
}
.te-detail-name { font-size: $jst-font-sm; color: $jst-text-regular; }
.te-detail-phone { font-size: $jst-font-sm; color: $jst-text-secondary; }

/* 底部 */
.te-footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: $jst-space-lg;
  padding-bottom: calc(#{$jst-space-lg} + env(safe-area-inset-bottom));
  background: rgba($jst-bg-card, 0.98);
  box-shadow: $jst-shadow-md;
  z-index: 10;
}
.te-footer--dual {
  display: flex;
  gap: $jst-space-md;
}
.te-footer__btn { flex: 1; }
::v-deep .te-footer .u-button,
::v-deep .te-footer__btn.u-button {
  height: 96rpx;
  border: none;
}
.te-footer__btn--ghost {
  border: 2rpx solid $jst-border !important;
  background: $jst-bg-card;
  color: $jst-brand;
}
.te-footer__btn--primary {
  background: linear-gradient(135deg, $jst-brand, $jst-brand-dark);
  color: $jst-text-inverse;
}

/* 弹窗 */
.te-popup {
  padding: $jst-space-xl;
  padding-bottom: calc(#{$jst-space-xl} + env(safe-area-inset-bottom));
}
.te-popup__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $jst-space-lg;
}
.te-popup__title { font-size: $jst-font-lg; font-weight: $jst-weight-bold; color: $jst-text-primary; }
.te-popup__close {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: $jst-bg-page;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: $jst-font-base;
  color: $jst-text-secondary;
}
.te-popup__body { padding: $jst-space-sm 0; }
.te-popup__footer { margin-top: $jst-space-lg; }
::v-deep .te-popup__footer .u-button {
  height: 96rpx;
  background: linear-gradient(135deg, $jst-brand, $jst-brand-dark);
  border: none;
}
</style>
