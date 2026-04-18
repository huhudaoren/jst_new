import request from '@/utils/request'

// 查看手机号完整号码操作日志（分页）
export function listPhoneViews(params) {
  return request({ url: '/admin/sales/audit/phone-views', method: 'get', params })
}

// 查看导出操作日志（分页）
export function listExports(params) {
  return request({ url: '/admin/sales/audit/exports', method: 'get', params })
}
