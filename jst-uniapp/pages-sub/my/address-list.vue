<template>
  <view class="address-page">
    <jst-loading :loading="pageLoading" text="地址加载中..." />

    <view class="address-page__header">
      <view class="address-page__back" @tap="handleBack"><</view>
      <text class="address-page__title">{{ pickMode ? '选择收货地址' : '收货地址' }}</text>
      <text class="address-page__header-btn" @tap="goCreate">新增</text>
    </view>

    <view class="address-page__body">
      <view v-if="pickMode" class="address-page__tip">
        选择地址后将自动返回商品页并填充收货信息。
      </view>

      <view
        v-for="item in addresses"
        :key="item.addressId"
        class="address-page__card"
        @tap="handlePick(item)"
      >
        <view class="address-page__card-top">
          <view class="address-page__identity">
            <text class="address-page__name">{{ item.receiverName }}</text>
            <text class="address-page__mobile">{{ item.receiverMobileMasked || maskMobile(item.receiverMobile) }}</text>
          </view>
          <text v-if="item.isDefault === 1" class="address-page__badge">默认</text>
        </view>

        <text class="address-page__detail">{{ formatAddress(item) }}</text>

        <view class="address-page__actions">
          <text class="address-page__action" @tap.stop="goEdit(item.addressId)">编辑</text>
          <text
            v-if="item.isDefault !== 1"
            class="address-page__action address-page__action--primary"
            @tap.stop="handleSetDefault(item)"
          >
            设为默认
          </text>
          <text class="address-page__action address-page__action--danger" @tap.stop="confirmDelete(item)">
            删除
          </text>
        </view>
      </view>

      <jst-empty
        v-if="!pageLoading && !addresses.length"
        icon="📦"
        text="还没有收货地址，先新增一条吧"
      />
    </view>

    <view class="address-page__footer">
      <button class="address-page__submit" @tap="goCreate">新增收货地址</button>
    </view>
  </view>
</template>

<script>
import { deleteAddress, getAddressList, setDefaultAddress } from '@/api/address'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'

export default {
  components: {
    JstEmpty,
    JstLoading
  },
  data() {
    return {
      pageLoading: false,
      pickMode: false,
      addresses: []
    }
  },
  onLoad(query) {
    this.pickMode = query && query.mode === 'pick'
  },
  onShow() {
    this.loadList()
  },
  methods: {
    async loadList() {
      this.pageLoading = true
      try {
        const list = await getAddressList({ silent: true })
        this.addresses = Array.isArray(list) ? list : []
      } catch (error) {
        this.addresses = []
      } finally {
        this.pageLoading = false
      }
    },
    formatAddress(item) {
      return [item.province, item.city, item.district, item.addressDetail].filter(Boolean).join(' ')
    },
    maskMobile(value) {
      if (!value || value.length < 7) return value || ''
      return `${value.slice(0, 3)}****${value.slice(-4)}`
    },
    handlePick(item) {
      if (!this.pickMode) return
      uni.setStorageSync('ua_picked_address', item)
      uni.navigateBack()
    },
    goCreate() {
      uni.navigateTo({ url: '/pages-sub/my/address-edit' })
    },
    goEdit(addressId) {
      uni.navigateTo({ url: `/pages-sub/my/address-edit?id=${addressId}` })
    },
    handleSetDefault(item) {
      uni.showLoading({ title: '设置中...' })
      setDefaultAddress(item.addressId, { silent: true })
        .then(() => {
          uni.showToast({ title: '已设为默认', icon: 'success' })
          this.loadList()
        })
        .finally(() => {
          uni.hideLoading()
        })
    },
    confirmDelete(item) {
      uni.showModal({
        title: '删除地址',
        content: `确认删除“${item.receiverName}”的收货地址吗？`,
        success: async (res) => {
          if (!res.confirm) return
          try {
            await deleteAddress(item.addressId, { silent: true })
            uni.showToast({ title: '已删除', icon: 'success' })
            this.loadList()
          } catch (error) {
            uni.showToast({ title: '删除失败', icon: 'none' })
          }
        }
      })
    },
    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }
      uni.switchTab({ url: '/pages/my/index' })
    }
  }
}
</script>

<style scoped lang="scss">
.address-page {
  min-height: 100vh;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
  background: var(--jst-color-page-bg);
}

.address-page__header {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 24rpx;
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
}

.address-page__back,
.address-page__header-btn {
  width: 88rpx;
  font-size: 28rpx;
  color: var(--jst-color-brand);
}

.address-page__back {
  font-size: 34rpx;
  color: var(--jst-color-text-secondary);
}

.address-page__title {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  font-weight: 700;
  color: var(--jst-color-text);
}

.address-page__body {
  padding: 20rpx 24rpx 0;
}

.address-page__tip {
  margin-bottom: 20rpx;
  padding: 18rpx 20rpx;
  border-radius: var(--jst-radius-md);
  background: var(--jst-color-primary-soft);
  font-size: 22rpx;
  line-height: 1.7;
  color: var(--jst-color-text-secondary);
}

.address-page__card {
  margin-bottom: 20rpx;
  padding: 24rpx;
  border-radius: var(--jst-radius-lg);
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
}

.address-page__card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.address-page__identity {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16rpx;
}

.address-page__name {
  font-size: 30rpx;
  font-weight: 700;
  color: var(--jst-color-text);
}

.address-page__mobile {
  font-size: 24rpx;
  color: var(--jst-color-text-secondary);
}

.address-page__badge {
  padding: 6rpx 14rpx;
  border-radius: 999rpx;
  background: var(--jst-color-brand-soft);
  font-size: 20rpx;
  font-weight: 700;
  color: var(--jst-color-brand);
}

.address-page__detail {
  display: block;
  margin-top: 18rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: var(--jst-color-text-secondary);
}

.address-page__actions {
  display: flex;
  gap: 28rpx;
  margin-top: 22rpx;
  padding-top: 20rpx;
  border-top: 2rpx solid var(--jst-color-border);
}

.address-page__action {
  font-size: 24rpx;
  color: var(--jst-color-text-secondary);
}

.address-page__action--primary {
  color: var(--jst-color-brand);
  font-weight: 700;
}

.address-page__action--danger {
  color: var(--jst-color-danger);
}

.address-page__footer {
  position: fixed;
  right: 0;
  bottom: 0;
  left: 0;
  padding: 16rpx 24rpx calc(16rpx + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 -8rpx 18rpx rgba(15, 52, 96, 0.08);
}

.address-page__submit {
  height: 88rpx;
  border-radius: var(--jst-radius-md);
  background: linear-gradient(135deg, var(--jst-color-primary) 0%, var(--jst-color-primary-light) 100%);
  color: var(--jst-color-card-bg);
  font-size: 28rpx;
  font-weight: 700;
}
</style>
