<template>
  <view class="address-edit-page">
    <jst-loading :loading="pageLoading" text="地址加载中..." />

    <view class="address-edit-page__header">
      <view class="address-edit-page__back" @tap="handleBack"><</view>
      <text class="address-edit-page__title">{{ addressId ? '编辑地址' : '新增地址' }}</text>
      <text class="address-edit-page__save" @tap="submitForm">保存</text>
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
        </view>
      </picker>

      <picker mode="selector" :range="cityLabels" :value="cityIndex" @change="handleCityChange">
        <view class="address-edit-page__row address-edit-page__row--picker">
          <text class="address-edit-page__label">城市</text>
          <text class="address-edit-page__value">{{ form.city }}</text>
        </view>
      </picker>

      <picker mode="selector" :range="districtLabels" :value="districtIndex" @change="handleDistrictChange">
        <view class="address-edit-page__row address-edit-page__row--picker">
          <text class="address-edit-page__label">区县</text>
          <text class="address-edit-page__value">{{ form.district }}</text>
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
        <switch :checked="form.isDefault === 1" color="#ff8a35" @change="handleDefaultChange" />
      </view>
    </view>

    <view class="address-edit-page__footer">
      <button class="address-edit-page__submit" :loading="submitting" @tap="submitForm">保存地址</button>
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
.address-edit-page {
  min-height: 100vh;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
  background: var(--jst-color-page-bg);
}

.address-edit-page__header {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 24rpx;
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
}

.address-edit-page__back,
.address-edit-page__save {
  width: 88rpx;
  font-size: 28rpx;
}

.address-edit-page__back {
  font-size: 34rpx;
  color: var(--jst-color-text-secondary);
}

.address-edit-page__save {
  color: var(--jst-color-brand);
  text-align: right;
}

.address-edit-page__title {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  font-weight: 700;
  color: var(--jst-color-text);
}

.address-edit-page__section {
  margin-top: 20rpx;
  padding: 0 24rpx;
  background: var(--jst-color-card-bg);
}

.address-edit-page__row {
  display: flex;
  align-items: center;
  min-height: 92rpx;
  border-bottom: 2rpx solid var(--jst-color-border);
}

.address-edit-page__row:last-child {
  border-bottom: none;
}

.address-edit-page__row--picker::after {
  content: '>';
  margin-left: 12rpx;
  color: var(--jst-color-text-tertiary);
}

.address-edit-page__row--textarea {
  align-items: flex-start;
  padding: 24rpx 0;
}

.address-edit-page__row--switch {
  justify-content: space-between;
  padding: 8rpx 0;
}

.address-edit-page__label {
  width: 180rpx;
  font-size: 24rpx;
  color: var(--jst-color-text-secondary);
}

.address-edit-page__input,
.address-edit-page__value {
  flex: 1;
  text-align: right;
  font-size: 24rpx;
  color: var(--jst-color-text);
}

.address-edit-page__textarea {
  flex: 1;
  height: 180rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: var(--jst-color-text);
}

.address-edit-page__placeholder {
  color: var(--jst-color-text-tertiary);
}

.address-edit-page__switch-title {
  display: block;
  font-size: 26rpx;
  font-weight: 700;
  color: var(--jst-color-text);
}

.address-edit-page__switch-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: var(--jst-color-text-tertiary);
}

.address-edit-page__footer {
  position: fixed;
  right: 0;
  bottom: 0;
  left: 0;
  padding: 16rpx 24rpx calc(16rpx + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 -8rpx 18rpx rgba(15, 52, 96, 0.08);
}

.address-edit-page__submit {
  height: 88rpx;
  border-radius: var(--jst-radius-md);
  background: linear-gradient(135deg, var(--jst-color-primary) 0%, var(--jst-color-primary-light) 100%);
  color: var(--jst-color-card-bg);
  font-size: 28rpx;
  font-weight: 700;
}
</style>
