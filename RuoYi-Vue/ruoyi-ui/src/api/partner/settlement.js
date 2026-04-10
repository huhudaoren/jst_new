import request from '@/utils/request'

export function listPartnerSettlements(params) {
  return request({
    url: '/jst/partner/settlement/list',
    method: 'get',
    params
  })
}

export function getPartnerSettlement(id) {
  return request({
    url: `/jst/partner/settlement/${id}`,
    method: 'get'
  })
}

export function confirmPartnerSettlement(id) {
  return request({
    url: `/jst/partner/settlement/${id}/confirm`,
    method: 'post'
  })
}

export function disputePartnerSettlement(id, data) {
  return request({
    url: `/jst/partner/settlement/${id}/dispute`,
    method: 'post',
    data
  })
}
