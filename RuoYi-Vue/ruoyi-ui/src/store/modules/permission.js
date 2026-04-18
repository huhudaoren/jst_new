import auth from '@/plugins/auth'
import router, { constantRoutes, dynamicRoutes } from '@/router'
import { getRouters } from '@/api/menu'
import Layout from '@/layout/index'
import ParentView from '@/components/ParentView'
import InnerLink from '@/layout/components/InnerLink'

const JST_ROOT_TITLE = '竞赛通管理'
const JST_FLAT_FEATURE_PATHS = [
  '/jst/dashboard',
  '/jst/contest',
  '/jst/enroll',
  '/jst/partner-apply',
  '/jst/course',
  '/jst/form-template',
  '/jst/coupon/template',
  '/jst/rights/template',
  '/jst/mall',
  '/jst/mall/exchange',
  '/jst/notice'
]
const JST_OLD_GROUP_TITLES = new Set([
  '用户与身份',
  '赛事与报名',
  '订单与资金',
  '渠道与返点',
  '积分与商城',
  '营销中心',
  '消息中心',
  '风控中心',
  '财务中心'
])
const JST_MENU_GROUPS = [
  {
    key: 'overview',
    title: '核心运营',
    icon: 'el-icon-data-analysis',
    matcher: (path) => (
      path === '/jst/dashboard' ||
      path === '/jst/contest' ||
      path === '/jst/enroll' ||
      path === '/jst/partner-apply' ||
      path === '/jst/course' ||
      path === '/jst/form-template' ||
      path === '/jst/notice' ||
      path.startsWith('/jst/event/')
    )
  },
  {
    key: 'user-channel',
    title: '用户与渠道',
    icon: 'el-icon-user',
    matcher: (path) => (
      path.startsWith('/jst/user') ||
      path.startsWith('/jst/participant') ||
      path.startsWith('/jst/channel')
    )
  },
  {
    key: 'marketing',
    title: '营销与权益',
    icon: 'el-icon-present',
    matcher: (path) => (
      path.startsWith('/jst/coupon') ||
      path.startsWith('/jst/rights') ||
      path.startsWith('/jst/marketing')
    )
  },
  {
    key: 'points-mall',
    title: '积分与商城',
    icon: 'el-icon-coin',
    matcher: (path) => (
      path.startsWith('/jst/points') ||
      path === '/jst/mall' ||
      path.startsWith('/jst/mall/')
    )
  },
  {
    key: 'order-finance',
    title: '订单与财务',
    icon: 'el-icon-wallet',
    matcher: (path) => (
      path.startsWith('/jst/order') ||
      path.startsWith('/jst/finance')
    )
  },
  {
    key: 'risk',
    title: '风控与审计',
    icon: 'el-icon-warning',
    matcher: (path) => path.startsWith('/jst/risk')
  }
]

const permission = {
  state: {
    routes: [],
    addRoutes: [],
    defaultRoutes: [],
    topbarRouters: [],
    sidebarRouters: []
  },
  mutations: {
    SET_ROUTES: (state, routes) => {
      state.addRoutes = routes
      state.routes = constantRoutes.concat(routes)
    },
    SET_DEFAULT_ROUTES: (state, routes) => {
      state.defaultRoutes = constantRoutes.concat(routes)
    },
    SET_TOPBAR_ROUTES: (state, routes) => {
      state.topbarRouters = routes
    },
    SET_SIDEBAR_ROUTERS: (state, routes) => {
      state.sidebarRouters = routes
    },
  },
  actions: {
    // 生成路由
    GenerateRoutes({ commit, rootState }) {
      return new Promise(resolve => {
        getRouters().then(res => {
          const sdata = JSON.parse(JSON.stringify(res.data))
          const rdata = JSON.parse(JSON.stringify(res.data))
          const sidebarRoutes = dedupeJstRootRoutes(filterAsyncRouter(sdata))
          const rewriteRoutes = dedupeJstRootRoutes(filterAsyncRouter(rdata, false, true))
          const asyncRoutes = filterDynamicRoutes(dynamicRoutes, rootState.user.roles || [])
          rewriteRoutes.push({ path: '*', redirect: '/404', hidden: true })
          router.addRoutes(asyncRoutes)
          commit('SET_ROUTES', rewriteRoutes)
          commit('SET_SIDEBAR_ROUTERS', constantRoutes.concat(sidebarRoutes))
          commit('SET_DEFAULT_ROUTES', sidebarRoutes)
          commit('SET_TOPBAR_ROUTES', sidebarRoutes)
          resolve(rewriteRoutes)
        })
      })
    }
  }
}

function normalizePath(path) {
  if (!path) return '/'
  const result = path.trim().replace(/\/+/g, '/')
  if (result === '/') return result
  return result.endsWith('/') ? result.slice(0, -1) : result
}

function toAbsolutePath(path, parentPath) {
  const normalizedPath = normalizePath(path || '')
  if (normalizedPath.startsWith('/')) {
    return normalizedPath
  }
  const normalizedParent = normalizePath(parentPath || '/')
  return normalizePath(`${normalizedParent}/${normalizedPath}`)
}

function isJstRootRoute(route) {
  const routePath = normalizePath(route && route.path ? route.path : '')
  const routeTitle = route && route.meta ? route.meta.title : ''
  return routePath === '/jst' || routeTitle === JST_ROOT_TITLE
}

function collectRouteSnapshot(route) {
  const snapshot = {
    directPaths: new Set(),
    allPaths: new Set(),
    directGroupTitles: []
  }
  const parentPath = normalizePath(route.path || '/')

  const walk = (nodes, basePath, depth = 0) => {
    if (!Array.isArray(nodes) || !nodes.length || depth > 4) {
      return
    }
    nodes.forEach(node => {
      const currentPath = toAbsolutePath(node.path || '', basePath)
      snapshot.allPaths.add(currentPath)
      if (depth === 0) {
        snapshot.directPaths.add(currentPath)
        const title = node && node.meta ? node.meta.title : ''
        if (title) {
          snapshot.directGroupTitles.push(title)
        }
      }
      walk(node.children, currentPath, depth + 1)
    })
  }

  walk(route.children, parentPath)
  return snapshot
}

function scoreJstRoute(route) {
  const snapshot = collectRouteSnapshot(route)
  let directFeatureHits = 0
  let allFeatureHits = 0

  JST_FLAT_FEATURE_PATHS.forEach(featurePath => {
    if (snapshot.directPaths.has(featurePath)) {
      directFeatureHits += 1
    }
    if (snapshot.allPaths.has(featurePath)) {
      allFeatureHits += 1
    }
  })

  const oldGroupPenalty = snapshot.directGroupTitles.filter(title => JST_OLD_GROUP_TITLES.has(title)).length
  const childCount = Array.isArray(route.children) ? route.children.length : 0

  return (directFeatureHits * 100) + (allFeatureHits * 10) + childCount - (oldGroupPenalty * 30)
}

function dedupeJstRootRoutes(routes) {
  if (!Array.isArray(routes) || routes.length < 2) {
    return routes
  }
  const candidates = routes
    .map((route, index) => ({ route, index }))
    .filter(item => isJstRootRoute(item.route))

  if (candidates.length <= 1) {
    return routes
  }

  let best = candidates[0]
  let bestScore = scoreJstRoute(best.route)
  for (let i = 1; i < candidates.length; i += 1) {
    const current = candidates[i]
    const score = scoreJstRoute(current.route)
    if (score > bestScore) {
      best = current
      bestScore = score
    }
  }

  return routes.filter((route, index) => {
    if (!isJstRootRoute(route)) {
      return true
    }
    return index === best.index
  })
}

function groupJstRootChildren(routes) {
  if (!Array.isArray(routes) || !routes.length) {
    return routes
  }
  const jstRoot = routes.find(route => isJstRootRoute(route))
  if (!jstRoot || !Array.isArray(jstRoot.children) || jstRoot.children.length < 14) {
    return routes
  }

  const hiddenChildren = []
  const groupedBuckets = {}
  JST_MENU_GROUPS.forEach(group => {
    groupedBuckets[group.key] = []
  })
  const fallbackChildren = []

  jstRoot.children.forEach(child => {
    if (!child || child.hidden === true) {
      hiddenChildren.push(child)
      return
    }
    const childPath = toAbsolutePath(child.path || '', '/jst')
    const matchedGroup = JST_MENU_GROUPS.find(group => group.matcher(childPath))
    if (matchedGroup) {
      groupedBuckets[matchedGroup.key].push(child)
    } else {
      fallbackChildren.push(child)
    }
  })

  const groupedChildren = JST_MENU_GROUPS
    .filter(group => groupedBuckets[group.key].length > 0)
    .map(group => ({
      path: `group-${group.key}`,
      component: ParentView,
      hidden: false,
      alwaysShow: true,
      redirect: 'noRedirect',
      meta: {
        title: group.title,
        icon: group.icon
      },
      children: groupedBuckets[group.key]
    }))

  if (fallbackChildren.length > 0) {
    groupedChildren.push({
      path: 'group-other',
      hidden: false,
      alwaysShow: true,
      redirect: 'noRedirect',
      meta: {
        title: '其他功能',
        icon: 'el-icon-more'
      },
      children: fallbackChildren
    })
  }

  jstRoot.children = groupedChildren.concat(hiddenChildren)
  return routes
}

// 遍历后台传来的路由字符串，转换为组件对象
function filterAsyncRouter(asyncRouterMap, lastRouter = false, type = false) {
  return asyncRouterMap.filter(route => {
    if (type && route.children) {
      route.children = filterChildren(route.children)
    }
    if (route.component) {
      // Layout ParentView 组件特殊处理
      if (route.component === 'Layout') {
        route.component = Layout
      } else if (route.component === 'ParentView') {
        route.component = ParentView
      } else if (route.component === 'InnerLink') {
        route.component = InnerLink
      } else {
        route.component = loadView(route.component)
      }
    }
    if (route.children != null && route.children && route.children.length) {
      route.children = filterAsyncRouter(route.children, route, type)
    } else {
      delete route['children']
      delete route['redirect']
    }
    return true
  })
}

function filterChildren(childrenMap, lastRouter = false) {
  var children = []
  childrenMap.forEach(el => {
    el.path = lastRouter ? lastRouter.path + '/' + el.path : el.path
    if (el.children && el.children.length && el.component === 'ParentView') {
      children = children.concat(filterChildren(el.children, el))
    } else {
      children.push(el)
    }
  })
  return children
}

function hasPermission(roles, route) {
  const routeRoles = (route.meta && route.meta.roles) || route.roles
  if (routeRoles && routeRoles.length > 0) {
    return roles.some(role => routeRoles.includes(role))
  }
  return true
}

// 动态路由遍历，验证是否具备权限
export function filterDynamicRoutes(routes, roles = []) {
  const res = []
  routes.forEach(route => {
    const tmp = { ...route }
    const hasPermi = !tmp.permissions || auth.hasPermiOr(tmp.permissions)
    const hasRole = hasPermission(roles, tmp)

    if (hasPermi && hasRole) {
      if (tmp.children && tmp.children.length) {
        tmp.children = filterDynamicRoutes(tmp.children, roles)
      }
      res.push(tmp)
    }
  })
  return res
}

export const loadView = (view) => {
  if (process.env.NODE_ENV === 'development') {
    return (resolve) => require([`@/views/${view}`], resolve)
  } else {
    // 使用 import 实现生产环境的路由懒加载
    return () => import(`@/views/${view}`)
  }
}

export default permission
