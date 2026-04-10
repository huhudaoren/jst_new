<!-- 中文注释: 渠道方 · 临时档案管理 (E1-CH-7 Part B)
     对应原型: 无独立 PNG (参考 channel-students.png 视觉风格)
     功能: 临时档案列表 + 3 状态(unclaimed/claimed/conflict) + 编辑/删除/查看
     数据来源: GET /jst/wx/channel/participant/my + DELETE /PUT -->
<template>
  <view class="pt-page">
    <!-- 导航 -->
    <view class="pt-header">
      <view class="pt-header__back" @tap="goBack">←</view>
      <text class="pt-header__title">临时档案管理</text>
    </view>

    <!-- 状态 Tab -->
    <view class="pt-tabs">
      <view v-for="tab in statusTabs" :key="tab.key" :class="['pt-tab', activeStatus === tab.key ? 'pt-tab--active' : '']" @tap="switchTab(tab.key)">
        {{ tab.label }}({{ tab.count }})
      </view>
    </view>

    <!-- 列表 -->
    <view class="pt-list">
      <view v-for="item in filteredList" :key="item.participantId" class="pt-card">
        <view class="pt-card__main">
          <view class="pt-card__avatar" :style="{ background: getAvatarColor(item.name) }">
            <text class="pt-card__avatar-text">{{ (item.name || '').slice(0, 1) }}</text>
          </view>
          <view class="pt-card__info">
            <text class="pt-card__name">{{ item.name || '--' }}</text>
            <text class="pt-card__meta">{{ item.guardianMobile || '' }} · {{ item.school || '' }}</text>
            <text class="pt-card__time">创建于 {{ formatDate(item.createTime) }}</text>
          </view>
          <view class="pt-card__tag" :class="'pt-card__tag--' + getTagType(item.claimStatus)">
            {{ getTagIcon(item.claimStatus) }} {{ getTagText(item.claimStatus) }}
          </view>
        </view>

        <!-- claimed 显示关联用户 -->
        <view v-if="item.claimStatus === 'claimed' && item.claimedUserName" class="pt-card__claimed">
          <text>已认领用户: {{ item.claimedUserName }}</text>
        </view>

        <!-- 操作区 -->
        <view class="pt-card__actions">
          <template v-if="item.claimStatus === 'unclaimed'">
            <view class="pt-card__action pt-card__action--primary" @tap="editItem(item)">✏️ 编辑</view>
            <view class="pt-card__action pt-card__action--danger" @tap="confirmDelete(item)">🗑️ 删除</view>
          </template>
          <template v-else-if="item.claimStatus === 'claimed'">
            <view class="pt-card__action" @tap="viewDetail(item)">👁️ 查看详情</view>
          </template>
          <template v-else-if="item.claimStatus === 'conflict'">
            <view class="pt-card__action pt-card__action--warning" @tap="viewDetail(item)">⚠️ 查看冲突</view>
          </template>
        </view>
      </view>

      <view v-if="hasMore" class="pt-loadmore" @tap="loadMore">
        <text>加载更多</text>
      </view>

      <view v-if="!filteredList.length && !loading" class="pt-empty">
        <text class="pt-empty__icon">📋</text>
        <text class="pt-empty__title">暂无临时档案</text>
        <text class="pt-empty__desc">通过批量报名创建临时参赛档案</text>
      </view>
    </view>

    <!-- 编辑弹窗 -->
    <view v-if="editVisible" class="pt-mask" @tap="editVisible = false">
      <view class="pt-edit-popup" @tap.stop>
        <view class="pt-edit-header">
          <text class="pt-edit-title">编辑临时档案</text>
          <text class="pt-edit-close" @tap="editVisible = false">✕</text>
        </view>
        <view class="pt-edit-body">
          <view class="pt-field">
            <text class="pt-field__label">姓名</text>
            <input class="pt-field__input" v-model="editForm.name" placeholder="请输入姓名" />
          </view>
          <view class="pt-field">
            <text class="pt-field__label">监护人手机</text>
            <input class="pt-field__input" v-model="editForm.guardianMobile" placeholder="请输入手机号" type="number" maxlength="11" />
          </view>
          <view class="pt-field">
            <text class="pt-field__label">学校/机构</text>
            <input class="pt-field__input" v-model="editForm.school" placeholder="请输入学校" />
          </view>
          <view class="pt-field">
            <text class="pt-field__label">班级</text>
            <input class="pt-field__input" v-model="editForm.className" placeholder="请输入班级" />
          </view>
        </view>
        <view class="pt-edit-footer">
          <view class="pt-edit-btn pt-edit-btn--ghost" @tap="editVisible = false">取消</view>
          <view class="pt-edit-btn pt-edit-btn--primary" @tap="saveEdit">保存</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getMyParticipants, updateParticipant, deleteParticipant } from '@/api/channel'

const AVATAR_COLORS = [
  'linear-gradient(135deg, #3F51B5, #5C6BC0)',
  'linear-gradient(135deg, #E65100, #FF7043)',
  'linear-gradient(135deg, #1B5E20, #66BB6A)',
  'linear-gradient(135deg, #880E4F, #F06292)',
  'linear-gradient(135deg, #4527A0, #7E57C2)'
]

export default {
  data() {
    return {
      loading: false,
      activeStatus: 'all',
      statusTabs: [
        { key: 'all', label: '全部', count: 0 },
        { key: 'unclaimed', label: '未认领', count: 0 },
        { key: 'claimed', label: '已认领', count: 0 },
        { key: 'conflict', label: '待处理', count: 0 }
      ],
      list: [],
      total: 0,
      pageNum: 1,
      pageSize: 20,
      hasMore: false,
      // 编辑
      editVisible: false,
      editForm: {},
      editId: null
    }
  },
  computed: {
    filteredList() {
      if (this.activeStatus === 'all') return this.list
      return this.list.filter(item => item.claimStatus === this.activeStatus)
    }
  },
  onShow() {
    this.pageNum = 1
    this.loadList()
  },
  methods: {
    async loadList() {
      this.loading = true
      try {
        const params = { pageNum: this.pageNum, pageSize: this.pageSize }
        const res = await getMyParticipants(params)
        const rows = (res && res.rows) || (Array.isArray(res) ? res : [])
        const total = (res && res.total) || rows.length
        if (this.pageNum === 1) {
          this.list = rows
        } else {
          this.list = this.list.concat(rows)
        }
        this.total = total
        this.hasMore = this.list.length < total
        // 更新 tab count
        this.statusTabs[0].count = total
        this.statusTabs[1].count = rows.filter(r => r.claimStatus === 'unclaimed').length
        this.statusTabs[2].count = rows.filter(r => r.claimStatus === 'claimed').length
        this.statusTabs[3].count = rows.filter(r => r.claimStatus === 'conflict').length
      } catch (e) {
        if (this.pageNum === 1) this.list = []
      }
      this.loading = false
    },

    switchTab(key) { this.activeStatus = key },
    loadMore() { this.pageNum++; this.loadList() },

    getAvatarColor(name) {
      if (!name) return AVATAR_COLORS[0]
      return AVATAR_COLORS[name.charCodeAt(0) % AVATAR_COLORS.length]
    },

    getTagType(status) {
      if (status === 'unclaimed') return 'warning'
      if (status === 'claimed') return 'success'
      if (status === 'conflict') return 'danger'
      return ''
    },
    getTagIcon(status) {
      if (status === 'unclaimed') return '🟡'
      if (status === 'claimed') return '🟢'
      if (status === 'conflict') return '🔴'
      return ''
    },
    getTagText(status) {
      if (status === 'unclaimed') return '未认领'
      if (status === 'claimed') return '已认领'
      if (status === 'conflict') return '待处理'
      return status || '--'
    },

    formatDate(v) {
      if (!v) return '--'
      return String(v).replace('T', ' ').slice(0, 10)
    },

    editItem(item) {
      this.editId = item.participantId
      this.editForm = {
        name: item.name || '',
        guardianMobile: item.guardianMobile || '',
        school: item.school || '',
        className: item.className || ''
      }
      this.editVisible = true
    },

    async saveEdit() {
      if (!this.editForm.name) {
        uni.showToast({ title: '请输入姓名', icon: 'none' })
        return
      }
      try {
        await updateParticipant(this.editId, this.editForm)
        uni.showToast({ title: '保存成功', icon: 'success' })
        this.editVisible = false
        this.pageNum = 1
        this.loadList()
      } catch (e) {}
    },

    confirmDelete(item) {
      uni.showModal({
        title: '确认删除',
        content: `确定删除临时档案 "${item.name}" 吗？`,
        success: async (res) => {
          if (!res.confirm) return
          try {
            await deleteParticipant(item.participantId)
            uni.showToast({ title: '已删除', icon: 'success' })
            this.pageNum = 1
            this.loadList()
          } catch (e) {}
        }
      })
    },

    viewDetail(item) {
      uni.showToast({ title: '详情页待后续实现', icon: 'none' })
    },

    goBack() { uni.navigateBack() }
  }
}
</script>

<style scoped lang="scss">
.pt-page { min-height: 100vh; padding-bottom: 48rpx; background: var(--jst-color-page-bg); }

.pt-header { display: flex; align-items: center; padding: 0 32rpx; height: 112rpx; padding-top: 88rpx; background: #fff; border-bottom: 2rpx solid var(--jst-color-border); position: sticky; top: 0; z-index: 40; }
.pt-header__back { width: 72rpx; height: 72rpx; border-radius: var(--jst-radius-sm); background: var(--jst-color-page-bg); display: flex; align-items: center; justify-content: center; font-size: 36rpx; margin-right: 24rpx; }
.pt-header__title { flex: 1; font-size: 34rpx; font-weight: 700; color: var(--jst-color-text); }

/* Tab */
.pt-tabs { display: flex; gap: 16rpx; padding: 24rpx 32rpx 0; }
.pt-tab { flex-shrink: 0; height: 64rpx; padding: 0 28rpx; border: 3rpx solid var(--jst-color-border); border-radius: var(--jst-radius-full); background: #fff; font-size: 26rpx; font-weight: 500; color: var(--jst-color-text-secondary); display: flex; align-items: center; white-space: nowrap; }
.pt-tab--active { border-color: #3F51B5; background: #EEF0FF; color: #3F51B5; font-weight: 700; }

/* 列表 */
.pt-list { padding: 24rpx 32rpx 0; }
.pt-card { background: #fff; border-radius: var(--jst-radius-lg); box-shadow: var(--jst-shadow-card); margin-bottom: 20rpx; overflow: hidden; }
.pt-card__main { display: flex; align-items: center; gap: 20rpx; padding: 24rpx 28rpx; }
.pt-card__avatar { width: 80rpx; height: 80rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.pt-card__avatar-text { font-size: 36rpx; font-weight: 700; color: #fff; }
.pt-card__info { flex: 1; min-width: 0; }
.pt-card__name { display: block; font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); }
.pt-card__meta { display: block; margin-top: 4rpx; font-size: 24rpx; color: var(--jst-color-text-tertiary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.pt-card__time { display: block; margin-top: 4rpx; font-size: 22rpx; color: #ccc; }
.pt-card__tag { flex-shrink: 0; padding: 6rpx 16rpx; border-radius: var(--jst-radius-full); font-size: 22rpx; font-weight: 600; }
.pt-card__tag--warning { background: var(--jst-color-warning-soft); color: #B26A00; }
.pt-card__tag--success { background: var(--jst-color-success-soft); color: #0F7B3F; }
.pt-card__tag--danger { background: var(--jst-color-danger-soft); color: var(--jst-color-danger); }
.pt-card__claimed { padding: 12rpx 28rpx; font-size: 24rpx; color: #0F7B3F; background: var(--jst-color-success-soft); }
.pt-card__actions { display: flex; border-top: 2rpx solid var(--jst-color-border); }
.pt-card__action { flex: 1; height: 80rpx; display: flex; align-items: center; justify-content: center; gap: 8rpx; font-size: 24rpx; font-weight: 600; color: var(--jst-color-text-secondary); border-right: 2rpx solid var(--jst-color-border); }
.pt-card__action:last-child { border-right: none; }
.pt-card__action--primary { color: #3F51B5; }
.pt-card__action--danger { color: var(--jst-color-danger); }
.pt-card__action--warning { color: #B26A00; }

.pt-loadmore { padding: 32rpx; text-align: center; font-size: 26rpx; color: #3F51B5; font-weight: 600; }
.pt-empty { text-align: center; padding: 96rpx 48rpx; }
.pt-empty__icon { display: block; font-size: 80rpx; margin-bottom: 24rpx; }
.pt-empty__title { display: block; font-size: 30rpx; font-weight: 700; color: var(--jst-color-text-secondary); }
.pt-empty__desc { display: block; margin-top: 12rpx; font-size: 26rpx; color: var(--jst-color-text-tertiary); }

/* 编辑弹窗 */
.pt-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.45); z-index: 100; display: flex; align-items: flex-end; }
.pt-edit-popup { width: 100%; background: #fff; border-radius: var(--jst-radius-lg) var(--jst-radius-lg) 0 0; padding-bottom: calc(24rpx + env(safe-area-inset-bottom)); }
.pt-edit-header { display: flex; align-items: center; justify-content: space-between; padding: 28rpx 32rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.pt-edit-title { font-size: 32rpx; font-weight: 700; color: var(--jst-color-text); }
.pt-edit-close { font-size: 36rpx; color: var(--jst-color-text-tertiary); padding: 8rpx; }
.pt-edit-body { padding: 24rpx 32rpx; }
.pt-field { display: flex; align-items: center; margin-bottom: 20rpx; }
.pt-field__label { width: 180rpx; font-size: 28rpx; color: var(--jst-color-text-secondary); flex-shrink: 0; }
.pt-field__input { flex: 1; height: 72rpx; padding: 0 20rpx; background: var(--jst-color-page-bg); border-radius: var(--jst-radius-sm); font-size: 28rpx; color: var(--jst-color-text); }
.pt-edit-footer { display: flex; gap: 20rpx; padding: 0 32rpx; }
.pt-edit-btn { flex: 1; height: 88rpx; border-radius: var(--jst-radius-md); display: flex; align-items: center; justify-content: center; font-size: 28rpx; font-weight: 600; }
.pt-edit-btn--ghost { background: var(--jst-color-page-bg); color: var(--jst-color-text-secondary); }
.pt-edit-btn--primary { background: #3F51B5; color: #fff; }
</style>
