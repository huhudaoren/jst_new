import request from '@/utils/request'

export function getPartnerSummary() {
  return request({
    url: '/jst/partner/dashboard/summary',
    method: 'get'
  })
}

export function getPartnerTodo() {
  return request({
    url: '/jst/partner/dashboard/todo',
    method: 'get'
  })
}

export function getPartnerRecentNotice() {
  return request({
    url: '/jst/partner/notice/recent',
    method: 'get'
  })
}
