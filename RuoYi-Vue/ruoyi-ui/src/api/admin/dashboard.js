import request from '@/utils/request'

// 复用已有列表接口做聚合统计

// 今日订单（取 total）
export function countTodayOrders(query) {
  return request({ url: '/jst/order/jst_order_main/list', method: 'get', params: { ...query, pageNum: 1, pageSize: 1 } })
}

// 待审核赛事
export function countPendingContests(query) {
  return request({ url: '/jst/event/jst_contest/list', method: 'get', params: { ...query, pageNum: 1, pageSize: 1 } })
}

// 待审核退款
export function countPendingRefunds(query) {
  return request({ url: '/jst/order/jst_refund_record/list', method: 'get', params: { ...query, pageNum: 1, pageSize: 1 } })
}

// 待审核提现
export function countPendingWithdraws(query) {
  return request({ url: '/jst/channel/withdraw/list', method: 'get', params: { ...query, pageNum: 1, pageSize: 1 } })
}

// 赛事报名排行（取 Top 5）
export function topContests(query) {
  return request({ url: '/jst/event/jst_contest/list', method: 'get', params: { ...query, pageNum: 1, pageSize: 5 } })
}

// 渠道返点排行（取 Top 5）
export function topChannels(query) {
  return request({ url: '/jst/user/jst_channel/list', method: 'get', params: { ...query, pageNum: 1, pageSize: 5 } })
}
