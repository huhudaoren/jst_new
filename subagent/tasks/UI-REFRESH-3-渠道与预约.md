# 任务卡 UI-REFRESH-3 - 渠道端与预约页面刷新（批次 3）

## 阶段
阶段 F / **jst-uniapp**（UI Refresh Agent）

## 背景
本卡覆盖渠道方（教师/机构）全部页面 + 预约核销，共 23 页。这些页面服务于 B 端用户（渠道方），视觉上需要兼顾专业感和操作效率。

## 系统提示词
使用 `subagent/UI_REFRESH_AGENT_PROMPT.md`

## 必读
同 UI-REFRESH-2，额外参考已完成的批次 1/2 报告了解视觉标杆。

## 刷新页面清单（23 页）

### A. 渠道认证（3 页）
| # | 路径 | 说明 |
|---|------|------|
| 1 | `pages-sub/channel/apply-entry.vue` | 认证入口 |
| 2 | `pages-sub/channel/apply-form.vue` | 认证申请表单 |
| 3 | `pages-sub/channel/apply-status.vue` | 认证状态 |

### B. 渠道工作台（5 页）
| # | 路径 | 说明 |
|---|------|------|
| 4 | `pages-sub/channel/home.vue` | 渠道首页/工作台 |
| 5 | `pages-sub/channel/data.vue` | 数据统计 |
| 6 | `pages-sub/channel/students.vue` | 学生管理 |
| 7 | `pages-sub/channel/student-score.vue` | 学生成绩 |
| 8 | `pages-sub/channel/student-cert.vue` | 学生证书 |

### C. 渠道订单与返点（6 页）
| # | 路径 | 说明 |
|---|------|------|
| 9 | `pages-sub/channel/orders.vue` | 渠道订单列表 |
| 10 | `pages-sub/channel/order-detail.vue` | 渠道订单详情 |
| 11 | `pages-sub/channel/rebate.vue` | 返点中心 |
| 12 | `pages-sub/channel/settlement.vue` | 结算页 |
| 13 | `pages-sub/channel/withdraw-list.vue` | 提现列表 |
| 14 | `pages-sub/channel/withdraw-detail.vue` | 提现详情 |

### D. 渠道业务操作（3 页）
| # | 路径 | 说明 |
|---|------|------|
| 15 | `pages-sub/channel/withdraw-apply.vue` | 提现申请 |
| 16 | `pages-sub/channel/batch-enroll.vue` | 批量报名 |
| 17 | `pages-sub/channel/participants.vue` | 临时档案管理 |

### E. 渠道预约（1 页）
| # | 路径 | 说明 |
|---|------|------|
| 18 | `pages-sub/channel/appointment.vue` | 渠道团队预约 |

### F. 预约与核销（5 页）
| # | 路径 | 说明 |
|---|------|------|
| 19 | `pages-sub/appointment/apply.vue` | 个人预约申请 |
| 20 | `pages-sub/appointment/detail.vue` | 预约详情 |
| 21 | `pages-sub/appointment/my-list.vue` | 我的预约列表 |
| 22 | `pages-sub/appointment/scan.vue` | 扫码核销 |
| 23 | `pages-sub/appointment/writeoff-record.vue` | 核销记录 |

## 刷新规则
同 UI-REFRESH-2。额外注意：
- 渠道工作台（home/data）是 B 端核心入口，数据密度较高，需要在"呼吸感"和"信息密度"之间找平衡
- 提现/结算涉及金额展示，数字需要突出（大字号 + 粗字重 + 品牌色）
- 扫码核销页需要摄像头区域的视觉引导

## DoD
- [ ] 23 个页面全部刷新
- [ ] 零硬编码数值
- [ ] 编译无报错
- [ ] 业务功能无回归
- [ ] 任务报告 `UI-REFRESH-3-回答.md`

## 不许做
同 UI-REFRESH-2

## 依赖：UI-REFRESH-1 ✅（视觉标杆）
## 优先级：P1

---
派发时间：2026-04-11
