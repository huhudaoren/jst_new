/**
 * Pinia 实例 — 单例导出
 * 解决 uni-app Vue 2 小程序端页面组件早于根实例挂载导致
 * "getActivePinia() was called but there was no active Pinia" 的时序问题。
 */
// #ifndef VUE3
import { createPinia, setActivePinia } from 'pinia'
export const pinia = createPinia()
// 显式激活：避免小程序端 Page onShow 早于根实例 beforeCreate 导致 getActivePinia() 空
setActivePinia(pinia)
// #endif
