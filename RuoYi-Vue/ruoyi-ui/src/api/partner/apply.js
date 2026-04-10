import request from '@/utils/request'

export function submitPartnerApply(data) {
  return request({
    url: '/jst/public/organizer/partner/apply',
    method: 'post',
    headers: {
      isToken: false
    },
    data
  })
}

export function getPartnerApplyStatus(applyNo) {
  return request({
    url: `/jst/public/organizer/partner/apply/${applyNo}/status`,
    method: 'get',
    headers: {
      isToken: false
    }
  })
}

export function uploadPartnerApplyFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/jst/public/organizer/partner/apply/upload',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data',
      isToken: false,
      repeatSubmit: false
    },
    data: formData
  })
}
