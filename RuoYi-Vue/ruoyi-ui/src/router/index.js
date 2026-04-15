import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

// Public routes
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register'),
    hidden: true
  },
  {
    path: '/partner-apply',
    component: () => import('@/views/partner-apply/index'),
    hidden: true,
    meta: { title: '赛事方申请' }
  },
  {
    path: '/partner-apply/form',
    component: () => import('@/views/partner-apply/form'),
    hidden: true,
    meta: { title: '申请表单' }
  },
  {
    path: '/partner-apply/status',
    component: () => import('@/views/partner-apply/status'),
    hidden: true,
    meta: { title: '申请状态' }
  },
  {
    path: '/404',
    component: () => import('@/views/error/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: '/jst/dashboard',
    children: [
      {
        path: 'index',
        component: () => import('@/views/index'),
        name: 'Index',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/lock',
    component: () => import('@/views/lock'),
    hidden: true,
    meta: { title: '锁屏' }
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [
      {
        path: 'profile',
        component: () => import('@/views/system/user/profile/index'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  }
]

// Dynamic routes (hidden sub-pages not in backend menu tree)
export const dynamicRoutes = [
  {
    path: '/partner',
    component: Layout,
    hidden: true,
    permissions: ['jst:partner:contest:list'],
    children: [
      {
        path: 'contest-edit',
        component: () => import('@/views/partner/contest-edit'),
        name: 'PartnerContestCreate',
        meta: { title: '新建赛事', activeMenu: '/partner/contest-list' }
      },
      {
        path: 'contest-edit/:contestId(\\d+)',
        component: () => import('@/views/partner/contest-edit'),
        name: 'PartnerContestEdit',
        meta: { title: '编辑赛事', activeMenu: '/partner/contest-list' }
      }
    ]
  },
  {
    path: '/jst',
    component: Layout,
    hidden: true,
    permissions: ['jst:event:course:list'],
    children: [
      {
        path: 'course-edit',
        component: () => import('@/views/jst/course/edit'),
        name: 'JstCourseCreate',
        meta: { title: '新增课程', activeMenu: '/jst/course' }
      },
      {
        path: 'course-edit/:courseId(\\d+)',
        component: () => import('@/views/jst/course/edit'),
        name: 'JstCourseEdit',
        meta: { title: '编辑课程', activeMenu: '/jst/course' }
      }
    ]
  },
  {
    path: '/system/user-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:user:edit'],
    children: [
      {
        path: 'role/:userId(\\d+)',
        component: () => import('@/views/system/user/authRole'),
        name: 'AuthRole',
        meta: { title: '分配角色', activeMenu: '/system/user' }
      }
    ]
  },
  {
    path: '/system/role-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:role:edit'],
    children: [
      {
        path: 'user/:roleId(\\d+)',
        component: () => import('@/views/system/role/authUser'),
        name: 'AuthUser',
        meta: { title: '分配用户', activeMenu: '/system/role' }
      }
    ]
  },
  {
    path: '/system/dict-data',
    component: Layout,
    hidden: true,
    permissions: ['system:dict:list'],
    children: [
      {
        path: 'index/:dictId(\\d+)',
        component: () => import('@/views/system/dict/data'),
        name: 'Data',
        meta: { title: '字典数据', activeMenu: '/system/dict' }
      }
    ]
  },
  {
    path: '/monitor/job-log',
    component: Layout,
    hidden: true,
    permissions: ['monitor:job:list'],
    children: [
      {
        path: 'index/:jobId(\\d+)',
        component: () => import('@/views/monitor/job/log'),
        name: 'JobLog',
        meta: { title: '调度日志', activeMenu: '/monitor/job' }
      }
    ]
  },
  {
    path: '/tool/gen-edit',
    component: Layout,
    hidden: true,
    permissions: ['tool:gen:edit'],
    children: [
      {
        path: 'index/:tableId(\\d+)',
        component: () => import('@/views/tool/gen/editTable'),
        name: 'GenEdit',
        meta: { title: '修改生成配置', activeMenu: '/tool/gen' }
      }
    ]
  }
]

// Avoid repeated navigation errors
const routerPush = Router.prototype.push
const routerReplace = Router.prototype.replace

Router.prototype.push = function push(location) {
  return routerPush.call(this, location).catch(err => err)
}

Router.prototype.replace = function replace(location) {
  return routerReplace.call(this, location).catch(err => err)
}

export default new Router({
  mode: 'history',
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})
