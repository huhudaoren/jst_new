import request from '@/utils/request'

/** 销售工作台 Dashboard 摘要（待定 plan-04 补 /sales/me/dashboard 端点，暂用 performance 聚合） */
export function getDashboard() {
  return request({ url: '/sales/me/performance', method: 'get' })
}
