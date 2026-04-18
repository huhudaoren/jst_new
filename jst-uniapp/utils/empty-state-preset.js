/**
 * ═══════════════════════════════════════════════════════════════
 * empty-state-preset — 全站空态文案 / CTA 常量库
 * ═══════════════════════════════════════════════════════════════
 *
 * 为什么需要: 审计显示 "暂无数据" / "暂无订单" 在 60+ 页各说各话, 且 90% 空态
 * 无 CTA 引导用户下一步。此文件统一语气 + 强制每个场景都有 CTA。
 *
 * ▼ 语气约束
 *   - 禁用 "暂无..." (冰冷、信息量低)
 *   - 用 "还没有..." / "空空如也" / "暂时还是空的"
 *   - 副文案给出下一步建议: "去报名吧"、"看看赛事"
 *   - CTA 按钮动词开头: "去报名" / "去浏览" / "看看赛事"
 *
 * ▼ 使用示例
 *   <script>
 *   import { EMPTY_ORDERS } from '@/utils/empty-state-preset'
 *   export default {
 *     data() { return { EMPTY_ORDERS } }
 *   }
 *   </script>
 *
 *   <template>
 *     <jst-empty v-if="!list.length" v-bind="EMPTY_ORDERS" />
 *   </template>
 *
 * ▼ jst-empty 组件 prop 约定 (参考 components/jst-empty/jst-empty.vue)
 *   - text / desc / buttonText / buttonUrl / icon / buttonType
 *   - buttonUrl 支持前缀: 'switchTab:/pages/xxx' | 'reLaunch:/pages/xxx' | 默认 navigateTo
 *
 * ▼ 新增场景规则
 *   - 必须给 text 和 icon (emoji)
 *   - CTA 尽量给 (信息死角少), 实在无下一步才留空
 *   - 保持 12~20 字内, 简短
 */

// ─── 订单 / 交易 ───

export const EMPTY_ORDERS = {
  icon: '📦',
  text: '还没有订单',
  desc: '去浏览赛事，报一个感兴趣的吧',
  buttonText: '去浏览赛事',
  buttonUrl: 'switchTab:/pages/contest/list'
}

export const EMPTY_REFUNDS = {
  icon: '💳',
  text: '没有退款记录',
  desc: '所有退款都会在这里展示',
  buttonText: '',
  buttonUrl: ''
}

export const EMPTY_WITHDRAW = {
  icon: '💰',
  text: '还没有提现记录',
  desc: '累计收益达标后即可发起提现',
  buttonText: '查看收益',
  buttonUrl: '/pages-sub/channel/rebate'
}

// ─── 赛事 / 报名 ───

export const EMPTY_ENROLL = {
  icon: '📝',
  text: '还没有报名记录',
  desc: '挑一个合适的赛事试试',
  buttonText: '去报名',
  buttonUrl: 'switchTab:/pages/contest/list'
}

export const EMPTY_CONTESTS = {
  icon: '🏆',
  text: '当前分类还没有赛事',
  desc: '试试切换其他分类或清空筛选',
  buttonText: '',
  buttonUrl: ''
}

// ─── 积分 / 权益 / 优惠券 ───

export const EMPTY_COUPONS = {
  icon: '🎟️',
  text: '还没有优惠券',
  desc: '关注活动可领取优惠券',
  buttonText: '去逛逛',
  buttonUrl: 'switchTab:/pages/contest/list'
}

export const EMPTY_POINTS = {
  icon: '✨',
  text: '积分记录空空如也',
  desc: '完成报名、打卡、分享都可获得积分',
  buttonText: '去赚积分',
  buttonUrl: 'switchTab:/pages/contest/list'
}

export const EMPTY_RIGHTS = {
  icon: '🎁',
  text: '还没有权益',
  desc: '兑换商品或参加活动可获得权益',
  buttonText: '去积分商城',
  buttonUrl: '/pages-sub/mall/index'
}

// ─── 消息 / 通知 ───

export const EMPTY_MESSAGES = {
  icon: '🔔',
  text: '消息中心暂时还是空的',
  desc: '订单、审核、活动消息都会在此提醒',
  buttonText: '',
  buttonUrl: ''
}

// ─── 渠道方场景 ───

export const EMPTY_CHANNEL_STUDENTS = {
  icon: '👥',
  text: '还没有绑定学生',
  desc: '邀请学生扫码绑定，享受代报名服务',
  buttonText: '生成邀请码',
  buttonUrl: '/pages-sub/channel/invite'
}

// ─── 成绩 / 证书 ───

export const EMPTY_CERTS = {
  icon: '🎓',
  text: '还没有获得证书',
  desc: '参赛并取得成绩即可获得证书',
  buttonText: '查看赛事',
  buttonUrl: 'switchTab:/pages/contest/list'
}

export const EMPTY_SCORES = {
  icon: '📊',
  text: '还没有成绩记录',
  desc: '参赛成绩公布后将在此展示',
  buttonText: '',
  buttonUrl: ''
}

// ─── 学习 / 课程 ───

export const EMPTY_RECORDS = {
  icon: '📚',
  text: '还没有学习记录',
  desc: '订阅课程开始学习，进度会自动同步',
  buttonText: '去选课',
  buttonUrl: 'switchTab:/pages/course/list'
}

// ─── 搜索 / 网络 ───

export const EMPTY_SEARCH = {
  icon: '🔍',
  text: '没有找到匹配结果',
  desc: '换个关键词或清空筛选再试试',
  buttonText: '',
  buttonUrl: ''
}

export const EMPTY_NETWORK = {
  icon: '📡',
  text: '网络开了个小差',
  desc: '检查网络后点击重试',
  buttonText: '重新加载',
  buttonUrl: ''  // 空 url → 组件 emit('action') 交页面处理
}

// ─── 兜底 ───

export const EMPTY_DEFAULT = {
  icon: '📭',
  text: '暂时还是空的',
  desc: '',
  buttonText: '',
  buttonUrl: ''
}

// 统一导出 map, 方便动态场景
export const EMPTY_PRESETS = {
  orders: EMPTY_ORDERS,
  refunds: EMPTY_REFUNDS,
  withdraw: EMPTY_WITHDRAW,
  enroll: EMPTY_ENROLL,
  contests: EMPTY_CONTESTS,
  coupons: EMPTY_COUPONS,
  points: EMPTY_POINTS,
  rights: EMPTY_RIGHTS,
  messages: EMPTY_MESSAGES,
  channelStudents: EMPTY_CHANNEL_STUDENTS,
  certs: EMPTY_CERTS,
  scores: EMPTY_SCORES,
  records: EMPTY_RECORDS,
  search: EMPTY_SEARCH,
  network: EMPTY_NETWORK,
  default: EMPTY_DEFAULT
}

export default EMPTY_PRESETS
