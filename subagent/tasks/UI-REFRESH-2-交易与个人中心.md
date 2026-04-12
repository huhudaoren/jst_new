# 任务卡 UI-REFRESH-2 - 交易与个人中心页面刷新（批次 2）

## 阶段
阶段 F / **jst-uniapp**（UI Refresh Agent）

## 背景
UI-REFRESH-1 完成了 5 个主包高频页的刷新。本卡覆盖学生核心路径：赛事详情→报名→支付→订单→退款→个人信息，共 23 页。这些是用户完成交易闭环必经的页面。

## 系统提示词
使用 `subagent/UI_REFRESH_AGENT_PROMPT.md`

## 必读
1. `subagent/UI_REFRESH_AGENT_PROMPT.md`
2. `jst-uniapp/styles/design-tokens.scss`
3. 各页面对应原型 PNG（`小程序原型图/`）
4. `subagent/tasks/任务报告/UI-REFRESH-1-回答.md` — 了解批次 1 建立的视觉标杆

## 刷新页面清单（23 页）

### A. 赛事与报名（3 页）
| # | 路径 | 说明 |
|---|------|------|
| 1 | `pages-sub/contest/detail.vue` | 赛事详情（核心转化页） |
| 2 | `pages-sub/contest/enroll.vue` | 报名表单 |
| 3 | `pages/login/login.vue` | 登录页 |

### B. 订单与退款（6 页）
| # | 路径 | 说明 |
|---|------|------|
| 4 | `pages-sub/my/order-list.vue` | 订单列表 |
| 5 | `pages-sub/my/order-detail.vue` | 订单详情 |
| 6 | `pages-sub/my/refund-list.vue` | 退款列表 |
| 7 | `pages-sub/my/refund-detail.vue` | 退款详情 |
| 8 | `pages-sub/my/enroll.vue` | 我的报名列表 |
| 9 | `pages-sub/my/enroll-detail.vue` | 报名详情 |

### C. 个人中心子页（10 页）
| # | 路径 | 说明 |
|---|------|------|
| 10 | `pages-sub/my/profile-edit.vue` | 编辑资料 |
| 11 | `pages-sub/my/participant.vue` | 参赛人管理 |
| 12 | `pages-sub/my/binding.vue` | 渠道绑定 |
| 13 | `pages-sub/my/score.vue` | 我的成绩 |
| 14 | `pages-sub/my/cert.vue` | 我的证书 |
| 15 | `pages-sub/my/message.vue` | 消息中心 |
| 16 | `pages-sub/my/course.vue` | 我的课程 |
| 17 | `pages-sub/my/settings.vue` | 设置 |
| 18 | `pages-sub/my/privacy.vue` | 隐私协议 |
| 19 | `pages-sub/my/address-list.vue` | 地址列表 |

### D. 优惠券与积分（4 页）
| # | 路径 | 说明 |
|---|------|------|
| 20 | `pages-sub/coupon/center.vue` | 优惠券中心 |
| 21 | `pages-sub/coupon/claimable.vue` | 可领优惠券 |
| 22 | `pages-sub/coupon/select.vue` | 选择优惠券 |
| 23 | `pages-sub/my/address-edit.vue` | 地址编辑 |

## 刷新规则
每个页面按 prompt §五 工作流执行。核心目标：
- 手写 CSS → uView 组件 + jst-* 组件
- 硬编码数值 → token 变量
- 骨架屏 / 空状态 / 加载更多补齐
- 每页至少 1 个有意义的微交互
- **比原型更好**

## 视觉特效
由你自主设计。唯一要求：每页至少 1 个微交互，在报告中说明设计意图。

## DoD
- [ ] 23 个页面全部刷新
- [ ] 零硬编码数值
- [ ] 列表页有骨架屏 + 空状态 + 加载更多
- [ ] 详情页有骨架屏
- [ ] 表单页有合理的交互反馈
- [ ] 编译无报错
- [ ] 业务功能无回归（script 业务逻辑不动）
- [ ] 任务报告 `UI-REFRESH-2-回答.md`

## 不许做
- ❌ 不许改 `<script>` 中的业务逻辑（可追加视觉脚本，标记 `// [visual-effect]`）
- ❌ 不许改 api/ 或 store/
- ❌ 不许改后端代码
- ❌ 不许改本批次之外的页面
- ❌ 不许硬编码任何数值
- ✅ 可以使用 `utils/visual-effects.js` 和 `styles/animations.scss`

## 依赖：UI-INFRA ✅ + UI-REFRESH-1 ✅
## 优先级：P1

---
派发时间：2026-04-11
