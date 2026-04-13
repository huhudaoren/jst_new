# QUALITY-FIX-MP — 小程序质量批量修复

> 优先级：P0 | 预估：S | Agent：MiniProgram Agent

---

## 一、背景

质量审计发现小程序 16 处面向用户的体验问题：Mock 按钮暴露、开发阶段"联调"技术文案未清理、静默错误吃掉异常、硬编码颜色。本卡一次性修复。

## 二、修复清单

### A. 移除开发态残留（HIGH，5 处）

| # | 文件 | 当前问题 | 修复方式 |
|---|---|---|---|
| 1 | `pages/login/login.vue` | "Mock 登录（测试账号）"按钮对用户可见 | 用 `v-if="isDev"` 包裹 Mock 按钮（`isDev` 判断 `process.env.NODE_ENV === 'development'`），生产环境不显示 |
| 2 | `pages/login/login.vue` | "Mock 按钮用于联调测试账号；微信登录按钮保留生产占位" 描述文字 | **删除该段描述文字** |
| 3 | `pages/login/login.vue` | 错误提示 "请检查后端或测试数据" | **改为 "登录失败，请稍后重试"** |
| 4 | `pages-sub/contest/detail.vue` | "赛事详情暂未返回，待后端 F7 联调完成后自动展示。" | **改为 "暂无赛事详情"** |
| 5 | `pages-sub/contest/enroll.vue` | "赛事待联调" + "报名模板暂未返回，待 F8/F9 接口联调完成后自动展示。" | **改为 "暂无赛事信息" / "暂无报名表单"** |

### B. 清理技术文案（MEDIUM，6 处）

| # | 文件 | 当前文案 | 改为 |
|---|---|---|---|
| 1 | `pages-sub/my/order-detail.vue` | "订单详情暂未返回。" | "暂无订单详情" |
| 2 | `pages-sub/my/refund-detail.vue` | "退款详情暂未返回。" | "暂无退款详情" |
| 3 | `pages-sub/my/enroll-detail.vue` | "报名详情暂未返回。" | "暂无报名详情" |
| 4 | `pages-sub/my/profile-edit.vue` | "头像上传能力暂未接入" toast | "头像上传即将开放" |
| 5 | `pages-sub/channel/participants.vue` 约 244 行 | "详情页待后续实现" toast | "暂不支持查看详情" |
| 6 | `pages-sub/mall/detail.vue` 约 139 行 | 注释 "mock 支付立即成功 (开发期)" | 注释改为 "// 支付成功回调"（不改逻辑，Mock/Real 由后端 jst.wxpay.enabled 控制） |

### C. 硬编码颜色修复（MEDIUM，3 处）

| # | 文件 | 硬编码 | 修复 |
|---|---|---|---|
| 1 | `pages/contest/list.vue` 约 27 行 | `'color:' + '#B0B8CC'` 拼接 | 改为 `'color:' + '#B0B8CC'` → 使用 CSS class placeholder 颜色，或直接保持（u-search 组件 prop 限制时可保持） |
| 2 | `pages/notice/list.vue` 约 112 行 | `textSecondary: '#8A8A8A'` 在 data 中 | 如果是传给 u-icon 的 color prop，可保持（uView prop 不支持 SCSS 变量）；如果是 style 绑定，改为 class |
| 3 | `pages-sub/points/center.vue` 约 136 行 | `#7B1FA2` 在 CSS gradient 中 | **改为 `$jst-purple`**（这个在 style 块中，可以用变量） |

**判断原则**：
- uView 组件的 `color` prop 传入硬编码 hex → **保持**（组件 prop 不支持 SCSS）
- `<style>` 块中的 hex → **必须改为 $jst-* token**
- JS data 中定义给 `:style` 绑定的 hex → 可保持（运行时绑定无法用 SCSS）

### D. 静默错误加提示（MEDIUM，5 处）

以下页面的 catch 块为空或仅 `this.xxx = null`，用户看不到任何错误反馈。

**统一改为**：
```javascript
catch (e) {
  uni.showToast({ title: '加载失败，请重试', icon: 'none' })
  // 保留原有的默认值赋值
}
```

| # | 文件 |
|---|---|
| 1 | `pages-sub/contest/detail.vue`（约 190 行） |
| 2 | `pages-sub/contest/enroll.vue`（多处 catch） |
| 3 | `pages-sub/course/detail.vue` |
| 4 | `pages-sub/my/address-edit.vue` |
| 5 | `pages-sub/my/address-list.vue` |

## 三、关键约束

- **script 业务逻辑不动**（只改文案字符串、加 catch toast、加 v-if 条件）
- **不改 RuoYi-Vue 后端**
- **不改 api/ 接口层**
- **不改 pages.json 路由**
- 改完后 grep 确认零 "F7"/"F8"/"F9"/"联调"/"测试账号"/"测试数据" 残留（排除 test/ 目录和 CLAUDE.md）

## 四、验证方法

```bash
# 确认零开发态残留
grep -r "联调\|测试账号\|测试数据\|F7\|F8/F9" jst-uniapp/pages/ jst-uniapp/pages-sub/ --include="*.vue" -l
# 预期结果：0 个文件

# 确认零空 catch
grep -rn "catch.*{.*}" jst-uniapp/pages/ jst-uniapp/pages-sub/ --include="*.vue" | grep -v showToast | grep -v msgError | grep -v console
# 手动检查结果中是否有空 catch
```

## 五、DoD

- [ ] 5 处开发态残留修复（Mock 按钮隐藏、技术文案删除）
- [ ] 6 处用户可见技术文案改为友好文案
- [ ] 3 处可修的硬编码颜色修复（style 块中的）
- [ ] 5 处静默 catch 加 toast 提示
- [ ] grep 验证零残留
- [ ] script 业务逻辑未改动
- [ ] 报告交付
