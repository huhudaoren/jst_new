<!--
  ═══════════════════════════════════════════════════════════════
  表单页模板 — 复制我, 重命名, 替换 __PageName__
  ═══════════════════════════════════════════════════════════════

  覆盖场景:
    - u-form 校验 + 字段列表
    - 底部固定 CTA (提交 + 禁用态 + loading + safeAreaBottom)
    - 成功后回流 (setTimeout 400ms + navigateBack 或 reLaunch)
    - 离开前脏数据确认 (可选, 取消注释 onBackPress)

  替换 checklist:
    1) 把 __PageName__ 换成实际组件名 (如 FeedbackForm)
    2) 改 rules / form 字段适配业务
    3) 改 submitForm 调用真实 API
    4) 若需要离开确认, 取消注释 onBackPress 整段
-->
<template>
  <view class="page">
    <!-- 自定义导航 -->
    <view class="page__nav" :style="{ paddingTop: navPaddingTop }">
      <view class="page__back" @tap="handleBack">←</view>
      <text class="page__title">__表单标题__</text>
      <view class="page__nav-placeholder"></view>
    </view>

    <!-- 表单主体 -->
    <view class="page__body">
      <u-form
        ref="uForm"
        :model="form"
        :rules="rules"
        labelPosition="top"
        :borderBottom="false"
      >
        <view class="page__card">
          <u-form-item label="姓名" prop="name" required>
            <u-input
              v-model="form.name"
              placeholder="请输入真实姓名"
              :maxlength="20"
              border="bottom"
            />
          </u-form-item>

          <u-form-item label="联系电话" prop="phone" required>
            <u-input
              v-model="form.phone"
              type="number"
              placeholder="请输入 11 位手机号"
              :maxlength="11"
              border="bottom"
            />
          </u-form-item>

          <u-form-item label="备注" prop="remark">
            <u-input
              v-model="form.remark"
              type="textarea"
              placeholder="如有补充说明请填写"
              :maxlength="200"
              border="surround"
              :autoHeight="true"
            />
          </u-form-item>
        </view>
      </u-form>

      <!-- 空档, 配合底部 CTA -->
      <view class="page__bottom-spacer"></view>
    </view>

    <!-- 底部固定提交栏 -->
    <view class="page__cta-bar" :style="{ paddingBottom: safeAreaBottom }">
      <u-button
        class="page__cta-btn"
        :loading="submitting"
        :disabled="!canSubmit"
        @click="handleSubmit"
      >{{ submitting ? '提交中...' : '提交' }}</u-button>
    </view>
  </view>
</template>

<script>
import jstPageHelper from '@/mixins/jst-page-helper'
// import { __submitForm__ } from '@/api/__module__'

export default {
  name: '__PageName__',
  mixins: [jstPageHelper],
  data() {
    return {
      form: {
        name: '',
        phone: '',
        remark: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: ['blur', 'change'] },
          { min: 2, max: 20, message: '长度 2-20 字', trigger: ['blur'] }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: ['blur', 'change'] },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: ['blur'] }
        ]
      },
      submitting: false,
      // 记录初始值, 用于离开时脏检测
      initialForm: null
    }
  },
  computed: {
    canSubmit() {
      // 必填字段检查 (非严格校验, 仅做按钮 disabled)
      return !!this.form.name && !!this.form.phone && !this.submitting
    },
    isDirty() {
      if (!this.initialForm) return false
      return JSON.stringify(this.form) !== JSON.stringify(this.initialForm)
    }
  },
  onReady() {
    // u-form 规则注册
    if (this.$refs.uForm && typeof this.$refs.uForm.setRules === 'function') {
      this.$refs.uForm.setRules(this.rules)
    }
    this.initialForm = JSON.parse(JSON.stringify(this.form))
  },
  // 如需离开确认, 打开以下注释
  // async onBackPress(options) {
  //   if (options.from === 'backbutton' && this.isDirty) {
  //     const confirm = await this.confirmAsync({
  //       title: '放弃填写？',
  //       content: '未保存的内容将丢失',
  //       destructive: true,
  //       confirmText: '放弃'
  //     })
  //     return !confirm  // true 阻止返回
  //   }
  //   return false
  // },
  methods: {
    async handleSubmit() {
      if (!this.$refs.uForm) return
      try {
        await this.$refs.uForm.validate()
      } catch (err) {
        this.showToastError('请先完成必填项')
        return
      }

      this.submitting = true
      try {
        // TODO: 调用真实 API
        // await __submitForm__(this.form)
        await new Promise((r) => setTimeout(r, 400))

        this.showToastSuccess('提交成功')
        this.initialForm = JSON.parse(JSON.stringify(this.form))  // reset dirty
        setTimeout(() => {
          if (getCurrentPages().length > 1) {
            uni.navigateBack()
          } else {
            uni.switchTab({ url: '/pages/my/index' })
          }
        }, 600)
      } catch (e) {
        this.showToastError('提交失败，请稍后重试')
      } finally {
        this.submitting = false
      }
    },

    async handleBack() {
      if (this.isDirty) {
        const ok = await this.confirmAsync({
          title: '放弃填写？',
          content: '未保存的内容将丢失',
          destructive: true,
          confirmText: '放弃',
          cancelText: '继续填写'
        })
        if (!ok) return
      }
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
      } else {
        uni.switchTab({ url: '/pages/my/index' })
      }
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.page {
  min-height: 100vh;
  background: $jst-bg-page;
  padding-bottom: 200rpx;  // 底部 CTA 占位
}

.page__nav {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 $jst-page-padding;
  background: linear-gradient(135deg, $jst-brand-dark 0%, $jst-brand 100%);
  position: sticky;
  top: 0;
  z-index: 40;
}

.page__back,
.page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.page__back {
  font-size: 36rpx;
  color: $jst-text-inverse;
}

.page__title {
  flex: 1;
  text-align: center;
  font-size: $jst-font-lg;
  font-weight: $jst-weight-bold;
  color: $jst-text-inverse;
}

.page__body {
  padding: $jst-space-lg $jst-page-padding;
}

.page__card {
  padding: $jst-space-lg;
  border-radius: $jst-radius-card;
  background: $jst-bg-card;
  box-shadow: $jst-card-glow;
}

.page__bottom-spacer {
  height: $jst-space-xxl;
}

/* 底部固定 CTA */
.page__cta-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: $jst-space-md $jst-page-padding;
  background: $jst-bg-card;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.06);
  z-index: 30;
}

::v-deep .page__cta-btn.u-button {
  height: 88rpx;
  width: 100%;
  border: none;
  border-radius: $jst-radius-button;
  background: $jst-brand-gradient;
  color: $jst-text-inverse;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
}

::v-deep .page__cta-btn.u-button--disabled,
::v-deep .page__cta-btn.u-button[disabled] {
  background: $jst-bg-grey;
  color: $jst-text-placeholder;
}
</style>
