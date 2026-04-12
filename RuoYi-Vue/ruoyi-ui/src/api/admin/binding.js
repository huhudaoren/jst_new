import request from '@/utils/request'

// 绑定关系列表
export function listBinding(query) {
  return request({ url: '/jst/user/binding/list', method: 'get', params: query })
}

// 强制解绑
export function forceUnbind(bindingId, data) {
  return request({ url: '/jst/user/binding/' + bindingId + '/force-unbind', method: 'post', data })
}
