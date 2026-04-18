import request from '@/utils/request'

/** 我的预录入列表（分页） */
export function listMyPreReg(query) {
  return request({ url: '/sales/me/pre-register/list', method: 'get', params: query })
}

/**
 * 新建预录入。
 * @param {{ phone: string, targetName: string }} data
 */
export function createPreReg(data) {
  return request({ url: '/sales/me/pre-register', method: 'post', data })
}

/** 续期预录入（延长 30 天有效期，每条最多 2 次） */
export function renewPreReg(preId) {
  return request({ url: `/sales/me/pre-register/${preId}/renew`, method: 'post' })
}

/** 删除预录入（未匹配状态才可删） */
export function removePreReg(preId) {
  return request({ url: `/sales/me/pre-register/${preId}`, method: 'delete' })
}
