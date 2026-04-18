<!-- 中文注释: 发票抬头管理 (Round 2B · B3)
     后端: /jst/wx/user/invoice-title/* (分支 feat/backend-round-2b-b1-b3)
     视觉: 参考 address-list.vue 卡片风格 + 默认徽章 $jst-gold
     新增/编辑: 使用 u-popup 弹层, 避免多一层页面跳转
-->
<template>
  <view class="invt-page">
    <jst-loading :loading="pageLoading" text="抬头加载中..." />
    <u-skeleton v-if="pageLoading" :loading="true" :rows="6" title :avatar="false" class="invt-page__skeleton" />

    <!-- Hero -->
    <view class="invt-page__header">
      <view class="invt-page__back" @tap="handleBack">返回</view>
      <text class="invt-page__title">发票抬头</text>
      <u-button class="invt-page__header-btn" size="mini" plain @click="openCreate">新增</u-button>
    </view>

    <view class="invt-page__body">
      <view
        v-for="item in list"
        :key="item.titleId"
        class="invt-page__card"
      >
        <view class="invt-page__card-top">
          <view class="invt-page__identity">
            <view
              class="invt-page__chip"
              :class="item.titleType === 'company' ? 'invt-page__chip--company' : 'invt-page__chip--personal'"
            >
              <text>{{ item.titleType === 'company' ? '企业' : '个人' }}</text>
            </view>
            <text class="invt-page__name">{{ item.titleName }}</text>
          </view>
          <view v-if="item.isDefault === 1" class="invt-page__badge">
            <text>默认</text>
          </view>
        </view>

        <view v-if="item.titleType === 'company' && item.taxNo" class="invt-page__taxno">
          <text class="invt-page__taxno-label">税号</text>
          <text class="invt-page__taxno-value">{{ item.taxNo }}</text>
        </view>

        <view class="invt-page__actions">
          <u-button
            class="invt-page__action"
            :class="{ 'invt-page__action--disabled': item.isDefault === 1 }"
            plain
            :disabled="item.isDefault === 1"
            @click.stop="handleSetDefault(item)"
          >{{ item.isDefault === 1 ? '已默认' : '设默认' }}</u-button>
          <u-button class="invt-page__action invt-page__action--primary" plain @click.stop="openEdit(item)">编辑</u-button>
          <u-button class="invt-page__action invt-page__action--danger" plain @click.stop="confirmDelete(item)">删除</u-button>
        </view>
      </view>

      <jst-empty
        v-if="!pageLoading && !list.length"
        illustration="records"
        text="还没有发票抬头，添加一个方便下次开票"
        button-text="添加发票抬头"
        @action="openCreate"
      />
    </view>

    <!-- 底部固定新增 -->
    <view v-if="list.length" class="invt-page__footer">
      <u-button class="invt-page__submit" @click="openCreate">+ 添加新抬头</u-button>
    </view>

    <!-- 新增/编辑弹层 -->
    <u-popup v-model="formVisible" mode="bottom" border-radius="24" closeable>
      <view class="invt-form">
        <view class="invt-form__title">
          <text>{{ editing && editing.titleId ? '编辑发票抬头' : '添加发票抬头' }}</text>
        </view>

        <!-- 类型 radio -->
        <view class="invt-form__row">
          <text class="invt-form__label">抬头类型</text>
          <u-radio-group v-model="form.titleType" class="invt-form__radios" @change="onTypeChange">
            <u-radio name="personal" :active-color="brandColor">个人</u-radio>
            <u-radio name="company" :active-color="brandColor">企业</u-radio>
          </u-radio-group>
        </view>

        <!-- 抬头名称 -->
        <view class="invt-form__row invt-form__row--col">
          <text class="invt-form__label">{{ form.titleType === 'company' ? '企业全称' : '个人姓名' }} <text class="invt-form__required">*</text></text>
          <u-input
            v-model="form.titleName"
            class="invt-form__input"
            :placeholder="form.titleType === 'company' ? '请输入企业全称' : '请输入个人姓名'"
            :maxlength="128"
            border="bottom"
          />
        </view>

        <!-- 税号 (仅企业) -->
        <view v-if="form.titleType === 'company'" class="invt-form__row invt-form__row--col">
          <text class="invt-form__label">统一社会信用代码 <text class="invt-form__required">*</text></text>
          <u-input
            v-model="form.taxNo"
            class="invt-form__input"
            placeholder="15-20 位数字字母"
            :maxlength="20"
            border="bottom"
            @blur="onTaxNoBlur"
          />
          <text class="invt-form__hint">仅支持大写字母和数字，15-20 位</text>
        </view>

        <!-- 设为默认 -->
        <view class="invt-form__row">
          <text class="invt-form__label">设为默认抬头</text>
          <u-switch v-model="form.isDefault" :active-color="brandColor" size="18" />
        </view>

        <view class="invt-form__submit">
          <u-button class="invt-form__cancel" plain @click="formVisible = false">取消</u-button>
          <u-button class="invt-form__save" :loading="saving" @click="handleSave">保存</u-button>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script>
import {
  listInvoiceTitles,
  saveInvoiceTitle,
  deleteInvoiceTitle,
  setDefaultInvoiceTitle
} from '@/api/invoice-title'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'

// 中文注释: 统一社会信用代码格式 — 15-20 位大写字母或数字
const TAX_NO_REGEX = /^[A-Z0-9]{15,20}$/
const BRAND_COLOR = '#2B6CFF'

function emptyForm() {
  return {
    titleId: null,
    titleType: 'personal',
    titleName: '',
    taxNo: '',
    isDefault: false
  }
}

export default {
  components: { JstEmpty, JstLoading },
  data() {
    return {
      pageLoading: false,
      saving: false,
      list: [],
      formVisible: false,
      editing: null,
      form: emptyForm(),
      brandColor: BRAND_COLOR
    }
  },
  onShow() {
    this.loadList()
  },
  methods: {
    async loadList() {
      this.pageLoading = true
      try {
        const res = await listInvoiceTitles({ silent: true })
        // 后端返回有可能是数组也可能包 rows
        const arr = Array.isArray(res) ? res : (res && res.rows) || []
        this.list = arr
      } catch (error) {
        // 友好降级: 后端未合入走空态, 不 toast 打扰用户
        this.list = []
      } finally {
        this.pageLoading = false
      }
    },

    openCreate() {
      this.editing = null
      this.form = emptyForm()
      this.formVisible = true
    },

    openEdit(item) {
      this.editing = item
      this.form = {
        titleId: item.titleId,
        titleType: item.titleType || 'personal',
        titleName: item.titleName || '',
        taxNo: item.taxNo || '',
        isDefault: item.isDefault === 1
      }
      this.formVisible = true
    },

    onTypeChange(val) {
      // 切到 personal 清空 taxNo 避免校验误触发
      if (val === 'personal') this.form.taxNo = ''
    },

    onTaxNoBlur() {
      // 强制大写，去空格
      if (this.form.taxNo) this.form.taxNo = String(this.form.taxNo).trim().toUpperCase()
    },

    validateForm() {
      if (!this.form.titleName || !this.form.titleName.trim()) {
        uni.showToast({ title: '请填写抬头名称', icon: 'none' })
        return false
      }
      if (this.form.titleName.length > 128) {
        uni.showToast({ title: '抬头名称最长 128 字', icon: 'none' })
        return false
      }
      if (this.form.titleType === 'company') {
        if (!this.form.taxNo) {
          uni.showToast({ title: '企业抬头请填写税号', icon: 'none' })
          return false
        }
        const taxNo = String(this.form.taxNo).trim().toUpperCase()
        if (!TAX_NO_REGEX.test(taxNo)) {
          uni.showToast({ title: '税号格式不正确（15-20 位大写字母数字）', icon: 'none' })
          return false
        }
        this.form.taxNo = taxNo
      }
      return true
    },

    async handleSave() {
      if (!this.validateForm()) return
      this.saving = true
      try {
        const payload = {
          titleId: this.form.titleId || undefined,
          titleType: this.form.titleType,
          titleName: this.form.titleName.trim(),
          taxNo: this.form.titleType === 'company' ? this.form.taxNo : '',
          isDefault: this.form.isDefault ? 1 : 0
        }
        await saveInvoiceTitle(payload)
        uni.showToast({ title: '保存成功', icon: 'success' })
        this.formVisible = false
        this.loadList()
      } catch (error) {
        const msg = (error && (error.msg || error.message)) || '保存失败，请重试'
        uni.showToast({ title: msg, icon: 'none' })
      } finally {
        this.saving = false
      }
    },

    handleSetDefault(item) {
      if (item.isDefault === 1) return
      uni.showLoading({ title: '设置中...' })
      setDefaultInvoiceTitle(item.titleId, { silent: true })
        .then(() => {
          uni.showToast({ title: '已设为默认', icon: 'success' })
          this.loadList()
        })
        .catch((error) => {
          const msg = (error && (error.msg || error.message)) || '设置失败'
          uni.showToast({ title: msg, icon: 'none' })
        })
        .finally(() => {
          uni.hideLoading()
        })
    },

    confirmDelete(item) {
      uni.showModal({
        title: '删除抬头',
        content: `确认删除"${item.titleName}"？删除后不可恢复。`,
        confirmColor: '#FF4D4F',
        success: async (res) => {
          if (!res.confirm) return
          try {
            await deleteInvoiceTitle(item.titleId, { silent: true })
            uni.showToast({ title: '已删除', icon: 'success' })
            this.loadList()
          } catch (error) {
            const msg = (error && (error.msg || error.message)) || '删除失败'
            uni.showToast({ title: msg, icon: 'none' })
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

.invt-page {
  min-height: 100vh;
  padding-bottom: calc(140rpx + env(safe-area-inset-bottom));
  background: $jst-bg-page;
}

.invt-page__skeleton {
  margin: $jst-space-lg $jst-page-padding 0;
}

.invt-page__header {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 $jst-page-padding;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.invt-page__back {
  width: 88rpx;
  font-size: 34rpx;
  color: $jst-text-secondary;
}

.invt-page__title {
  flex: 1;
  text-align: center;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.invt-page__body {
  padding: $jst-space-lg $jst-page-padding 0;
}

.invt-page__card {
  margin-bottom: $jst-space-lg;
  padding: $jst-space-lg;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
}

.invt-page__card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $jst-space-md;
}

.invt-page__identity {
  display: flex;
  align-items: center;
  gap: $jst-space-sm;
  flex: 1;
  min-width: 0;
}

.invt-page__chip {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  padding: 4rpx 16rpx;
  border-radius: $jst-radius-round;
  font-size: $jst-font-xs;
  font-weight: $jst-weight-semibold;
  line-height: 1.6;
}

.invt-page__chip--personal {
  background: $jst-brand-light;
  color: $jst-brand;
}

.invt-page__chip--company {
  background: $jst-warning-light;
  color: $jst-warning;
}

.invt-page__name {
  flex: 1;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.invt-page__badge {
  flex-shrink: 0;
  padding: 4rpx 14rpx;
  border-radius: $jst-radius-round;
  background: $jst-gold-light;
  font-size: $jst-font-xs;
  font-weight: $jst-weight-semibold;
  line-height: 1.6;
  color: darken($jst-gold, 25%);
  border: 2rpx solid $jst-gold;
}

.invt-page__taxno {
  display: flex;
  align-items: center;
  gap: $jst-space-sm;
  margin-top: 18rpx;
  padding: 12rpx 16rpx;
  border-radius: $jst-radius-sm;
  background: $jst-bg-page;
}

.invt-page__taxno-label {
  font-size: $jst-font-sm;
  color: $jst-text-placeholder;
}

.invt-page__taxno-value {
  flex: 1;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
  letter-spacing: 1rpx;
  word-break: break-all;
}

.invt-page__actions {
  display: flex;
  gap: $jst-space-sm;
  margin-top: 22rpx;
  padding-top: $jst-space-md;
  border-top: 2rpx solid $jst-border;
}

.invt-page__action {
  flex: 1;
  min-height: 58rpx;
  padding: 0 $jst-space-sm;
  border-radius: $jst-radius-md;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.invt-page__footer {
  position: fixed;
  right: 0;
  bottom: 0;
  left: 0;
  padding: 16rpx $jst-page-padding calc(16rpx + env(safe-area-inset-bottom));
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.invt-page__submit {
  height: 88rpx;
  border-radius: $jst-radius-md;
  background: $jst-brand-gradient;
  color: $jst-text-inverse;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
}

::v-deep .invt-page__header-btn.u-button {
  min-width: 98rpx;
  height: 56rpx;
  border-color: $jst-brand;
  border-radius: $jst-radius-round;
  color: $jst-brand;
  font-size: $jst-font-sm;
}

::v-deep .invt-page__action.u-button {
  border-color: $jst-border;
  background: $jst-bg-card;
}

::v-deep .invt-page__action--primary.u-button {
  border-color: $jst-brand;
  color: $jst-brand;
}

::v-deep .invt-page__action--danger.u-button {
  border-color: $jst-danger;
  color: $jst-danger;
}

::v-deep .invt-page__action--disabled.u-button {
  border-color: $jst-border;
  color: $jst-text-placeholder;
  background: $jst-bg-page;
}

::v-deep .invt-page__submit.u-button {
  border: none;
  min-height: 88rpx;
}

/* 表单弹层 */
.invt-form {
  padding: $jst-space-xl $jst-page-padding calc(#{$jst-space-xl} + env(safe-area-inset-bottom));
  background: $jst-bg-card;
}

.invt-form__title {
  text-align: center;
  margin-bottom: $jst-space-xl;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.invt-form__row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $jst-space-md 0;
  border-bottom: 2rpx solid $jst-border;
  gap: $jst-space-md;
}

.invt-form__row--col {
  flex-direction: column;
  align-items: stretch;
  gap: $jst-space-sm;
}

.invt-form__label {
  font-size: $jst-font-base;
  color: $jst-text-primary;
  font-weight: $jst-weight-medium;
}

.invt-form__required {
  color: $jst-danger;
  margin-left: 4rpx;
}

.invt-form__input {
  width: 100%;
}

.invt-form__hint {
  font-size: $jst-font-xs;
  color: $jst-text-placeholder;
  margin-top: 4rpx;
}

.invt-form__radios {
  display: flex;
  gap: $jst-space-xl;
}

.invt-form__submit {
  display: flex;
  gap: $jst-space-md;
  margin-top: $jst-space-xl;
}

::v-deep .invt-form__cancel.u-button {
  flex: 1;
  height: 88rpx;
  border-color: $jst-border;
  border-radius: $jst-radius-md;
  color: $jst-text-secondary;
  font-size: $jst-font-base;
}

::v-deep .invt-form__save.u-button {
  flex: 2;
  height: 88rpx;
  border: none;
  border-radius: $jst-radius-md;
  background: $jst-brand-gradient;
  color: $jst-text-inverse;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
}
</style>
