// ═══════════════════════════════════════════
// 空态预设 Preset — 13 场景标准化文案 + CTA + 插画
// 用法: <jst-empty v-bind="EMPTY_ORDERS" />
// ═══════════════════════════════════════════

// 订单空态
export const EMPTY_ORDERS = {
  illustration: 'orders',
  text: '还没有订单记录，去浏览赛事报名试试',
  buttonText: '去浏览赛事',
  buttonUrl: 'switchTab:/pages/contest/list'
}

// 退款空态（复用订单视觉）
export const EMPTY_REFUNDS = {
  illustration: 'orders',
  text: '暂无退款记录',
  buttonText: '查看我的订单',
  buttonUrl: '/pages-sub/my/order-list'
}

// 报名空态
export const EMPTY_ENROLL = {
  illustration: 'records',
  text: '当前筛选下还没有报名记录',
  buttonText: '去浏览赛事',
  buttonUrl: 'switchTab:/pages/contest/list'
}

// 搜索空态
export const EMPTY_SEARCH = {
  illustration: 'search',
  text: '没有找到相关结果，试试换个关键词',
  buttonText: '',
  buttonUrl: ''
}

// 网络错误
export const EMPTY_NETWORK = {
  illustration: 'network',
  text: '网络开小差了，请稍后重试',
  buttonText: '重新加载',
  buttonUrl: ''
}

// 权益空态
export const EMPTY_RIGHTS = {
  illustration: 'rights',
  text: '暂无可用权益',
  buttonText: '去首页看看',
  buttonUrl: 'switchTab:/pages/index/index'
}

// 优惠券空态（复用权益视觉）
export const EMPTY_COUPONS = {
  illustration: 'rights',
  text: '暂无可用优惠券',
  buttonText: '去领券中心',
  buttonUrl: '/pages-sub/marketing/coupon-center'
}

// 证书空态
export const EMPTY_CERTS = {
  illustration: 'records',
  text: '还没有证书记录',
  buttonText: ''
}

// 成绩空态
export const EMPTY_SCORES = {
  illustration: 'records',
  text: '还没有成绩记录'
}

// 通用空态
export const EMPTY_DEFAULT = {
  illustration: 'default',
  text: '暂无数据'
}

// 消息空态
export const EMPTY_MESSAGES = {
  illustration: 'default',
  text: '暂无消息通知'
}

// 渠道学生空态
export const EMPTY_CHANNEL_STUDENTS = {
  illustration: 'records',
  text: '还没有绑定的学生'
}

// 记录空态
export const EMPTY_RECORDS = {
  illustration: 'records',
  text: '暂无记录'
}

// 积分空态
export const EMPTY_POINTS = {
  illustration: 'default',
  text: '暂无积分明细'
}

// 提现空态
export const EMPTY_WITHDRAW = {
  illustration: 'default',
  text: '暂无提现记录'
}
