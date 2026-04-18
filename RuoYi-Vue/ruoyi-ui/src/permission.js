import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register', '/public/partner-apply', '/public/partner-apply/form', '/public/partner-apply/status']
const legacyPublicPartnerApplyPrefix = '/partner-apply'
const publicPartnerApplyPrefix = '/public/partner-apply'

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

const resolveLegacyPublicPartnerApplyPath = (path) => {
  if (path === legacyPublicPartnerApplyPrefix) {
    return publicPartnerApplyPrefix
  }
  if (path.startsWith(`${legacyPublicPartnerApplyPrefix}/`)) {
    return path.replace(legacyPublicPartnerApplyPrefix, publicPartnerApplyPrefix)
  }
  return ''
}

const isPartnerUser = () => {
  return store.getters.roles.includes('jst_partner')
}

const isSalesManager = () => {
  return store.getters.roles.includes('jst_sales_manager')
}

const isSalesUser = () => {
  return store.getters.roles.includes('jst_sales')
}

const shouldUsePartnerHome = (to) => {
  return isPartnerUser() && (to.path === '/' || to.path === '/index')
}

const shouldUseSalesHome = (to) => {
  return !isPartnerUser() && (isSalesUser() || isSalesManager()) && (to.path === '/' || to.path === '/index')
}

const getDefaultHomePath = () => {
  if (isPartnerUser()) return '/partner/home'
  if (isSalesManager()) return '/sales-workbench/manager-dashboard'
  if (isSalesUser()) return '/sales-workbench/index'
  return '/'
}

const APP_TITLE = '竞赛通'

const setPageTitle = (to) => {
  const pageTitle = to && to.meta && to.meta.title ? String(to.meta.title) : ''
  document.title = pageTitle ? `${pageTitle} - ${APP_TITLE}` : APP_TITLE
}

router.beforeEach((to, from, next) => {
  NProgress.start()
  setPageTitle(to)
  const token = getToken()

  if (!token) {
    const redirectedPublicPath = resolveLegacyPublicPartnerApplyPath(to.path || '')
    if (redirectedPublicPath) {
      next({ path: redirectedPublicPath, query: to.query, hash: to.hash, replace: true })
      NProgress.done()
      return
    }
  }

  if (token) {
    const isLock = store.getters.isLock
    if (to.path === '/login') {
      next({ path: getDefaultHomePath() })
      NProgress.done()
    } else if (isWhiteList(to.path)) {
      next()
    } else if (isLock && to.path !== '/lock') {
      next({ path: '/lock' })
      NProgress.done()
    } else if (!isLock && to.path === '/lock') {
      next({ path: '/' })
      NProgress.done()
    } else if (store.getters.roles.length === 0) {
      isRelogin.show = true
      store.dispatch('GetInfo').then(() => {
        isRelogin.show = false
        store.dispatch('GenerateRoutes').then(accessRoutes => {
          router.addRoutes(accessRoutes)
          if (shouldUsePartnerHome(to)) {
            next({ path: '/partner/home', replace: true })
          } else if (shouldUseSalesHome(to)) {
            next({ path: getDefaultHomePath(), replace: true })
          } else {
            next({ ...to, replace: true })
          }
        })
      }).catch(err => {
        store.dispatch('LogOut').then(() => {
          Message.error(err)
          next({ path: '/' })
        })
      })
    } else if (shouldUsePartnerHome(to)) {
      next({ path: '/partner/home', replace: true })
      NProgress.done()
    } else if (shouldUseSalesHome(to)) {
      next({ path: getDefaultHomePath(), replace: true })
      NProgress.done()
    } else {
      next()
    }
  } else if (isWhiteList(to.path)) {
    next()
  } else {
    next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
    NProgress.done()
  }
})

router.afterEach(() => {
  NProgress.done()
})
