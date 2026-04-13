<!-- 中文注释: 渠道方 · 临时档案管理 (E1-CH-7 Part B)
     对应原型: 无独立 PNG (参考 channel-students.png 视觉风格)
     功能: 临时档案列表 + 3 状态(unclaimed/claimed/conflict) + 编辑/删除/查看
     数据来源: GET /jst/wx/channel/participant/my + DELETE /PUT -->
<template>
  <view class="pt-page">
    <!-- 导航 -->
    <view class="pt-header" :style="{ paddingTop: navPaddingTop }">
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
          <u-tag
            :text="getTagText(item.claimStatus)"
            :type="getTagType(item.claimStatus) === 'danger' ? 'error' : getTagType(item.claimStatus)"
            size="mini"
            plain
            shape="circle"
          ></u-tag>
        </view>

        <!-- claimed 显示关联用户 -->
        <view v-if="item.claimStatus === 'claimed' && item.claimedUserName" class="pt-card__claimed">
          <text>已认领用户: {{ item.claimedUserName }}</text>
        </view>

        <!-- 操作区 -->
        <view class="pt-card__actions">
          <template v-if="item.claimStatus === 'unclaimed'">
            <u-button text="编辑" size="mini" type="primary" plain @click="editItem(item)"></u-button>
            <u-button text="删除" size="mini" type="error" plain @click="confirmDelete(item)"></u-button>
          </template>
          <template v-else-if="item.claimStatus === 'claimed'">
            <u-button text="查看详情" size="mini" plain @click="viewDetail(item)"></u-button>
          </template>
          <template v-else-if="item.claimStatus === 'conflict'">
            <u-button text="查看冲突" size="mini" type="warning" plain @click="viewDetail(item)"></u-button>
          </template>
        </view>
      </view>

      <u-loadmore v-if="filteredList.length" :status="loading ? 'loading' : (hasMore ? 'loadmore' : 'nomore')" @loadmore="loadMore"></u-loadmore>

      <u-empty v-if="!filteredList.length && !loading" mode="data" text="暂无临时档案"></u-empty>
    </view>

    <!-- 编辑弹窗 -->
    <u-popup :show="editVisible" mode="bottom" :round="16" @close="editVisible = false" @open="editVisible = true">
      <view class="pt-edit-popup">
        <view class="pt-edit-header">
          <text class="pt-edit-title">编辑临时档案</text>
          <text class="pt-edit-close" @tap="editVisible = false">✕</text>
        </view>
        <view class="pt-edit-body">
          <view class="pt-field">
            <text class="pt-field__label">姓名</text>
            <u--input v-model="editForm.name" placeholder="请输入姓名" border="surround"></u--input>
          </view>
          <view class="pt-field">
            <text class="pt-field__label">监护人手机</text>
            <u--input v-model="editForm.guardianMobile" placeholder="请输入手机号" type="number" maxlength="11" border="surround"></u--input>
          </view>
          <view class="pt-field">
            <text class="pt-field__label">学校/机构</text>
            <u--input v-model="editForm.school" placeholder="请输入学校" border="surround"></u--input>
          </view>
          <view class="pt-field">
            <text class="pt-field__label">班级</text>
            <u--input v-model="editForm.className" placeholder="请输入班级" border="surround"></u--input>
          </view>
        </view>
        <view class="pt-edit-footer">
          <u-button text="取消" shape="circle" @click="editVisible = false"></u-button>
          <u-button text="保存" type="primary" shape="circle" @click="saveEdit"></u-button>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script>
import { getMyParticipants, updateParticipant, deleteParticipant } from '@/api/channel'

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
      uni.showToast({ title: '暂不支持查看详情', icon: 'none' })
    },

    goBack() { uni.navigateBack() }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.pt-page { min-height: 100vh; padding-bottom: $jst-space-xxl; background: $jst-bg-page; }

.pt-header { display: flex; align-items: center; padding: 0 32rpx; height: 112rpx; padding-top: 88rpx; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; position: sticky; top: 0; z-index: 40; }
.pt-header__back { width: 72rpx; height: 72rpx; border-radius: $jst-radius-sm; background: $jst-bg-page; display: flex; align-items: center; justify-content: center; font-size: 36rpx; margin-right: 24rpx; }
.pt-header__title { flex: 1; font-size: 34rpx; font-weight: 600; color: $jst-text-primary; }

/* Tab */
.pt-tabs { display: flex; gap: 16rpx; padding: 24rpx 32rpx 0; }
.pt-tab { flex-shrink: 0; height: 64rpx; padding: 0 28rpx; border: 3rpx solid $jst-border; border-radius: $jst-radius-round; background: $jst-bg-card; font-size: 26rpx; font-weight: 500; color: $jst-text-regular; display: flex; align-items: center; white-space: nowrap; }
.pt-tab--active { border-color: $jst-brand; background: $jst-brand-light; color: $jst-brand; font-weight: 600; }

/* 列表 */
.pt-list { padding: 24rpx 32rpx 0; }
.pt-card { background: $jst-bg-card; border-radius: $jst-radius-lg; box-shadow: $jst-shadow-sm; margin-bottom: 20rpx; overflow: hidden; }
.pt-card__main { display: flex; align-items: center; gap: 20rpx; padding: 24rpx 28rpx; }
.pt-card__avatar { width: 80rpx; height: 80rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.pt-card__avatar-text { font-size: 36rpx; font-weight: 600; color: $jst-text-inverse; }
.pt-card__info { flex: 1; min-width: 0; }
.pt-card__name { display: block; font-size: 28rpx; font-weight: 600; color: $jst-text-primary; }
.pt-card__meta { display: block; margin-top: 4rpx; font-size: 24rpx; color: $jst-text-secondary; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.pt-card__time { display: block; margin-top: 4rpx; font-size: 22rpx; color: $jst-text-placeholder; }
.pt-card__claimed { padding: 12rpx 28rpx; font-size: 24rpx; color: $jst-success; background: $jst-success-light; }
.pt-card__actions { display: grid; grid-template-columns: 1fr 1fr; gap: $jst-space-sm; padding: $jst-space-md $jst-space-lg; border-top: 2rpx solid $jst-border; }

/* 编辑弹窗 */
.pt-edit-popup { width: 100%; background: $jst-bg-card; border-radius: $jst-radius-lg $jst-radius-lg 0 0; padding-bottom: calc(24rpx + env(safe-area-inset-bottom)); }
.pt-edit-header { display: flex; align-items: center; justify-content: space-between; padding: 28rpx 32rpx; border-bottom: 2rpx solid $jst-border; }
.pt-edit-title { font-size: 32rpx; font-weight: 600; color: $jst-text-primary; }
.pt-edit-close { font-size: 36rpx; color: $jst-text-secondary; padding: 8rpx; }
.pt-edit-body { padding: 24rpx 32rpx; }
.pt-field { display: flex; align-items: center; margin-bottom: 20rpx; }
.pt-field__label { width: 180rpx; font-size: 28rpx; color: $jst-text-regular; flex-shrink: 0; }
.pt-edit-footer { display: flex; gap: 20rpx; padding: 0 32rpx; }
::v-deep .pt-card__actions .u-button { height: 72rpx; }
::v-deep .pt-field .u-input { flex: 1; }
::v-deep .pt-edit-footer .u-button { flex: 1; height: 88rpx; }
</style>
