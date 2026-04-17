<!-- 中文注释: 渠道方 · 批量报名 (E1-CH-7 Part A)
     对应原型: 无独立 PNG (参考 channel-students.png 视觉风格)
     功能: 3Tab添加参赛人(已绑定学生/手动新增临时档案/Excel导入) + 赛事选择 + 底部提交
     数据来源: GET /jst/wx/channel/students + POST batch-create + POST batch-enroll -->
<template>
  <view class="be-page">
    <!-- 导航 -->
    <view class="be-header" :style="{ paddingTop: navPaddingTop }">
      <view class="be-header__back" @tap="goBack">←</view>
      <text class="be-header__title">批量报名</text>
    </view>

    <!-- 赛事选择 -->
    <view class="be-section">
      <view class="be-section__title">选择赛事</view>
      <view class="be-contest" @tap="showContestPicker = true">
        <template v-if="selectedContest">
          <text class="be-contest__name">{{ selectedContest.contestName }}</text>
          <text class="be-contest__price">报名费: ¥{{ selectedContest.price || '0' }} / 人</text>
        </template>
        <text v-else class="be-contest__placeholder">请选择赛事 ›</text>
      </view>
    </view>

    <!-- 3 Tab 切换 -->
    <view class="be-tabs">
      <view v-for="tab in addTabs" :key="tab.key" :class="['be-tab', activeTab === tab.key ? 'be-tab--active' : '']" @tap="activeTab = tab.key">
        {{ tab.label }}
      </view>
    </view>

    <!-- Tab A: 从已绑定学生选择 -->
    <view v-if="activeTab === 'bound'" class="be-section">
      <u-empty v-if="!boundStudents.length && !loadingStudents" mode="data" text="暂无绑定学生，请先邀请学生绑定"></u-empty>
      <view v-for="stu in boundStudents" :key="stu.bindingId" class="be-check-item" @tap="toggleStudent(stu)">
        <view :class="['be-checkbox', isStudentSelected(stu) ? 'be-checkbox--checked' : '']">
          <text v-if="isStudentSelected(stu)">✓</text>
        </view>
        <view class="be-check-info">
          <text class="be-check-name">{{ stu.studentName || '--' }}</text>
          <text class="be-check-meta">{{ stu.schoolName || '' }} {{ stu.mobileMasked || '' }}</text>
        </view>
      </view>
    </view>

    <!-- Tab B: 手动新增临时档案 -->
    <view v-if="activeTab === 'manual'" class="be-section">
      <view v-for="(item, idx) in manualList" :key="idx" class="be-manual-card">
        <view class="be-manual-header">
          <text class="be-manual-idx">参赛人 {{ idx + 1 }}</text>
          <text class="be-manual-remove" @tap="removeManual(idx)">删除</text>
        </view>
        <view class="be-field">
          <text class="be-field__label">姓名 *</text>
          <input class="be-field__input" v-model="item.name" placeholder="请输入姓名" />
        </view>
        <view class="be-field">
          <text class="be-field__label">性别</text>
          <picker mode="selector" :range="genderOptions" range-key="label" @change="e => item.gender = genderOptions[e.detail.value].value">
            <view class="be-field__input">{{ getGenderLabel(item.gender) || '请选择' }}</view>
          </picker>
        </view>
        <view class="be-field">
          <text class="be-field__label">出生日期</text>
          <picker mode="date" @change="e => item.birthday = e.detail.value">
            <view class="be-field__input">{{ item.birthday || '请选择日期' }}</view>
          </picker>
        </view>
        <view class="be-field">
          <text class="be-field__label">监护人姓名</text>
          <input class="be-field__input" v-model="item.guardianName" placeholder="请输入" />
        </view>
        <view class="be-field">
          <text class="be-field__label">监护人手机 *</text>
          <input class="be-field__input" v-model="item.guardianMobile" placeholder="请输入手机号" type="number" maxlength="11" />
        </view>
        <view class="be-field">
          <text class="be-field__label">学校/机构</text>
          <input class="be-field__input" v-model="item.school" placeholder="请输入" />
        </view>
        <view class="be-field">
          <text class="be-field__label">班级</text>
          <input class="be-field__input" v-model="item.className" placeholder="请输入" />
        </view>
        <view class="be-field">
          <text class="be-field__label">证件类型</text>
          <picker mode="selector" :range="idTypeOptions" range-key="label" @change="e => item.idType = idTypeOptions[e.detail.value].value">
            <view class="be-field__input">{{ getIdTypeLabel(item.idType) || '请选择' }}</view>
          </picker>
        </view>
        <view class="be-field">
          <text class="be-field__label">证件号</text>
          <input class="be-field__input" v-model="item.idNo" placeholder="请输入证件号码" />
        </view>
      </view>
      <u-button text="+ 新增一行参赛人" shape="circle" plain @click="addManual"></u-button>
    </view>

    <!-- Tab C: Excel 导入 -->
    <view v-if="activeTab === 'excel'" class="be-section">
      <view class="be-excel-tip">
        <text class="be-excel-tip__title">Excel 批量导入</text>
        <text class="be-excel-tip__desc">1. 下载模板 → 2. 填写信息 → 3. 上传文件</text>
      </view>
      <view class="be-excel-actions">
        <u-button text="下载模板" icon="download" plain shape="circle" @click="downloadTemplate"></u-button>
        <u-button text="上传 Excel" icon="upload" type="primary" shape="circle" @click="chooseFile"></u-button>
      </view>
      <view v-if="importResult" class="be-import-result">
        <text class="be-import-result__text">
          导入结果: {{ importResult.successCount || 0 }} 条成功
          <text v-if="importResult.failCount"> / {{ importResult.failCount }} 条失败</text>
        </text>
      </view>
    </view>

    <!-- 已选参赛人列表 -->
    <view v-if="selectedParticipants.length" class="be-section">
      <view class="be-section__title">已选参赛人 ({{ selectedParticipants.length }}人)</view>
      <view v-for="(p, idx) in selectedParticipants" :key="idx" class="be-selected-item">
        <view class="be-selected-avatar" :style="{ background: getAvatarColor(p.name || p.studentName) }">
          <text>{{ ((p.name || p.studentName || '').slice(0, 1)) }}</text>
        </view>
        <view class="be-selected-info">
          <text class="be-selected-name">{{ p.name || p.studentName }}</text>
          <u-tag
            :text="p.source === 'bound' ? '已绑定' : p.source === 'manual' ? '临时档案' : '导入'"
            :type="p.source === 'bound' ? 'success' : (p.source === 'manual' ? 'warning' : 'primary')"
            size="mini"
            plain
            shape="circle"
          ></u-tag>
        </view>
        <text class="be-selected-remove" @tap="removeParticipant(idx)">✕</text>
      </view>
    </view>

    <!-- 底部提交 -->
    <view class="be-footer">
      <view class="be-footer__summary">
        <text class="be-footer__count">{{ selectedParticipants.length }} 人</text>
        <text class="be-footer__total">合计: ¥{{ totalAmount }}</text>
      </view>
      <u-button
        :text="submitting ? '提交中...' : '提交报名'"
        type="primary"
        shape="circle"
        :disabled="!canSubmit || submitting"
        @click="onSubmit"
      ></u-button>
    </view>

    <!-- 赛事选择弹窗 -->
    <u-popup :show="showContestPicker" mode="bottom" :round="16" @close="showContestPicker = false" @open="showContestPicker = true">
      <view class="be-picker">
        <view class="be-picker__header">
          <text class="be-picker__title">选择赛事</text>
          <text class="be-picker__close" @tap="showContestPicker = false">✕</text>
        </view>
        <scroll-view scroll-y class="be-picker__body">
          <view v-for="c in contestList" :key="c.contestId" class="be-picker__item" @tap="selectContest(c)">
            <text class="be-picker__name">{{ c.contestName }}</text>
            <text class="be-picker__price">¥{{ c.price || '0' }}</text>
          </view>
          <view v-if="!contestList.length" class="be-picker__empty">
            <text>暂无可报名赛事</text>
          </view>
        </scroll-view>
      </view>
    </u-popup>
  </view>
</template>

<script>
import { getChannelStudents, batchCreateParticipants, batchEnroll, batchImportParticipants } from '@/api/channel'
import { getContestList } from '@/api/contest'
import { getToken } from '@/api/request'

const AVATAR_COLORS = [
  'linear-gradient(135deg, #283593, #3949AB)',
  'linear-gradient(135deg, #E65100, #FF7043)',
  'linear-gradient(135deg, #1B5E20, #66BB6A)',
  'linear-gradient(135deg, #880E4F, #F06292)',
  'linear-gradient(135deg, #4527A0, #7E57C2)'
]

export default {
  data() {
    return {
      skeletonShow: true, // [visual-effect]
      activeTab: 'bound',
      addTabs: [
        { key: 'bound', label: '已绑定学生' },
        { key: 'manual', label: '手动新增' },
        { key: 'excel', label: 'Excel导入' }
      ],
      // 赛事
      showContestPicker: false,
      contestList: [],
      selectedContest: null,
      // Tab A
      boundStudents: [],
      loadingStudents: false,
      selectedBound: [], // 已勾选的学生 id 列表
      // Tab B
      manualList: [],
      // Tab C
      importResult: null,
      importedParticipants: [],
      // 提交
      submitting: false,
      // 单学生跳入
      singleStudentId: null,

      genderOptions: [
        { value: 'M', label: '男' },
        { value: 'F', label: '女' }
      ],
      idTypeOptions: [
        { value: 'ID_CARD', label: '身份证' },
        { value: 'PASSPORT', label: '护照' },
        { value: 'OTHER', label: '其他' }
      ]
    }
  },
  computed: {
    // 汇聚所有来源的参赛人列表
    selectedParticipants() {
      const result = []
      // 已绑定学生
      this.boundStudents.forEach(stu => {
        const id = stu.studentId || stu.bindingId
        if (this.selectedBound.includes(id)) {
          result.push({ ...stu, name: stu.studentName, source: 'bound', participantId: stu.participantId || stu.studentId })
        }
      })
      // 手动新增 (仅显示已填姓名的)
      this.manualList.forEach(m => {
        if (m.name) result.push({ ...m, source: 'manual' })
      })
      // 导入
      this.importedParticipants.forEach(p => {
        result.push({ ...p, source: 'import' })
      })
      return result
    },
    totalAmount() {
      const price = (this.selectedContest && this.selectedContest.price) || 0
      return (this.selectedParticipants.length * Number(price)).toFixed(2)
    },
    canSubmit() {
      return this.selectedContest && this.selectedParticipants.length > 0
    }
  },
  onLoad(opts) {
    if (opts.singleStudentId) {
      this.singleStudentId = opts.singleStudentId
    }
    this.loadContests()
    this.loadBoundStudents()
  },
  methods: {
    async loadContests() {
      try {
        const res = await getContestList({ pageNum: 1, pageSize: 50, status: 'enrolling' })
        this.contestList = (res && res.rows) || (Array.isArray(res) ? res : [])
      } catch (e) {
        this.contestList = []
      }
    },

    async loadBoundStudents() {
      this.loadingStudents = true
      try {
        const res = await getChannelStudents({ pageNum: 1, pageSize: 100 })
        this.boundStudents = (res && res.rows) || (Array.isArray(res) ? res : [])
        // 若从学生管理单学生跳入，预选
        if (this.singleStudentId) {
          const match = this.boundStudents.find(s => String(s.studentId) === String(this.singleStudentId) || String(s.bindingId) === String(this.singleStudentId))
          if (match) {
            this.selectedBound = [match.studentId || match.bindingId]
          }
        }
      } catch (e) {
        this.boundStudents = []
      }
      this.loadingStudents = false
    },

    selectContest(c) {
      this.selectedContest = c
      this.showContestPicker = false
    },

    // Tab A: 学生勾选
    toggleStudent(stu) {
      const id = stu.studentId || stu.bindingId
      const idx = this.selectedBound.indexOf(id)
      if (idx >= 0) {
        this.selectedBound.splice(idx, 1)
      } else {
        this.selectedBound.push(id)
      }
    },
    isStudentSelected(stu) {
      const id = stu.studentId || stu.bindingId
      return this.selectedBound.includes(id)
    },

    // Tab B: 手动新增
    addManual() {
      this.manualList.push({
        name: '', gender: '', birthday: '', guardianName: '', guardianMobile: '',
        school: '', className: '', idType: '', idNo: ''
      })
    },
    removeManual(idx) {
      this.manualList.splice(idx, 1)
    },

    // Tab C: Excel
    downloadTemplate() {
      uni.showToast({ title: '模板下载功能待接入 OSS', icon: 'none' })
    },
    async chooseFile() {
      // #ifdef MP-WEIXIN
      try {
        const res = await new Promise((resolve, reject) => {
          wx.chooseMessageFile({
            count: 1,
            type: 'file',
            extension: ['xlsx', 'xls'],
            success: resolve,
            fail: reject
          })
        })
        if (res.tempFiles && res.tempFiles.length) {
          const result = await batchImportParticipants(res.tempFiles[0].path, getToken())
          this.importResult = result || {}
          if (result && result.participants) {
            this.importedParticipants = result.participants.map(p => ({ ...p, source: 'import' }))
          }
        }
      } catch (e) {}
      // #endif
      // #ifdef H5
      uni.showToast({ title: 'H5 端暂不支持文件选择', icon: 'none' })
      // #endif
    },

    // 移除已选参赛人
    removeParticipant(idx) {
      const p = this.selectedParticipants[idx]
      if (p.source === 'bound') {
        const id = p.studentId || p.bindingId || p.participantId
        const bIdx = this.selectedBound.indexOf(id)
        if (bIdx >= 0) this.selectedBound.splice(bIdx, 1)
      } else if (p.source === 'manual') {
        // 找到 manualList 中对应项
        const mIdx = this.manualList.findIndex(m => m.name === p.name && m.guardianMobile === p.guardianMobile)
        if (mIdx >= 0) this.manualList.splice(mIdx, 1)
      } else if (p.source === 'import') {
        const iIdx = this.importedParticipants.findIndex(ip => ip.name === p.name)
        if (iIdx >= 0) this.importedParticipants.splice(iIdx, 1)
      }
    },

    // 提交批量报名
    async onSubmit() {
      if (!this.canSubmit || this.submitting) return

      // 校验手动新增必填
      for (const m of this.manualList) {
        if (m.name && !m.guardianMobile) {
          uni.showToast({ title: `请填写 ${m.name} 的监护人手机号`, icon: 'none' })
          return
        }
      }

      this.submitting = true
      try {
        // 先创建临时档案 (手动新增的)
        const manualParticipants = this.manualList.filter(m => m.name)
        let createdIds = []
        if (manualParticipants.length) {
          const res = await batchCreateParticipants({ participants: manualParticipants })
          createdIds = (res && res.participantIds) || []
        }

        // 收集所有 participantId
        const participantIds = []
        // 已绑定学生
        this.boundStudents.forEach(stu => {
          const id = stu.studentId || stu.bindingId
          if (this.selectedBound.includes(id)) {
            participantIds.push(stu.participantId || stu.studentId)
          }
        })
        // 手动创建的
        participantIds.push(...createdIds)
        // 导入的
        this.importedParticipants.forEach(p => {
          if (p.participantId) participantIds.push(p.participantId)
        })

        // 批量报名
        const enrollRes = await batchEnroll({
          contestId: this.selectedContest.contestId,
          participantIds
        })

        // 中文注释: 批量报名完成后展示明确的成功/失败汇总而非直接静默返回
        const successCount = (enrollRes && (enrollRes.validCount || enrollRes.successCount)) || participantIds.length
        const failCount = (enrollRes && (enrollRes.invalidCount || enrollRes.failCount)) || 0
        uni.showModal({
          title: '批量报名完成',
          content: `成功 ${successCount} 人 / 失败 ${failCount} 人${failCount ? '\n失败名单请在"报名审核"中查看。' : ''}`,
          confirmText: '返回',
          showCancel: false,
          success: () => {
            uni.navigateBack()
          }
        })
      } catch (e) {
        // toast 已在 request 层处理
      }
      this.submitting = false
    },

    getAvatarColor(name) {
      if (!name) return AVATAR_COLORS[0]
      return AVATAR_COLORS[name.charCodeAt(0) % AVATAR_COLORS.length]
    },
    getGenderLabel(v) {
      const item = this.genderOptions.find(o => o.value === v)
      return item ? item.label : ''
    },
    getIdTypeLabel(v) {
      const item = this.idTypeOptions.find(o => o.value === v)
      return item ? item.label : ''
    },
    goBack() { uni.navigateBack() }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.be-page { min-height: 100vh; padding-bottom: calc(160rpx + env(safe-area-inset-bottom)); background: $jst-bg-page; }

.be-header { display: flex; align-items: center; padding: 0 32rpx; height: 112rpx; padding-top: 88rpx; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; position: sticky; top: 0; z-index: 40; }
.be-header__back { width: 72rpx; height: 72rpx; border-radius: $jst-radius-sm; background: $jst-bg-page; display: flex; align-items: center; justify-content: center; font-size: 36rpx; margin-right: 24rpx; }
.be-header__title { flex: 1; font-size: 34rpx; font-weight: 600; color: $jst-text-primary; }

.be-section { margin: 24rpx 32rpx 0; background: $jst-bg-card; border-radius: $jst-radius-lg; box-shadow: $jst-shadow-sm; overflow: hidden; padding: 28rpx; }
.be-section__title { font-size: 30rpx; font-weight: 600; color: $jst-text-primary; margin-bottom: 20rpx; display: flex; align-items: center; gap: 12rpx; }
.be-section__title::before { content: ''; width: 6rpx; height: 30rpx; background: $jst-brand; border-radius: 4rpx; }

/* 赛事选择 */
.be-contest { padding: 20rpx; background: $jst-bg-page; border-radius: $jst-radius-md; }
.be-contest__name { display: block; font-size: 28rpx; font-weight: 600; color: $jst-text-primary; }
.be-contest__price { display: block; margin-top: 8rpx; font-size: 24rpx; color: $jst-warning; font-weight: 600; }
.be-contest__placeholder { font-size: 28rpx; color: $jst-text-secondary; }

/* Tab */
.be-tabs { display: flex; gap: 16rpx; padding: 24rpx 32rpx 0; }
.be-tab { flex: 1; height: 72rpx; border: 3rpx solid $jst-border; border-radius: $jst-radius-round; background: $jst-bg-card; font-size: 26rpx; font-weight: 500; color: $jst-text-regular; display: flex; align-items: center; justify-content: center; }
.be-tab--active { border-color: $jst-brand; background: $jst-brand-light; color: $jst-brand; font-weight: 600; }

/* Tab A 勾选 */
.be-check-item { display: flex; align-items: center; gap: 20rpx; padding: 20rpx 0; border-bottom: 2rpx solid $jst-border; }
.be-check-item:last-child { border-bottom: none; }
.be-checkbox { width: 44rpx; height: 44rpx; border: 3rpx solid $jst-border; border-radius: $jst-radius-sm; display: flex; align-items: center; justify-content: center; font-size: 28rpx; color: $jst-text-inverse; flex-shrink: 0; }
.be-checkbox--checked { background: $jst-brand; border-color: $jst-brand; }
.be-check-info { flex: 1; min-width: 0; }
.be-check-name { display: block; font-size: 28rpx; font-weight: 600; color: $jst-text-primary; }
.be-check-meta { display: block; font-size: 22rpx; color: $jst-text-secondary; margin-top: 4rpx; }

/* Tab B 手动 */
.be-manual-card { padding: 24rpx 0; border-bottom: 2rpx solid $jst-border; }
.be-manual-card:last-child { border-bottom: none; }
.be-manual-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.be-manual-idx { font-size: 28rpx; font-weight: 600; color: $jst-text-primary; }
.be-manual-remove { font-size: 24rpx; color: $jst-danger; font-weight: 600; }
.be-field { display: flex; align-items: center; margin-bottom: 16rpx; }
.be-field__label { width: 180rpx; font-size: 26rpx; color: $jst-text-regular; flex-shrink: 0; }
.be-field__input { flex: 1; height: 64rpx; padding: 0 16rpx; background: $jst-bg-page; border-radius: $jst-radius-sm; font-size: 26rpx; color: $jst-text-primary; display: flex; align-items: center; }

/* Tab C Excel */
.be-excel-tip { margin-bottom: 24rpx; }
.be-excel-tip__title { display: block; font-size: 28rpx; font-weight: 600; color: $jst-text-primary; }
.be-excel-tip__desc { display: block; margin-top: 8rpx; font-size: 24rpx; color: $jst-text-secondary; }
.be-excel-actions { display: flex; gap: 20rpx; }
::v-deep .be-excel-actions .u-button { flex: 1; }
.be-import-result { margin-top: 20rpx; padding: 16rpx; background: $jst-success-light; border-radius: $jst-radius-sm; }
.be-import-result__text { font-size: 26rpx; color: $jst-success; }

/* 已选列表 */
.be-selected-item { display: flex; align-items: center; gap: 20rpx; padding: 16rpx 0; border-bottom: 2rpx solid $jst-border; }
.be-selected-item:last-child { border-bottom: none; }
.be-selected-avatar { width: 64rpx; height: 64rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 28rpx; font-weight: 600; color: $jst-text-inverse; flex-shrink: 0; }
.be-selected-info { flex: 1; min-width: 0; }
.be-selected-name { display: block; font-size: 28rpx; font-weight: 600; color: $jst-text-primary; }
.be-selected-remove { width: 48rpx; height: 48rpx; display: flex; align-items: center; justify-content: center; font-size: 28rpx; color: $jst-text-secondary; flex-shrink: 0; }

/* 底部 */
.be-footer { position: fixed; bottom: 0; left: 0; right: 0; background: rgba($jst-bg-card, 0.97); border-top: 2rpx solid $jst-border; padding: 20rpx 32rpx; padding-bottom: calc(20rpx + env(safe-area-inset-bottom)); display: flex; align-items: center; justify-content: space-between; gap: 24rpx; z-index: 50; box-shadow: $jst-shadow-sm; }
.be-footer__summary { flex: 1; }
.be-footer__count { display: block; font-size: 26rpx; color: $jst-text-regular; }
.be-footer__total { display: block; font-size: 32rpx; font-weight: 600; color: $jst-warning; margin-top: 4rpx; }
::v-deep .be-footer .u-button { width: 280rpx; height: 88rpx; flex-shrink: 0; }

/* 空状态 */
.be-empty { padding: 48rpx; text-align: center; font-size: 28rpx; color: $jst-text-secondary; }

/* 弹窗 */
.be-picker { width: 100%; max-height: 70vh; background: $jst-bg-card; border-radius: $jst-radius-lg $jst-radius-lg 0 0; overflow: hidden; }
.be-picker__header { display: flex; align-items: center; justify-content: space-between; padding: 28rpx 32rpx; border-bottom: 2rpx solid $jst-border; }
.be-picker__title { font-size: 32rpx; font-weight: 600; color: $jst-text-primary; }
.be-picker__close { font-size: 36rpx; color: $jst-text-secondary; padding: 8rpx; }
.be-picker__body { max-height: 60vh; padding: 0 32rpx; }
.be-picker__item { display: flex; align-items: center; justify-content: space-between; padding: 28rpx 0; border-bottom: 2rpx solid $jst-border; }
.be-picker__name { flex: 1; font-size: 28rpx; font-weight: 600; color: $jst-text-primary; }
.be-picker__price { font-size: 26rpx; color: $jst-warning; font-weight: 600; flex-shrink: 0; }
.be-picker__empty { padding: 64rpx; text-align: center; font-size: 28rpx; color: $jst-text-secondary; }
</style>
