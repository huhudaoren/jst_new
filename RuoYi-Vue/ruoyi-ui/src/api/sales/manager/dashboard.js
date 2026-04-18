import request from '@/utils/request'

export function getManagerDashboard(month) {
  return request({ url: '/sales/manager/team/dashboard', method: 'get', params: { month } })
}
