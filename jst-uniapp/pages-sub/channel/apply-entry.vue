<!-- 中文注释: 渠道认证入口 · 类型选择
     对应原型: 小程序原型图/channel-identity.html
     调用接口: GET /jst/wx/channel/auth/my -->
<template>
  <view class="ae-page">
    <view class="ae-hero" :style="{ paddingTop: navPaddingTop }">
      <text class="ae-hero__title">申请成为渠道方</text>
      <text class="ae-hero__desc">认证后可享受渠道方专属权益：场地减免 · 专属课程 · 客服特权 · 返点结算</text>
    </view>

    <view class="ae-section">
      <view class="ae-section__head">
        <text class="ae-section__title">选择认证类型</text>
        <u-tag text="3 种身份可选" type="primary" plain shape="circle" size="mini"></u-tag>
      </view>

      <u-cell-group :border="false" customClass="ae-card-group">
        <u-cell
          title="老师认证"
          label="适用于在职教师、培训讲师"
          isLink
          clickable
          customClass="ae-card"
          @click="go('teacher')"
        >
          <template #icon>
            <text class="ae-card__icon">🎓</text>
          </template>
        </u-cell>

        <u-cell
          title="机构认证"
          label="适用于培训机构、学校、社团"
          isLink
          clickable
          customClass="ae-card"
          @click="go('organization')"
        >
          <template #icon>
            <text class="ae-card__icon">🏢</text>
          </template>
        </u-cell>

        <u-cell
          title="个人认证"
          label="适用于个体经营、自由职业"
          isLink
          clickable
          customClass="ae-card"
          @click="go('individual')"
        >
          <template #icon>
            <text class="ae-card__icon">👤</text>
          </template>
        </u-cell>
      </u-cell-group>
    </view>
  </view>
</template>

<script>
import { getMyChannelApply } from '@/api/channel'

export default {
  data() {
    return {
      skeletonShow: true // [visual-effect]
    }
  },
  onShow() { this.checkExisting() },
  methods: {
    async checkExisting() {
      try {
        const data = await getMyChannelApply()
        if (data && data.applyId && ['pending', 'approved', 'locked_for_manual'].includes(data.applyStatus)) {
          uni.redirectTo({ url: '/pages-sub/channel/apply-status' })
        }
      } catch (e) {}
    },
    go(type) {
      uni.navigateTo({ url: '/pages-sub/channel/apply-form?channelType=' + type })
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.ae-page { min-height: 100vh; background: $jst-bg-page; }
.ae-hero { padding: 96rpx $jst-space-xl 56rpx; background: linear-gradient(150deg, $jst-brand-dark 0%, $jst-brand 40%, lighten($jst-brand, 8%) 100%); color: $jst-text-inverse; border-bottom-left-radius: $jst-radius-xl; border-bottom-right-radius: $jst-radius-xl; }
.ae-hero__title { display: block; font-size: $jst-font-xxl; font-weight: $jst-weight-semibold; }
.ae-hero__desc { display: block; margin-top: $jst-space-md; font-size: $jst-font-sm; line-height: 1.7; color: rgba(255, 255, 255, 0.76); }
.ae-section { margin: $jst-space-xl $jst-space-xl 0; }
.ae-section__head { display: flex; align-items: center; justify-content: space-between; margin-bottom: $jst-space-md; }
.ae-section__title { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.ae-card-group { border-radius: $jst-radius-md; overflow: hidden; box-shadow: $jst-shadow-sm; }
.ae-card__icon { display: inline-flex; align-items: center; justify-content: center; width: 64rpx; height: 64rpx; font-size: $jst-font-xxl; margin-right: $jst-space-sm; }

::v-deep .ae-card.u-cell {
  background: $jst-bg-card;
  min-height: 132rpx;
}

::v-deep .ae-card .u-cell__title-text {
  font-size: $jst-font-md;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

::v-deep .ae-card .u-cell__label {
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}
</style>
