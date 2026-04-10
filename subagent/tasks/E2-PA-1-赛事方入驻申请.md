# 任务卡 E2-PA-1 - 赛事方入驻申请页（PC 前端）

## 阶段 / 端
阶段 E 批 2 / **RuoYi-Vue 前端**（ruoyi-ui）

## 背景
PRD §7.6 + §5.5 要求赛事方 Web 工作台，入驻申请是第一个页面。后端 F5 已完成，但 RuoYi-Vue 前端 admin 工程尚无赛事方入口页面。

## 前置条件
- 若依前端工程 `RuoYi-Vue/ruoyi-ui/` 需已可启动（`npm run dev`）
- 若 ruoyi-ui 不存在，本卡需先初始化

## PRD 依据
- §5.5 赛事方入驻闭环
- §7.6 赛事方入驻与工作台
- 原型：`event-apply.html`

## CCB 决策
- Q-04：Web 后台与 H5 管理员同账号，**赛事方独立账号** sys_user 走 `jst_partner` 角色
- Q-08：赛事方数据完全隔离，走 PartnerScope 切面（PA-9 实现）

## 交付物

### 1. 入驻申请入口页（公开）

**新建**：`ruoyi-ui/src/views/partner-apply/index.vue`

独立路由（无需登录可访问）：
- 顶部：平台 logo + "赛事方入驻"
- 欢迎说明 + 权益介绍
- 主按钮：**立即申请**（跳 form）+ **查看申请进度**（输入手机号 + 验证码）

### 2. 入驻申请表单页

**新建**：`ruoyi-ui/src/views/partner-apply/form.vue`

4 步步进器：

#### Step 1 - 基本信息
- 机构名称 `@NotBlank`
- 统一社会信用代码 `@NotBlank + @Pattern`
- 机构类型（赛事方/培训机构/其他）
- 所在省市区
- 详细地址

#### Step 2 - 联系人信息
- 法人姓名
- 法人身份证号
- 联系人姓名
- 联系人手机号
- 联系人邮箱

#### Step 3 - 资质材料上传
- 营业执照照片（必填）
- 组织机构代码证（可选）
- 赛事举办经验说明（textarea 500 字）
- 其他证件附件

#### Step 4 - 确认提交
- 展示所有已填信息（只读）
- 勾选"同意《赛事方合作协议》"
- 提交按钮 → `POST /jst/partner/apply`

### 3. 申请状态查询页

**新建**：`ruoyi-ui/src/views/partner-apply/status.vue`

- 输入手机号 + 短信验证码查询
- 展示当前申请状态：
  - pending 审核中
  - approved 已通过（显示临时账号 + 初始密码）
  - rejected 已驳回（显示驳回原因 + 修改重提按钮）

### 4. 路由注册

**修改**：`ruoyi-ui/src/router/index.js`

添加 3 个公开路由：
```js
{ path: '/partner-apply', component: () => import('@/views/partner-apply/index.vue'), hidden: true, meta: { noAuth: true } },
{ path: '/partner-apply/form', component: () => import('@/views/partner-apply/form.vue'), hidden: true, meta: { noAuth: true } },
{ path: '/partner-apply/status', component: () => import('@/views/partner-apply/status.vue'), hidden: true, meta: { noAuth: true } }
```

**修改**：`ruoyi-ui/src/permission.js` 白名单加入 `/partner-apply*`

### 5. API 封装

**新建**：`ruoyi-ui/src/api/partner/apply.js`

```js
import request from '@/utils/request'

export function submitPartnerApply(body) {
  return request({ url: '/jst/partner/apply', method: 'post', data: body })
}
export function getPartnerApplyStatus(mobile, code) {
  return request({ url: '/jst/partner/apply/status', method: 'get', params: { mobile, code } })
}
export function resubmitPartnerApply(id, body) {
  return request({ url: `/jst/partner/apply/${id}/resubmit`, method: 'post', data: body })
}
```

### 6. 响应式适配（Q-04 决策的 H5 职能）

- 使用 Element UI 的 `el-row :gutter` + 断点
- 手机宽度下（<768px）步进器改为纵向堆叠
- 上传组件在手机下用 `el-upload` 的 `:drag="false"` 禁用拖拽
- 输入框字号 14px min，按钮 high tap area

## DoD
- [ ] 3 个页面 + 路由注册
- [ ] 4 步步进表单完整
- [ ] 资质上传走 OSS 签名
- [ ] 手机浏览器打开可用（响应式）
- [ ] 任务报告 `E2-PA-1-回答.md`

## 不许做
- ❌ 不许改后端（F5 已完成）
- ❌ 不许在申请页要求登录
- ❌ 不许在表单里展示"内部字段"（如 auditor、audit_time 等）

## 依赖：E0-1
## 优先级：P1

---
派发时间：2026-04-10
