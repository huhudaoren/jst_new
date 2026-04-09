<!-- 中文注释: 渠道方 · 我的学生 (骨架页)
     对应原型: 小程序原型图/channel-students.png
     TODO: 等 F-CHANNEL-DASHBOARD 后端接口 ready 后迭代, 禁止 mock -->
<template>
  <view class="sk-page">
    <view class="sk-card">
      <text class="sk-card__icon">👥</text>
      <text class="sk-card__title">我的学生</text>
      <text class="sk-card__desc">功能开发中，敬请期待。后端接口就绪后将展示绑定到本渠道的学生列表。</text>
    </view>
  </view>
</template>
<script>
export default { name: 'channel-students-skeleton' }
</script>
<style scoped lang="scss">
.sk-page { min-height: 100vh; padding: 160rpx 48rpx; background: var(--jst-color-page-bg); }
.sk-card { padding: 56rpx 40rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); text-align: center; }
.sk-card__icon { font-size: 72rpx; }
.sk-card__title { display: block; margin-top: 20rpx; font-size: 32rpx; font-weight: 700; color: var(--jst-color-text); }
.sk-card__desc { display: block; margin-top: 16rpx; font-size: 24rpx; line-height: 1.6; color: var(--jst-color-text-tertiary); }
</style>
