/**
 * 用户 Store (Pinia)
 * 注意: 如果你的 Uniapp 项目还没装 Pinia,先 npm i pinia,
 *        然后在 main.js: import { createPinia } from 'pinia'; app.use(createPinia())
 */
import { defineStore } from 'pinia'
import { wxLogin, getProfile, logout, setToken, clearToken, getToken } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    userInfo: null,    // { userId, nickname, avatar, mobileMasked, ... }
    isNewUser: false
  }),

  actions: {
    /**
     * 登录: 调微信 wx.login → 把 code 发后端 → 拿 token
     * 开发期 Mock 模式: code 直接传 MOCK_xxx
     */
    async login(code) {
      const res = await wxLogin(code)
      this.token = res.token
      this.userInfo = res.userInfo
      this.isNewUser = res.isNewUser
      setToken(res.token)
      return res
    },

    /** 获取/刷新当前用户资料 */
    async fetchProfile() {
      if (!this.token) return null
      const profile = await getProfile()
      this.userInfo = profile
      return profile
    },

    async doLogout() {
      try { await logout() } catch (e) {}
      this.token = ''
      this.userInfo = null
      clearToken()
    }
  }
})
