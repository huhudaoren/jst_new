<template>
  <view class="address-page">
    <jst-loading :loading="pageLoading" text="地址加载中..." />
    <u-skeleton v-if="pageLoading" :loading="true" :rows="7" title :avatar="false" class="jst-page-skeleton" />

    <view class="address-page__header">
      <view class="address-page__back" @tap="handleBack">返回</view>
      <text class="address-page__title">{{ pickMode ? '选择收货地址' : '收货地址' }}</text>
      <u-button class="address-page__header-btn" size="mini" plain @click="goCreate">新增</u-button>
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
          <u-tag v-if="item.isDefault === 1" class="address-page__badge" text="默认" type="primary" plain size="mini" />
        </view>

        <text class="address-page__detail">{{ formatAddress(item) }}</text>

        <view class="address-page__actions">
          <u-button class="address-page__action" plain @click.stop="goEdit(item.addressId)">编辑</u-button>
          <u-button v-if="item.isDefault !== 1" class="address-page__action address-page__action--primary" plain @click.stop="handleSetDefault(item)">设为默认</u-button>
          <u-button class="address-page__action address-page__action--danger" plain @click.stop="confirmDelete(item)">
            删除
          </u-button>
        </view>
      </view>

      <jst-empty
        v-if="!pageLoading && !addresses.length"
        icon="📦"
        text="还没有收货地址，先新增一条吧"
      />
    </view>

    <view class="address-page__footer">
      <u-button class="address-page__submit" @click="goCreate">新增收货地址</u-button>
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
        uni.showToast({ title: '加载失败，请重试', icon: 'none' })
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
@import '@/styles/design-tokens.scss';

.address-page {
  min-height: 100vh;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
  background: $jst-bg-page;
}

.jst-page-skeleton {
  margin: $jst-space-lg $jst-page-padding 0;
}

.address-page__header {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 $jst-page-padding;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.address-page__back {
  width: 88rpx;
  font-size: 34rpx;
  color: $jst-text-secondary;
}

.address-page__title {
  flex: 1;
  text-align: center;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.address-page__body {
  padding: $jst-space-lg $jst-page-padding 0;
}

.address-page__tip {
  margin-bottom: $jst-space-lg;
  padding: 18rpx 20rpx;
  border-radius: $jst-radius-md;
  background: $jst-brand-light;
  font-size: $jst-font-xs;
  line-height: 1.7;
  color: $jst-text-secondary;
}

.address-page__card {
  margin-bottom: $jst-space-lg;
  padding: $jst-space-lg;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
}

.address-page__card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $jst-space-md;
}

.address-page__identity {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: $jst-space-md;
}

.address-page__name {
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.address-page__mobile {
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.address-page__detail {
  display: block;
  margin-top: 18rpx;
  font-size: $jst-font-sm;
  line-height: 1.7;
  color: $jst-text-secondary;
}

.address-page__actions {
  display: flex;
  gap: $jst-space-sm;
  margin-top: 22rpx;
  padding-top: $jst-space-md;
  border-top: 2rpx solid $jst-border;
}

.address-page__action {
  min-height: 58rpx;
  padding: 0 $jst-space-sm;
  border-radius: $jst-radius-md;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.address-page__footer {
  position: fixed;
  right: 0;
  bottom: 0;
  left: 0;
  padding: 16rpx $jst-page-padding calc(16rpx + env(safe-area-inset-bottom));
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.address-page__submit {
  height: 88rpx;
  border-radius: $jst-radius-md;
  background: $jst-brand-gradient;
  color: $jst-text-inverse;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
}

::v-deep .address-page__header-btn.u-button {
  min-width: 98rpx;
  height: 56rpx;
  border-color: $jst-brand;
  border-radius: $jst-radius-round;
  color: $jst-brand;
  font-size: $jst-font-sm;
}

::v-deep .address-page__badge .u-tag {
  border-color: $jst-brand;
  color: $jst-brand;
}

::v-deep .address-page__action.u-button {
  border-color: $jst-border;
  background: $jst-bg-card;
}

::v-deep .address-page__action--primary.u-button {
  border-color: $jst-brand;
  color: $jst-brand;
}

::v-deep .address-page__action--danger.u-button {
  border-color: $jst-danger;
  color: $jst-danger;
}

::v-deep .address-page__submit.u-button {
  border: none;
  min-height: 88rpx;
}
</style>
