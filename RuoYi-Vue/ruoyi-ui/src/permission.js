import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register', '/partner-apply', '/partner-apply/form', '/partner-apply/status']

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

const isPartnerUser = () => {
  return store.getters.roles.includes('jst_partner')
}

const shouldUsePartnerHome = (to) => {
  return isPartnerUser() && (to.path === '/' || to.path === '/index')
}

const getDefaultHomePath = () => {
  return isPartnerUser() ? '/partner/home' : '/'
}

const APP_TITLE = '竞赛通'

const setPageTitle = (to) => {
  const pageTitle = to && to.meta && to.meta.title ? String(to.meta.title) : ''
  document.title = pageTitle ? `${pageTitle} - ${APP_TITLE}` : APP_TITLE
}

router.beforeEach((to, from, next) => {
  NProgress.start()
  setPageTitle(to)
  if (getToken()) {
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
