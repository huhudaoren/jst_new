import request from '@/utils/request'

export function getReminders() {
  return request({ url: '/sales/me/performance/reminders', method: 'get' })
}
