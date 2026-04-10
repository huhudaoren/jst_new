import request from '@/utils/request'

export function scanWriteoff(payload) {
  const data = typeof payload === 'string'
    ? {
        qrCode: payload,
        scanType: 'partner_h5',
        terminal: 'partner_h5'
      }
    : {
        ...payload,
        qrCode: payload && (payload.qrCode || payload.payload),
        scanType: (payload && payload.scanType) || 'partner_h5',
        terminal: (payload && payload.terminal) || 'partner_h5'
      }

  return request({
    url: '/jst/wx/writeoff/scan',
    method: 'post',
    data
  })
}

export function getWriteoffRecent(params) {
  return request({
    url: '/jst/wx/writeoff/records',
    method: 'get',
    params: {
      pageNum: 1,
      pageSize: 20,
      ...params
    }
  })
}
