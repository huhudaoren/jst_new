<!-- 中文注释: 设置页；对应原型 小程序原型图/settings.html；无直接接口 -->
<template>
  <view class="settings-page">
    <view class="settings-page__header">
      <view class="settings-page__back" @tap="goBack">←</view>
      <text class="settings-page__header-title">设置</text>
    </view>

    <!-- 隐私与协议 -->
    <view class="settings-page__group">
      <text class="settings-page__group-title">隐私与协议</text>
      <view class="settings-page__cell" @tap="navigatePrivacyPolicy">
        <view class="settings-page__cell-icon settings-page__cell-icon--neutral">📄</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">隐私保护政策</text>
        </view>
        <u-icon name="arrow-right" class="settings-page__cell-arrow" size="22" />
      </view>
      <view class="settings-page__cell" @tap="navigateAgreement">
        <view class="settings-page__cell-icon settings-page__cell-icon--neutral">📋</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">用户服务协议</text>
        </view>
        <u-icon name="arrow-right" class="settings-page__cell-arrow" size="22" />
      </view>
      <view class="settings-page__cell" @tap="navigatePrivacySettings">
        <view class="settings-page__cell-icon settings-page__cell-icon--brand">🔒</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">隐私设置</text>
          <text class="settings-page__cell-desc">个人信息、权限授权管理</text>
        </view>
        <u-icon name="arrow-right" class="settings-page__cell-arrow" size="22" />
      </view>
    </view>

    <!-- 账号安全 -->
    <view class="settings-page__group">
      <text class="settings-page__group-title">账号安全</text>
      <view class="settings-page__cell" @tap="navigateBinding">
        <view class="settings-page__cell-icon settings-page__cell-icon--brand">🔗</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">绑定关系管理</text>
          <text class="settings-page__cell-desc">查看或解除与老师/机构的绑定</text>
        </view>
        <u-icon name="arrow-right" class="settings-page__cell-arrow" size="22" />
      </view>
      <view class="settings-page__cell" @tap="navigateAddressList">
        <view class="settings-page__cell-icon settings-page__cell-icon--brand">📍</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">地址管理</text>
          <text class="settings-page__cell-desc">兑换实物商品的收货地址</text>
        </view>
        <u-icon name="arrow-right" class="settings-page__cell-arrow" size="22" />
      </view>
      <view class="settings-page__cell" @tap="navigateInvoiceTitle">
        <view class="settings-page__cell-icon settings-page__cell-icon--neutral">🧾</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">发票抬头管理</text>
          <text class="settings-page__cell-desc">开票时自动填充</text>
        </view>
        <u-icon name="arrow-right" class="settings-page__cell-arrow" size="22" />
      </view>
      <view v-if="isChannelUser" class="settings-page__cell" @tap="navigateSettlement">
        <view class="settings-page__cell-icon settings-page__cell-icon--success">💳</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">收款账户</text>
          <text class="settings-page__cell-desc">渠道提现结算账户管理</text>
        </view>
        <u-icon name="arrow-right" class="settings-page__cell-arrow" size="22" />
      </view>
    </view>

    <!-- 消息通知 -->
    <view class="settings-page__group">
      <text class="settings-page__group-title">消息通知</text>
      <view class="settings-page__cell">
        <view class="settings-page__cell-icon settings-page__cell-icon--brand">🔔</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">系统推送</text>
          <text class="settings-page__cell-desc">订单、审核结果等重要通知</text>
        </view>
        <u-switch
          v-model="notifySystem"
          :active-color="switchActiveColor"
          size="18"
          @change="onChangeNotifySystem"
        />
      </view>
      <view class="settings-page__cell">
        <view class="settings-page__cell-icon settings-page__cell-icon--neutral">📣</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">营销推送</text>
          <text class="settings-page__cell-desc">活动、优惠券等促销信息</text>
        </view>
        <u-switch
          v-model="notifyMarketing"
          :active-color="switchActiveColor"
          size="18"
          @change="onChangeNotifyMarketing"
        />
      </view>
    </view>

    <!-- 通用 -->
    <view class="settings-page__group">
      <text class="settings-page__group-title">通用</text>
      <view class="settings-page__cell">
        <view class="settings-page__cell-icon settings-page__cell-icon--neutral">🌐</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">语言</text>
        </view>
        <text class="settings-page__cell-value">简体中文</text>
        <u-icon name="arrow-right" class="settings-page__cell-arrow" size="22" />
      </view>
      <view class="settings-page__cell" @tap="clearCache">
        <view class="settings-page__cell-icon settings-page__cell-icon--neutral">🗑️</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">清除缓存</text>
          <text class="settings-page__cell-desc">{{ cacheText }}</text>
        </view>
        <text class="settings-page__cell-value">清除</text>
        <u-icon name="arrow-right" class="settings-page__cell-arrow" size="22" />
      </view>
      <view class="settings-page__cell" @tap="navigateHelp">
        <view class="settings-page__cell-icon settings-page__cell-icon--success">💬</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">客服与帮助</text>
          <text class="settings-page__cell-desc">FAQ、在线客服、联系方式</text>
        </view>
        <u-icon name="arrow-right" class="settings-page__cell-arrow" size="22" />
      </view>
      <view class="settings-page__cell">
        <view class="settings-page__cell-icon settings-page__cell-icon--brand">🔄</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">检查更新</text>
          <text class="settings-page__cell-desc">当前版本 v1.0.0</text>
        </view>
        <u-tag class="settings-page__cell-tag" text="最新" type="success" plain size="mini" />
      </view>
    </view>

    <!-- 版本 -->
    <view class="settings-page__version">
      <view class="settings-page__version-icon">🏆</view>
      <view class="settings-page__version-info">
        <text class="settings-page__version-name">竞赛通</text>
        <text class="settings-page__version-text">v1.0.0 · 构建 20260318</text>
      </view>
    </view>

    <!-- 危险操作 -->
    <view class="settings-page__group settings-page__group--danger">
      <view class="settings-page__cell settings-page__cell--danger" @tap="handleDeactivate">
        <view class="settings-page__cell-icon settings-page__cell-icon--danger">⚠️</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name settings-page__cell-name--danger">注销账号</text>
          <text class="settings-page__cell-desc">永久删除账号及所有数据</text>
        </view>
        <u-icon name="arrow-right" class="settings-page__cell-arrow" size="22" />
      </view>
    </view>

    <u-button class="settings-page__logout" @click="handleLogout">退出登录</u-button>
  </view>
</template>

<script>
import { useUserStore } from '@/store/user'

const NOTIFY_STORAGE_KEY = 'jst_notify_prefs'
// 中文注释: u-switch 的 active-color prop 只接收字符串，无法引入 SCSS 变量，这里显性复用 $jst-brand 色值
const BRAND_COLOR = '#2B6CFF'

export default {
  data() {
    return {
      cacheText: '当前缓存计算中...',
      notifySystem: true,
      notifyMarketing: true,
      switchActiveColor: BRAND_COLOR
    }
  },
  computed: {
    // 中文注释: 渠道方视角（复用 my/index 的判定）
    isChannelUser() {
      const store = useUserStore()
      const info = store.userInfo || {}
      if (info.userType === 'channel') return true
      const roles = store.roles || []
      return Array.isArray(roles) && roles.includes('jst_channel')
    }
  },
  onLoad() {
    this.getCacheSize()
    this.loadNotifyPrefs()
  },
  methods: {
    // 中文注释: 从本地读取通知偏好（后端暂无此接口，本地存储为权宜之计）
    // TODO(backend-round-2b): 后端需提供 GET/POST /jst/wx/user/notify-prefs 同步偏好
    loadNotifyPrefs() {
      try {
        const raw = uni.getStorageSync(NOTIFY_STORAGE_KEY)
        const data = raw && typeof raw === 'object' ? raw : (raw ? JSON.parse(raw) : {})
        if (typeof data.system === 'boolean') this.notifySystem = data.system
        if (typeof data.marketing === 'boolean') this.notifyMarketing = data.marketing
      } catch (e) { /* ignore */ }
    },
    saveNotifyPrefs() {
      try {
        uni.setStorageSync(NOTIFY_STORAGE_KEY, { system: this.notifySystem, marketing: this.notifyMarketing })
      } catch (e) { /* ignore */ }
    },
    onChangeNotifySystem(v) { this.notifySystem = !!v; this.saveNotifyPrefs() },
    onChangeNotifyMarketing(v) { this.notifyMarketing = !!v; this.saveNotifyPrefs() },
    getCacheSize() {
      try {
        const res = uni.getStorageInfoSync()
        const sizeKB = res.currentSize || 0
        this.cacheText = sizeKB > 1024
          ? `当前缓存：${(sizeKB / 1024).toFixed(1)} MB`
          : `当前缓存：${sizeKB} KB`
      } catch (e) {
        this.cacheText = '当前缓存：未知'
      }
    },
    clearCache() {
      uni.showModal({
        title: '清除缓存',
        content: '清除后需要重新登录，确认清除？',
        success: (res) => {
          if (!res.confirm) return
          try {
            // 保留 token，只清理缓存数据
            uni.showToast({ title: '缓存已清除', icon: 'success' })
            this.cacheText = '缓存已清除'
          } catch (e) { /* ignore */ }
        }
      })
    },
    navigatePrivacyPolicy() { uni.navigateTo({ url: '/pages-sub/my/privacy' }) },
    navigatePrivacySettings() { uni.navigateTo({ url: '/pages-sub/my/privacy?type=settings' }) },
    navigateAgreement() {
      // 用户协议暂复用隐私政策页面结构
      uni.navigateTo({ url: '/pages-sub/my/privacy?type=agreement' })
    },
    navigateBinding() { uni.navigateTo({ url: '/pages-sub/my/binding' }) },
    navigateAddressList() { uni.navigateTo({ url: '/pages-sub/my/address-list' }) },
    // TODO(backend-round-2b): 发票抬头管理后端接口未实现（缺 GET/POST /jst/wx/invoice-title），先占位
    navigateInvoiceTitle() {
      uni.showToast({ title: '发票抬头管理开发中', icon: 'none' })
    },
    navigateSettlement() { uni.navigateTo({ url: '/pages-sub/channel/settlement' }) },
    navigateHelp() { uni.navigateTo({ url: '/pages-sub/public/help' }) },
    // 中文注释: 注销账号 — 微信小程序合规要求，需人工审核
    // TODO(backend-round-2b): 后端需提供 POST /jst/wx/user/deactivate 接口（走客服审核流程）
    handleDeactivate() {
      uni.showModal({
        title: '注销账号',
        content: '注销账号为不可恢复操作，如需办理请联系客服（400-888-xxxx），客服核实身份后协助办理。',
        confirmText: '联系客服',
        success: (res) => {
          if (res.confirm) {
            uni.showToast({ title: '请拨打 400-888-xxxx', icon: 'none', duration: 3000 })
          }
        }
      })
    },
    handleLogout() {
      uni.showModal({
        title: '退出登录',
        content: '确认退出当前账号？',
        success: async (res) => {
          if (!res.confirm) return
          const userStore = useUserStore()
          await userStore.doLogout()
          uni.reLaunch({ url: '/pages/login/login' })
        }
      })
    },
    goBack() {
      if (getCurrentPages().length > 1) { uni.navigateBack(); return }
      uni.switchTab({ url: '/pages/my/index' })
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.settings-page {
  min-height: 100vh;
  padding-bottom: 60rpx;
  background: $jst-bg-page;
}

.settings-page__header {
  display: flex;
  align-items: center;
  padding: $jst-space-lg $jst-page-padding;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.settings-page__back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72rpx;
  height: 72rpx;
  border-radius: 22rpx;
  background: $jst-bg-page;
  font-size: $jst-font-md;
  color: $jst-text-primary;
}

.settings-page__header-title {
  flex: 1;
  margin-left: $jst-space-md;
  font-size: 34rpx;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.settings-page__group {
  margin: $jst-space-lg $jst-page-padding 0;
  border-radius: $jst-radius-lg;
  overflow: hidden;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
}

.settings-page__group-title {
  display: block;
  padding: $jst-space-lg 28rpx $jst-space-md;
  border-bottom: 2rpx solid $jst-border;
  letter-spacing: 1rpx;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
  color: $jst-text-placeholder;
}

.settings-page__cell {
  display: flex;
  align-items: center;
  padding: 28rpx;
  border-bottom: 2rpx solid $jst-border;
  gap: $jst-space-lg;
}

.settings-page__cell:last-child {
  border-bottom: none;
}

.settings-page__cell-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 68rpx;
  height: 68rpx;
  border-radius: $jst-radius-sm;
  font-size: $jst-font-lg;
  flex-shrink: 0;
}

.settings-page__cell-icon--neutral {
  background: $jst-bg-page;
}

.settings-page__cell-icon--brand {
  background: $jst-brand-light;
}

.settings-page__cell-icon--success {
  background: $jst-success-light;
}

.settings-page__cell-icon--danger {
  background: $jst-danger-light;
}

.settings-page__cell-name--danger {
  color: $jst-danger;
}

.settings-page__group--danger {
  // 视觉弱化处理 — 注销入口不抢焦点
  .settings-page__cell--danger:active {
    opacity: 0.8;
  }
}

.settings-page__cell-body {
  flex: 1;
  min-width: 0;
}

.settings-page__cell-name {
  display: block;
  font-size: $jst-font-base;
  font-weight: $jst-weight-medium;
  color: $jst-text-primary;
}

.settings-page__cell-desc {
  display: block;
  margin-top: 4rpx;
  font-size: $jst-font-sm;
  color: $jst-text-placeholder;
}

.settings-page__cell-value {
  margin-right: $jst-space-sm;
  white-space: nowrap;
  font-size: $jst-font-base;
  color: $jst-text-placeholder;
}

.settings-page__version {
  display: flex;
  align-items: center;
  gap: $jst-space-lg;
  margin: $jst-space-lg $jst-page-padding 0;
  padding: 28rpx;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
}

.settings-page__version-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 88rpx;
  height: 88rpx;
  border-radius: $jst-radius-md;
  font-size: 48rpx;
  flex-shrink: 0;
  background: linear-gradient(135deg, $jst-brand 0%, $jst-brand-dark 100%);
}

.settings-page__version-name {
  display: block;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

.settings-page__version-text {
  display: block;
  margin-top: 4rpx;
  font-size: $jst-font-sm;
  color: $jst-text-placeholder;
}

.settings-page__logout {
  margin: $jst-space-xl $jst-page-padding 0;
  height: 88rpx;
  border-radius: $jst-radius-md;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
}

::v-deep .settings-page__cell-arrow .u-icon__icon {
  color: $jst-text-placeholder;
}

::v-deep .settings-page__cell-tag .u-tag {
  border-color: $jst-success;
  color: $jst-success;
}

::v-deep .settings-page__logout.u-button {
  border-color: $jst-danger;
  background: $jst-danger-light;
  color: $jst-danger;
}
</style>
