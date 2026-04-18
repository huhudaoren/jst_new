/**
 * ═══════════════════════════════════════════════════════════════
 * request-interceptor — 可重试 / 统一错误映射的请求增强层
 * ═══════════════════════════════════════════════════════════════
 *
 * 设计约束:
 *   - 不修改 api/request.js 现有逻辑 (避免全站回归)
 *   - 本文件是"增强层", 只有新代码 import 时才生效
 *   - 业务码映射集中在此处, 方便后续扩展 (如 403/500/限流码)
 *
 * ▼ 与现有 request.js 的关系
 *   request.js:
 *     - 拦 401 → 跳登录
 *     - code!=200 → 弹 toast (除非 silent)
 *     - 网络 fail → 弹 "网络异常"
 *
 *   retryableRequest:
 *     - 调 request.js 底层, 外加一层重试决策
 *     - 超时 / 5xx → 指数退避重试
 *     - 业务失败 (2xx 但 code!=200) → 不重试 (避免重复下单)
 *     - 配合 silent:true 使用, 自己掌控错误提示
 *
 * ▼ 使用示例
 *   import { retryableRequest } from '@/utils/request-interceptor'
 *   import { BASE_URL } from '@/api/request'
 *
 *   const data = await retryableRequest(
 *     { url: '/jst/wx/home/dashboard', method: 'GET', silent: true },
 *     { maxRetries: 2, timeoutMs: 15000 }
 *   )
 *
 * ▼ 选型原则 (何时用 retryableRequest 而不是原生 request)
 *   只读 GET 接口 (首页聚合 / 列表拉取): 可用, 重试低风险
 *   写接口 (下单 / 支付 / 申请): 禁用, 改用原生 request, 由业务方决定重试
 */

import request, { BASE_URL, getToken, clearToken } from '@/api/request'
import { showToast, showModalAsync } from '@/utils/global-actions'

const DEFAULT_TIMEOUT_MS = 15000
const DEFAULT_MAX_RETRIES = 3
const RETRYABLE_HTTP = [0, 408, 500, 502, 503, 504]  // 0 = 请求失败/超时

// ─── 业务错误码映射 ───
// 与后端 JstErrorCode / AjaxResult.code 对齐
const BIZ_CODE_MESSAGES = {
  400: '请求参数有误',
  401: null,           // request.js 已处理跳登录
  403: '权限不足，无法操作',
  404: '请求的资源不存在',
  409: '数据冲突，请刷新后重试',
  429: '操作太频繁，请稍后再试',
  500: '服务异常，请稍后重试',
  502: '服务暂时不可用',
  503: '服务暂时不可用',
  504: '网络超时，请重试'
}

// ─── 纯 uni.request 封装 (给重试用, 不走 request.js 的 toast 分支) ───

function rawRequest(options, timeoutMs) {
  return new Promise((resolve, reject) => {
    const token = getToken()
    const task = uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      timeout: timeoutMs,
      header: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(options.header || {})
      },
      success: (res) => resolve(res),
      fail: (err) => reject({ __network: true, err })
    })
    // 可选: 保留 task 引用用于 abort, 当前未暴露
    return task
  })
}

// ─── 重试判定 ───

function isRetryable(res, err) {
  if (err && err.__network) return true  // timeout / 网络错误
  if (!res) return true
  if (RETRYABLE_HTTP.indexOf(res.statusCode) >= 0) return true
  return false
}

function sleep(ms) {
  return new Promise((r) => setTimeout(r, ms))
}

/**
 * 指数退避延迟: 300ms * 2^n (+随机 jitter)
 */
function backoffDelay(attempt) {
  const base = 300 * Math.pow(2, attempt)
  const jitter = Math.random() * 200
  return Math.min(base + jitter, 5000)
}

// ─── 核心: 可重试请求 ───

/**
 * @param {Object} options 同 api/request.js options (url/method/data/header/silent)
 * @param {Object} [retryOpts]
 * @param {number} [retryOpts.maxRetries=3]
 * @param {number} [retryOpts.timeoutMs=15000]
 * @returns 业务 data (code==200 时), 失败 reject
 */
export async function retryableRequest(options, retryOpts = {}) {
  const maxRetries = retryOpts.maxRetries != null ? retryOpts.maxRetries : DEFAULT_MAX_RETRIES
  const timeoutMs = retryOpts.timeoutMs || DEFAULT_TIMEOUT_MS

  let attempt = 0
  let lastErr = null

  while (attempt <= maxRetries) {
    let res = null
    let err = null
    try {
      res = await rawRequest(options, timeoutMs)
    } catch (e) {
      err = e
    }

    // 可重试场景 → 退避后再来
    if (isRetryable(res, err) && attempt < maxRetries) {
      await sleep(backoffDelay(attempt))
      attempt += 1
      lastErr = err || { statusCode: res && res.statusCode }
      continue
    }

    // 不可重试 或 已到次数上限
    if (err) {
      if (!options.silent) {
        showToast('error', '网络不稳定，请检查后重试')
      }
      return Promise.reject(err)
    }

    return processResponse(res, options)
  }

  return Promise.reject(lastErr || new Error('request failed'))
}

// ─── 响应后处理 (对齐 request.js 语义) ───

function processResponse(res, options) {
  const body = res.data

  // 1. 401 → 跳登录
  if (res.statusCode === 401 || (body && body.code === 401)) {
    clearToken()
    uni.reLaunch({ url: '/pages/login/login' })
    return Promise.reject(new Error('未登录'))
  }

  // 2. 业务成功
  if (body && body.code === 200) {
    const isPaged = Object.prototype.hasOwnProperty.call(body, 'rows')
      || Object.prototype.hasOwnProperty.call(body, 'total')
    return Promise.resolve(isPaged ? body : (body.data !== undefined ? body.data : body))
  }

  // 3. 业务失败 → 映射文案
  const code = (body && body.code) || res.statusCode
  const mapped = BIZ_CODE_MESSAGES[code]
  if (!options.silent) {
    const text = mapped || (body && body.msg) || '操作失败'
    showToast('error', text)
  }
  return Promise.reject(body || { code, msg: mapped })
}

// ─── 工具: 403 / 500 场景配合 modal 阻塞 ───

/**
 * 阻塞式错误提示 (用于支付失败等必须用户确认的场景)
 */
export async function blockingAlert(bizCode, fallbackMsg) {
  const msg = BIZ_CODE_MESSAGES[bizCode] || fallbackMsg || '操作失败'
  await showModalAsync({
    title: '提示',
    content: msg,
    showCancel: false,
    confirmText: '我知道了'
  })
}

export default {
  retryableRequest,
  blockingAlert,
  BIZ_CODE_MESSAGES
}
