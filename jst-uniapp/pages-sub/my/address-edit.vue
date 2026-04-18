<template>
  <view class="address-edit-page">
    <jst-loading :loading="pageLoading" text="地址加载中..." />
    <u-skeleton v-if="pageLoading" :loading="true" :rows="8" title :avatar="false" class="jst-page-skeleton" />

    <view class="address-edit-page__header">
      <view class="address-edit-page__back" @tap="handleBack"> 返回 </view>
      <text class="address-edit-page__title">{{ addressId ? '编辑地址' : '新增地址' }}</text>
      <u-button class="address-edit-page__save" size="mini" shape="circle" plain @click="submitForm">保存</u-button>
    </view>

    <view class="address-edit-page__section">
      <view class="address-edit-page__row">
        <text class="address-edit-page__label">收货人</text>
        <input
          v-model="form.receiverName"
          class="address-edit-page__input"
          maxlength="50"
          placeholder="请输入收货人姓名"
          placeholder-class="address-edit-page__placeholder"
        />
      </view>

      <view class="address-edit-page__row">
        <text class="address-edit-page__label">手机号</text>
        <input
          v-model="form.receiverMobile"
          class="address-edit-page__input"
          maxlength="11"
          type="number"
          placeholder="请输入收货人手机号"
          placeholder-class="address-edit-page__placeholder"
        />
      </view>
    </view>

    <view class="address-edit-page__section">
      <picker mode="selector" :range="provinceLabels" :value="provinceIndex" @change="handleProvinceChange">
        <view class="address-edit-page__row address-edit-page__row--picker">
          <text class="address-edit-page__label">省份</text>
          <text class="address-edit-page__value">{{ form.province }}</text>
          <u-icon name="arrow-right" class="address-edit-page__arrow" size="22" />
        </view>
      </picker>

      <picker mode="selector" :range="cityLabels" :value="cityIndex" @change="handleCityChange">
        <view class="address-edit-page__row address-edit-page__row--picker">
          <text class="address-edit-page__label">城市</text>
          <text class="address-edit-page__value">{{ form.city }}</text>
          <u-icon name="arrow-right" class="address-edit-page__arrow" size="22" />
        </view>
      </picker>

      <picker mode="selector" :range="districtLabels" :value="districtIndex" @change="handleDistrictChange">
        <view class="address-edit-page__row address-edit-page__row--picker">
          <text class="address-edit-page__label">区县</text>
          <text class="address-edit-page__value">{{ form.district }}</text>
          <u-icon name="arrow-right" class="address-edit-page__arrow" size="22" />
        </view>
      </picker>

      <view class="address-edit-page__row address-edit-page__row--textarea">
        <text class="address-edit-page__label">详细地址</text>
        <textarea
          v-model="form.addressDetail"
          class="address-edit-page__textarea"
          maxlength="200"
          placeholder="请输入街道、楼栋门牌等详细信息"
          placeholder-class="address-edit-page__placeholder"
        />
      </view>

      <view class="address-edit-page__row">
        <text class="address-edit-page__label">邮编</text>
        <input
          v-model="form.postalCode"
          class="address-edit-page__input"
          maxlength="10"
          placeholder="选填"
          placeholder-class="address-edit-page__placeholder"
        />
      </view>
    </view>

    <view class="address-edit-page__section">
      <view class="address-edit-page__row address-edit-page__row--switch">
        <view>
          <text class="address-edit-page__switch-title">设为默认地址</text>
          <text class="address-edit-page__switch-desc">商城兑换实物商品时将优先使用</text>
        </view>
        <switch :checked="form.isDefault === 1" color="var(--jst-warning)" @change="handleDefaultChange" />
      </view>
    </view>

    <view class="address-edit-page__footer">
      <u-button class="address-edit-page__submit" :loading="submitting" @click="submitForm">保存地址</u-button>
    </view>
  </view>
</template>

<script>
import { createAddress, getAddressDetail, updateAddress } from '@/api/address'
import JstLoading from '@/components/jst-loading/jst-loading.vue'

const REGION_OPTIONS = [
  {
    name: '北京市',
    cities: [
      { name: '北京市', districts: ['朝阳区', '海淀区', '丰台区'] }
    ]
  },
  {
    name: '上海市',
    cities: [
      { name: '上海市', districts: ['浦东新区', '徐汇区', '闵行区'] }
    ]
  },
  {
    name: '广东省',
    cities: [
      { name: '广州市', districts: ['天河区', '越秀区', '海珠区'] },
      { name: '深圳市', districts: ['南山区', '福田区', '宝安区'] }
    ]
  }
]

export default {
  components: { JstLoading },
  data() {
    return {
      pageLoading: false,
      submitting: false,
      addressId: null,
      provinceIndex: 0,
      cityIndex: 0,
      districtIndex: 0,
      form: {
        receiverName: '',
        receiverMobile: '',
        province: '',
        city: '',
        district: '',
        addressDetail: '',
        postalCode: '',
        isDefault: 0
      }
    }
  },
  computed: {
    provinceLabels() {
      return REGION_OPTIONS.map((item) => item.name)
    },
    cityOptions() {
      return (REGION_OPTIONS[this.provinceIndex] && REGION_OPTIONS[this.provinceIndex].cities) || []
    },
    cityLabels() {
      return this.cityOptions.map((item) => item.name)
    },
    districtOptions() {
      return (this.cityOptions[this.cityIndex] && this.cityOptions[this.cityIndex].districts) || []
    },
    districtLabels() {
      return this.districtOptions
    }
  },
  onLoad(query) {
    this.addressId = query && query.id ? Number(query.id) : null
    if (this.addressId) {
      this.loadDetail()
      return
    }
    this.syncRegionByIndex()
  },
  methods: {
    async loadDetail() {
      this.pageLoading = true
      try {
        const detail = await getAddressDetail(this.addressId, { silent: true })
        this.form.receiverName = detail.receiverName || ''
        this.form.receiverMobile = detail.receiverMobile || ''
        this.form.addressDetail = detail.addressDetail || ''
        this.form.postalCode = detail.postalCode || ''
        this.form.isDefault = detail.isDefault === 1 ? 1 : 0
        this.setRegionByValue(detail.province, detail.city, detail.district)
      } catch (error) {
        uni.showToast({ title: '加载失败，请重试', icon: 'none' })
      } finally {
        this.pageLoading = false
      }
    },
    setRegionByValue(province, city, district) {
      const provinceIdx = REGION_OPTIONS.findIndex((item) => item.name === province)
      this.provinceIndex = provinceIdx >= 0 ? provinceIdx : 0
      const cities = REGION_OPTIONS[this.provinceIndex].cities || []
      const cityIdx = cities.findIndex((item) => item.name === city)
      this.cityIndex = cityIdx >= 0 ? cityIdx : 0
      const districts = cities[this.cityIndex] ? cities[this.cityIndex].districts : []
      const districtIdx = districts.findIndex((item) => item === district)
      this.districtIndex = districtIdx >= 0 ? districtIdx : 0
      this.syncRegionByIndex()
    },
    syncRegionByIndex() {
      const province = REGION_OPTIONS[this.provinceIndex] || REGION_OPTIONS[0]
      const city = (province.cities && province.cities[this.cityIndex]) || province.cities[0]
      const district = (city.districts && city.districts[this.districtIndex]) || city.districts[0]
      this.form.province = province.name
      this.form.city = city.name
      this.form.district = district
    },
    handleProvinceChange(event) {
      this.provinceIndex = Number(event.detail.value || 0)
      this.cityIndex = 0
      this.districtIndex = 0
      this.syncRegionByIndex()
    },
    handleCityChange(event) {
      this.cityIndex = Number(event.detail.value || 0)
      this.districtIndex = 0
      this.syncRegionByIndex()
    },
    handleDistrictChange(event) {
      this.districtIndex = Number(event.detail.value || 0)
      this.syncRegionByIndex()
    },
    handleDefaultChange(event) {
      this.form.isDefault = event.detail.value ? 1 : 0
    },
    async submitForm() {
      if (this.submitting) return

      const payload = {
        receiverName: `${this.form.receiverName || ''}`.trim(),
        receiverMobile: `${this.form.receiverMobile || ''}`.trim(),
        province: this.form.province,
        city: this.form.city,
        district: this.form.district,
        addressDetail: `${this.form.addressDetail || ''}`.trim(),
        postalCode: `${this.form.postalCode || ''}`.trim(),
        isDefault: this.form.isDefault
      }

      if (!payload.receiverName) {
        uni.showToast({ title: '请输入收货人姓名', icon: 'none' })
        return
      }
      if (!/^1[3-9]\d{9}$/.test(payload.receiverMobile)) {
        uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
        return
      }
      if (!payload.addressDetail) {
        uni.showToast({ title: '请输入详细地址', icon: 'none' })
        return
      }

      this.submitting = true
      try {
        if (this.addressId) {
          await updateAddress(this.addressId, payload, { silent: true })
        } else {
          await createAddress(payload, { silent: true })
        }
        uni.showToast({ title: '保存成功', icon: 'success' })
        setTimeout(() => uni.navigateBack(), 350)
      } catch (error) {
        uni.showToast({ title: '保存失败', icon: 'none' })
      } finally {
        this.submitting = false
      }
    },
    handleBack() {
      uni.navigateBack()
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.address-edit-page {
  min-height: 100vh;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
  background: $jst-bg-page;
}

.jst-page-skeleton {
  margin: $jst-space-lg $jst-page-padding 0;
}

.address-edit-page__header {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 $jst-page-padding;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.address-edit-page__back {
  width: 88rpx;
  font-size: 34rpx;
  color: $jst-text-secondary;
}

.address-edit-page__title {
  flex: 1;
  text-align: center;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.address-edit-page__section {
  margin-top: $jst-space-lg;
  padding: 0 $jst-page-padding;
  background: $jst-bg-card;
}

.address-edit-page__row {
  display: flex;
  align-items: center;
  min-height: 92rpx;
  gap: $jst-space-sm;
  border-bottom: 2rpx solid $jst-border;
}

.address-edit-page__row:last-child {
  border-bottom: none;
}

.address-edit-page__row--textarea {
  align-items: flex-start;
  padding: $jst-space-lg 0;
}

.address-edit-page__row--switch {
  justify-content: space-between;
  padding: $jst-space-xs 0;
}

.address-edit-page__label {
  width: 180rpx;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.address-edit-page__input,
.address-edit-page__value {
  flex: 1;
  text-align: right;
  font-size: $jst-font-sm;
  color: $jst-text-primary;
}

.address-edit-page__textarea {
  flex: 1;
  height: 180rpx;
  font-size: $jst-font-sm;
  line-height: 1.7;
  color: $jst-text-primary;
}

.address-edit-page__placeholder {
  color: $jst-text-placeholder;
}

.address-edit-page__switch-title {
  display: block;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

.address-edit-page__switch-desc {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-xs;
  color: $jst-text-placeholder;
}

.address-edit-page__footer {
  position: fixed;
  right: 0;
  bottom: 0;
  left: 0;
  padding: 16rpx $jst-page-padding calc(16rpx + env(safe-area-inset-bottom));
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.address-edit-page__submit {
  height: 88rpx;
  border-radius: $jst-radius-md;
  background: $jst-brand-gradient;
  color: $jst-text-inverse;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
}

.address-edit-page__arrow {
  flex-shrink: 0;
}

::v-deep .address-edit-page__arrow .u-icon__icon {
  color: $jst-text-placeholder;
}

::v-deep .address-edit-page__save.u-button {
  min-width: 118rpx;
  height: 56rpx;
  border-color: $jst-brand;
  border-radius: $jst-radius-round;
  color: $jst-brand;
  font-size: $jst-font-sm;
}

::v-deep .address-edit-page__submit.u-button {
  min-height: 88rpx;
  border: none;
}
</style>
