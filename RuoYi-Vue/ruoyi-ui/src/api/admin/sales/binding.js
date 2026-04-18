import request from '@/utils/request'

// 手动绑定/转移渠道归属销售
export function manualBind(data) {
  return request({ url: '/admin/sales/binding/manual', method: 'post', data })
}
