/**
 * 营销活动 API (WX-C4 · F-MARKETING-RIGHTS-BE)
 * 对应后端: /jst/wx/campaign/*
 */
import request from '@/api/request'

/** 活动列表 */
export function getCampaignList(params) {
  return request({ url: '/jst/wx/campaign/list', method: 'GET', data: params })
}

/** 活动详情 */
export function getCampaignDetail(id) {
  return request({ url: `/jst/wx/campaign/${id}`, method: 'GET' })
}
