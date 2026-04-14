/**
 * 统一请求封装
 * 强约束 (架构设计/26-Uniapp用户端架构.md §4):
 *   1. 自动注入 Authorization Bearer token
 *   2. 401 自动跳登录页
 *   3. 业务错误码统一弹 toast
 *   4. 禁止任何接口绕过本封装
 *   5. 禁止页面内 mock 数据,所有数据从真实接口取
 */

// 开发时改成你电脑 IP (如果用真机/模拟器调试)
// 微信开发者工具 → 详情 → 本地设置 → 勾选「不校验合法域名...」
// #ifdef H5
export const BASE_URL = ''  // H5 同域部署，Nginx 反向代理
// #endif
// #ifndef H5
export const BASE_URL = 'http://127.0.0.1:8080'
// #endif

const TOKEN_KEY = 'jst-token'

export function getToken() {
  return uni.getStorageSync(TOKEN_KEY) || ''
}

export function setToken(token) {
  uni.setStorageSync(TOKEN_KEY, token)
}

export function clearToken() {
  uni.removeStorageSync(TOKEN_KEY)
}

/**
 * 统一请求
 * @param {Object} options { url, method, data, header, silent }
 *   silent: true 时业务失败不弹 toast (用于静默接口)
 */
export default function request(options) {
  return new Promise((resolve, reject) => {
    const token = getToken()
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(options.header || {})
      },
      success: (res) => {
        const body = res.data
        // 1. HTTP 401 → 跳登录
        if (res.statusCode === 401 || (body && body.code === 401)) {
          clearToken()
          uni.reLaunch({ url: '/pages/login/login' })
          return reject(new Error('未登录'))
        }
        // 2. 业务成功
        if (body && body.code === 200) {
          const isPagedResponse = Object.prototype.hasOwnProperty.call(body, 'rows')
            || Object.prototype.hasOwnProperty.call(body, 'total')
          return resolve(isPagedResponse ? body : (body.data !== undefined ? body.data : body))
        }
        // 3. 业务失败
        if (!options.silent) {
          uni.showToast({ title: (body && body.msg) || '操作失败', icon: 'none' })
        }
        reject(body)
      },
      fail: (err) => {
        if (!options.silent) {
          uni.showToast({ title: '网络异常', icon: 'none' })
        }
        reject(err)
      }
    })
  })
}
