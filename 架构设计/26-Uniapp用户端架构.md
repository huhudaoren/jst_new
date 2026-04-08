# 26. Uniapp 用户端架构

> 范围：竞赛通微信小程序（学生 / 家长 / 渠道方/老师）
> 原型：`D:\coding\jst_v1\小程序原型图\` 共 50 张 HTML 页面（视觉蓝图，**非工程**）
> 决策 Q3=本期做（含 VOD 课程）/ Q5=三端齐做（含本端）
> SSOT：与原型图冲突时以 PRD V4.1 为准（CLAUDE.md 强约束）

---

## 1. 技术栈与版本

| 项 | 版本/选择 | 说明 |
|---|---|---|
| 框架 | **Uniapp（Vue 2 版本）** | 与若依 Vue2 admin 端栈一致，复用经验 |
| 编辑器 | HBuilderX | Uniapp 官方 |
| 包管理 | npm | |
| UI 库 | uni-ui + 自定义组件 | uni-ui 不足处自写 |
| 状态管理 | **Pinia**（Uniapp 已支持） | 不用 Vuex |
| 请求库 | uni.request 自封装 | 不引入 axios |
| 路由 | pages.json 静态路由 | Uniapp 标准 |
| 视频播放 | uni-video + 阿里云 VOD JS SDK | 课程视频 |
| 二维码 | uQRCode（生成）+ uni.scanCode（扫描） | 核销 |
| 富文本 | u-parse 或 mp-html | 公告/赛事详情 |
| 地图 | uni.chooseLocation | 收货地址 |

**禁止引入**：vue-router（Uniapp 不用）、axios、Element UI（PC 才用）、Lodash（按需）

---

## 2. 工程目录结构

新建 Uniapp 项目位于 `D:\coding\jst_v1\jst-uniapp\`（与 RuoYi-Vue 同级）：

```
jst-uniapp/
├── manifest.json              # 微信 AppID / 权限声明
├── pages.json                 # 路由 + tabBar
├── App.vue                    # 全局生命周期 / token 自动续期
├── main.js                    # Pinia / 全局组件 / 错误兜底
├── uni.scss                   # 全局 SCSS 变量（color/font/spacing）
│
├── api/                       # 后端接口封装（按业务域）
│   ├── request.js             # 核心请求库 + token + 拦截
│   ├── auth.js                # 登录/注册/token 续期
│   ├── user.js                # 个人资料/绑定
│   ├── contest.js             # 赛事列表/详情/报名
│   ├── enroll.js              # 报名/动态表单
│   ├── order.js               # 订单/支付/退款
│   ├── course.js              # 课程/学习记录
│   ├── points.js              # 积分账户/商城
│   ├── coupon.js              # 优惠券中心
│   ├── cert.js                # 证书/成绩
│   ├── notice.js              # 公告/消息
│   ├── teacher.js             # 渠道方/老师工作台
│   └── appointment.js         # 团队预约/扫码核销
│
├── store/                     # Pinia stores
│   ├── index.js
│   ├── user.js                # 用户信息 + token + 角色 + 当前视角
│   ├── view.js                # 学生/老师 双视角切换
│   ├── cart.js                # 报名/兑换购物车
│   └── config.js              # 字典/全局配置缓存
│
├── pages/                     # ⭐ 5 个 tabBar 主页
│   ├── index/index.vue        # 首页(对应原型 index.html)
│   ├── contest/list.vue       # 赛事(原型 contest-list.html)
│   ├── course/list.vue        # 课程(course-list.html)
│   ├── notice/list.vue        # 公告(notice.html)
│   └── my/index.vue           # 我的(my.html,双视角)
│
├── pages-sub/                 # 子页面分包(微信小程序分包)
│   ├── auth/
│   │   ├── login.vue
│   │   └── profile-edit.vue
│   ├── contest/
│   │   ├── detail.vue         # 赛事详情(contest-detail.html)
│   │   ├── enroll.vue         # 报名流程(enroll.html)
│   │   └── search.vue
│   ├── course/
│   │   ├── detail.vue
│   │   └── player.vue         # 视频播放,集成 VOD
│   ├── notice/
│   │   ├── detail.vue
│   │   └── message.vue        # 我的消息
│   ├── my/
│   │   ├── enroll.vue         # 我的报名
│   │   ├── order.vue          # 我的订单
│   │   ├── score.vue          # 我的成绩
│   │   ├── cert.vue           # 我的证书
│   │   ├── course.vue         # 我的课程(含 AI 课堂 Tab)
│   │   ├── reserve.vue        # 我的预约
│   │   ├── binding.vue        # 绑定关系
│   │   ├── settings.vue       # 隐私设置
│   │   └── help.vue           # 帮助
│   ├── public/
│   │   ├── score-query.vue    # 公开成绩查询
│   │   └── cert-verify.vue    # 证书验真(扫码直达)
│   ├── teacher/               # 老师/机构子分包
│   │   ├── home.vue           # 老师工作台
│   │   ├── identity.vue       # 身份认证
│   │   ├── students.vue       # 学生管理
│   │   ├── orders.vue         # 老师订单
│   │   ├── data.vue           # 报名数据
│   │   ├── org-home.vue       # 机构工作台
│   │   ├── checkin-scan.vue   # 扫码核销
│   │   ├── writeoff-record.vue # 核销记录
│   │   └── rebate.vue         # 返点结算
│   ├── points/
│   │   ├── center.vue         # 积分中心
│   │   ├── detail.vue         # 积分明细
│   │   ├── mall.vue           # 积分商城
│   │   ├── goods-detail.vue
│   │   └── order.vue
│   ├── coupon/
│   │   ├── center.vue         # 优惠券中心
│   │   └── select.vue         # 选券
│   ├── rights/
│   │   ├── center.vue         # 权益中心
│   │   └── detail.vue
│   ├── maic/                  # OpenMAIC AI 课程
│   │   ├── list.vue
│   │   ├── create.vue
│   │   ├── detail.vue
│   │   ├── stats.vue
│   │   └── classroom.vue
│   └── pay/
│       ├── result.vue         # 支付结果回调
│       └── voucher-upload.vue # 对公转账凭证
│
├── components/                # 公共组件
│   ├── jst-form-render/       # ⭐ 动态表单渲染器(关键!读 enroll_form_template.schema_json)
│   ├── jst-status-tag/        # 状态机标签(订单/退款/认领等)
│   ├── jst-empty/             # 空状态
│   ├── jst-loading/           # 加载占位
│   ├── jst-amount/            # 金额显示(分→元 + 千分位)
│   ├── jst-cert-card/         # 证书卡片
│   ├── jst-contest-card/      # 赛事卡片
│   ├── jst-points-balance/    # 积分余额条
│   ├── jst-team-qrcode/       # 团队预约二维码
│   └── jst-tab-switcher/      # 双视角切换器
│
├── utils/
│   ├── auth.js                # token 存取
│   ├── format.js              # 日期/金额/手机号脱敏格式化
│   ├── upload.js              # 文件上传(走后端 /jst/wx/upload)
│   ├── pay.js                 # 微信支付封装
│   ├── permission.js          # 角色判断
│   └── enums.js               # 业务字典缓存(对应 sys_dict)
│
├── static/                    # 静态资源(图标/默认头像/兜底图)
│
└── styles/                    # 全局样式
    ├── variables.scss         # 设计 token(从 design-system.css 抽取)
    ├── reset.scss
    ├── theme.scss
    └── components.scss
```

**分包策略**：主包 5 个 tab 页（< 2MB），子包按业务域拆分（teacher / maic / pay / points），符合微信小程序 2MB 主包限制。

---

## 3. pages.json 关键配置

```json
{
  "easycom": { "autoscan": true, "custom": { "^jst-(.*)": "@/components/jst-$1/jst-$1.vue" } },

  "pages": [
    { "path": "pages/index/index",   "style": { "navigationBarTitleText": "竞赛通" } },
    { "path": "pages/contest/list",  "style": { "navigationBarTitleText": "赛事" } },
    { "path": "pages/course/list",   "style": { "navigationBarTitleText": "课程" } },
    { "path": "pages/notice/list",   "style": { "navigationBarTitleText": "公告" } },
    { "path": "pages/my/index",      "style": { "navigationBarTitleText": "我的" } }
  ],

  "subPackages": [
    { "root": "pages-sub/auth",    "pages": [...] },
    { "root": "pages-sub/contest", "pages": [...] },
    { "root": "pages-sub/course",  "pages": [...] },
    { "root": "pages-sub/my",      "pages": [...] },
    { "root": "pages-sub/teacher", "pages": [...] },
    { "root": "pages-sub/maic",    "pages": [...] },
    { "root": "pages-sub/points",  "pages": [...] },
    { "root": "pages-sub/coupon",  "pages": [...] },
    { "root": "pages-sub/rights",  "pages": [...] },
    { "root": "pages-sub/pay",     "pages": [...] },
    { "root": "pages-sub/public",  "pages": [...] }
  ],

  "tabBar": {
    "color": "#999", "selectedColor": "#FF6B35", "backgroundColor": "#fff",
    "list": [
      { "pagePath": "pages/index/index",  "iconPath": "static/tab/home.png",   "selectedIconPath": "static/tab/home-on.png",   "text": "首页" },
      { "pagePath": "pages/contest/list", "iconPath": "static/tab/contest.png","selectedIconPath": "static/tab/contest-on.png","text": "赛事" },
      { "pagePath": "pages/course/list",  "iconPath": "static/tab/course.png", "selectedIconPath": "static/tab/course-on.png", "text": "课程" },
      { "pagePath": "pages/notice/list",  "iconPath": "static/tab/notice.png", "selectedIconPath": "static/tab/notice-on.png", "text": "公告" },
      { "pagePath": "pages/my/index",     "iconPath": "static/tab/my.png",     "selectedIconPath": "static/tab/my-on.png",     "text": "我的" }
    ]
  }
}
```

---

## 4. 请求封装（核心）

`api/request.js`：
```javascript
import { useUserStore } from '@/store/user'

const BASE_URL = process.env.UNI_PLATFORM === 'mp-weixin'
  ? 'https://api.jst.example.com'   // 真机
  : 'http://127.0.0.1:8080'          // 开发机

/**
 * 统一请求
 * 强约束:
 *   1. 自动注入 Authorization Bearer token
 *   2. 401 自动跳登录
 *   3. 业务错误码统一弹 toast
 *   4. 禁止任何接口绕过本封装
 */
export default function request(options) {
  const userStore = useUserStore()
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        ...(userStore.token ? { Authorization: `Bearer ${userStore.token}` } : {}),
        ...(options.header || {})
      },
      success: (res) => {
        const body = res.data
        if (res.statusCode === 401) {
          userStore.logout()
          uni.reLaunch({ url: '/pages-sub/auth/login' })
          return reject(new Error('未登录'))
        }
        if (body.code === 200) {
          resolve(body.data)
        } else {
          if (!options.silent) uni.showToast({ title: body.msg || '操作失败', icon: 'none' })
          reject(body)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络异常', icon: 'none' })
        reject(err)
      }
    })
  })
}
```

`api/auth.js`：
```javascript
import request from './request'

export const wxLogin   = (code) => request({ url: '/jst/wx/auth/login', method: 'POST', data: { code } })
export const bindPhone = (data) => request({ url: '/jst/wx/auth/bind-phone', method: 'POST', data })
export const getProfile = () => request({ url: '/jst/wx/user/profile', method: 'GET' })
export const updateProfile = (data) => request({ url: '/jst/wx/user/profile', method: 'PUT', data })
```

---

## 5. 登录流程（完整链路）

```
[小程序启动]
   ↓
App.vue onLaunch → 检查本地 token
   ↓
  ┌── 有 token → 调 /jst/wx/user/profile 验证有效性
  │       ↓ 成功 → 进入 index
  │       ↓ 401  → 清空 token,跳 login
  │
  └── 无 token → 跳 login
                     ↓
                  用户点登录按钮 → wx.login → code
                     ↓
                  POST /jst/wx/auth/login {code}
                     ↓
                  后端: code → openid/unionid → 查/创 jst_user → 颁发 JWT
                     ↓
                  返回 {token, userInfo, isNewUser}
                     ↓
                  存 token 到 storage + Pinia
                     ↓
                  isNewUser=true → 跳 profile-edit (强制完善资料)
                  isNewUser=false → 跳 index
```

**强约束**：
- 用户**首次登录不得自动写入 mobile**，必须显式 `bindPhone`（getPhoneNumber 授权）
- token 失效后自动续期由后端决定（若依 JWT 默认 30min，refresh 在 header 中返回新 token）

---

## 6. 双视角切换（学生 / 老师）

PRD 要求 my.html 一个用户可能同时是学生和渠道方（老师），通过页面顶部 Tab 切换视角。

`store/view.js`：
```javascript
import { defineStore } from 'pinia'

export const useViewStore = defineStore('view', {
  state: () => ({ currentView: 'student' }),  // student | teacher
  actions: {
    switchView(v) {
      if (!['student','teacher'].includes(v)) return
      this.currentView = v
      uni.setStorageSync('jst-view', v)
    }
  }
})
```

`my/index.vue`：根据 `useUserStore().roles` 是否含 `channel` 决定是否显示视角切换器。无渠道身份则只显示学生视角。

---

## 7. 动态表单渲染器（jst-form-render）⭐ 核心组件

PRD 报名流程使用 `jst_enroll_form_template.schema_json` 动态字段。前端必须有一个**通用渲染器**，根据 schema 渲染：

```vue
<!-- components/jst-form-render/jst-form-render.vue -->
<template>
  <view>
    <view v-for="field in schema.fields" :key="field.key" class="form-item">
      <component
        :is="getComponent(field.type)"
        :field="field"
        :value="formValues[field.key]"
        @input="onInput(field.key, $event)"
      />
    </view>
  </view>
</template>
```

支持字段类型（与 PRD 一致）：
`text/textarea/radio/checkbox/select/date/age/number/image/audio/video/file/group/conditional`

报名提交时：
```javascript
// 上传 form_snapshot_json,后端原样存入 enroll_record.form_snapshot_json
await enrollApi.submit({
  contestId,
  participantId,
  templateId: schema.template_id,
  templateVersion: schema.template_version,
  formData: formValues,
  attachments: uploadedFileUrls
})
```

---

## 8. 原型图 → Uniapp 页面映射表

| HTML 原型 | Uniapp 路径 | 关联接口 |
|---|---|---|
| index.html | pages/index/index | /jst/wx/index/banner, /jst/wx/contest/hot |
| login.html | pages-sub/auth/login | /jst/wx/auth/login |
| contest-list.html | pages/contest/list | /jst/wx/contest/list |
| contest-detail.html | pages-sub/contest/detail | /jst/wx/contest/{id} |
| enroll.html | pages-sub/contest/enroll | /jst/wx/enroll/template, /jst/wx/enroll/submit |
| my.html | pages/my/index | /jst/wx/user/profile |
| my-enroll.html | pages-sub/my/enroll | /jst/wx/my/enroll |
| my-order.html | pages-sub/my/order | /jst/wx/my/order |
| my-score.html | pages-sub/my/score | /jst/wx/my/score |
| my-cert.html | pages-sub/my/cert | /jst/wx/my/cert |
| my-course.html | pages-sub/my/course | /jst/wx/my/course |
| course-detail.html | pages-sub/course/detail | /jst/wx/course/{id} |
| course-player.html | pages-sub/course/player | /jst/wx/course/play-auth, /jst/wx/course/learn-record |
| teacher-home.html | pages-sub/teacher/home | /jst/wx/teacher/home |
| teacher-students.html | pages-sub/teacher/students | /jst/wx/teacher/students |
| teacher-orders.html | pages-sub/teacher/orders | /jst/wx/teacher/orders |
| teacher-data.html | pages-sub/teacher/data | /jst/wx/teacher/stats |
| checkin-scan.html | pages-sub/teacher/checkin-scan | /jst/wx/appointment/team/writeoff |
| writeoff-record.html | pages-sub/teacher/writeoff-record | /jst/wx/appointment/writeoff/list |
| points-center.html | pages-sub/points/center | /jst/wx/points/account |
| points-mall.html | pages-sub/points/mall | /jst/wx/mall/goods/list |
| coupon-center.html | pages-sub/coupon/center | /jst/wx/coupon/my |
| reserve.html | pages-sub/contest/reserve | /jst/wx/appointment/create |
| my-reserve.html | pages-sub/my/reserve | /jst/wx/my/appointment |
| score-query.html | pages-sub/public/score-query | /jst/public/score/query |
| cert-verify.html | pages-sub/public/cert-verify | /jst/public/cert/verify |
| 其余 ... | 同类映射 | 详见 27 文档 |

**所有 admin-*.html 不映射到本工程**——它们对应 H5 审核端，见 28 文档；功能上 PC RuoYi 端已覆盖，本期可能不做 H5 审核独立工程。

---

## 9. 与 PRD 原型冲突的处理

CLAUDE.md 强约束：**原型图与 PRD 冲突以 PRD 为准**。

具体落地：
- 视觉/布局/icon → 沿用原型 HTML
- 字段名称/类型/必填 → 以 PRD 为准
- 状态枚举值/状态机跃迁 → 以 PRD/状态机文档为准
- 业务流程（资金流/审批流/退款顺序）→ 以 PRD 为准
- 任何金额计算/积分换算 → 以 PRD 为准

---

## 10. 测试数据规约（沿用 §3.8）

**禁止**在 .vue 文件 `data()` 写死示例数据。所有列表必须从真实接口拉取，接口未实现时由后端提供 mock 数据库（参见 99-test-fixtures.sql）。

---

## 11. 与原型图的 HTML 复用策略

由于原型已有 50 张完成度极高的 HTML，开发流程建议：

1. 用浏览器打开原型 HTML，复制 `<body>` 主结构
2. 把 HTML class 转换为 Uniapp 的 view + class
3. CSS 来自 `design-system.css`，复制到 `styles/` 目录后改 SCSS
4. 把 onclick="location.href='xxx.html'" 改为 `@tap="navigateTo('/pages-sub/...')"`
5. 接入对应 API，删除写死数据

**禁止**直接把 HTML 当 web-view 嵌入小程序——必须重写为 Uniapp 组件。

---

## 12. 工程初始化检查清单（Agent 落地）

- [ ] HBuilderX 新建 Uniapp 项目（Vue2 + 默认）
- [ ] 安装 Pinia for uniapp
- [ ] 复制 `小程序原型图/design-system.css` 到 `styles/design-system.scss` 改造
- [ ] 配置 manifest.json 微信 AppID
- [ ] 创建 `api/request.js` + `store/user.js`
- [ ] 实现 `pages-sub/auth/login.vue` 完成首个端到端流程
- [ ] 配 99-test-fixtures.sql 的测试用户登录跑通
