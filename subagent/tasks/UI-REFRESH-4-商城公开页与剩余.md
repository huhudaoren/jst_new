# 任务卡 UI-REFRESH-4 - 商城、公开页与剩余页面刷新（批次 4）

## 阶段
阶段 F / **jst-uniapp**（UI Refresh Agent）

## 背景
最后一批，覆盖积分商城、公开页面、权益中心、公告详情等，共 23 页。完成后小程序 74 页全部刷新。

## 系统提示词
使用 `subagent/UI_REFRESH_AGENT_PROMPT.md`

## 必读
同前，额外参考批次 1/2/3 报告。

## 刷新页面清单（23 页）

### A. 积分商城（4 页）
| # | 路径 | 说明 |
|---|------|------|
| 1 | `pages-sub/mall/list.vue` | 商城列表 |
| 2 | `pages-sub/mall/detail.vue` | 商品详情 |
| 3 | `pages-sub/mall/exchange-list.vue` | 兑换记录 |
| 4 | `pages-sub/mall/exchange-detail.vue` | 兑换详情 |

### B. 积分中心（2 页）
| # | 路径 | 说明 |
|---|------|------|
| 5 | `pages-sub/points/center.vue` | 积分中心 |
| 6 | `pages-sub/points/detail.vue` | 积分明细 |

### C. 权益中心（3 页）
| # | 路径 | 说明 |
|---|------|------|
| 7 | `pages-sub/rights/center.vue` | 权益中心 |
| 8 | `pages-sub/rights/detail.vue` | 权益详情 |
| 9 | `pages-sub/rights/writeoff-apply.vue` | 权益核销申请 |

### D. 公开页面（5 页）
| # | 路径 | 说明 |
|---|------|------|
| 10 | `pages-sub/public/partner-apply.vue` | 赛事方入驻申请 |
| 11 | `pages-sub/public/apply-status.vue` | 申请状态 |
| 12 | `pages-sub/public/score-query.vue` | 成绩查询（公开） |
| 13 | `pages-sub/public/cert-verify.vue` | 证书验证（公开） |
| 14 | `pages-sub/public/help.vue` | 帮助中心 |

### E. 公告与消息（2 页）
| # | 路径 | 说明 |
|---|------|------|
| 15 | `pages-sub/notice/detail.vue` | 公告详情 |
| 16 | `pages-sub/notice/message.vue` | 系统消息 |

### F. 课程与营销（2 页）
| # | 路径 | 说明 |
|---|------|------|
| 17 | `pages-sub/course/detail.vue` | 课程详情 |
| 18 | `pages-sub/marketing/campaign.vue` | 营销活动 |

### G. 补充页面（5 页）
| # | 路径 | 说明 |
|---|------|------|
| 19~23 | 如有遗漏页面 | 全量扫描 pages/ + pages-sub/ 确保零遗漏 |

> Agent 开工时请 `glob "jst-uniapp/pages*/**/*.vue"` 确认完整列表，与批次 1/2/3 去重后全部纳入。

## 刷新规则
同前。额外注意：
- 商城页面需要突出商品图片和价格
- 公开页面可能被未登录用户访问，视觉上需要传达平台可信度
- 帮助中心以文本为主，排版和行距是重点

## 收尾要求
本批次完成后，请额外做一次**全量一致性扫描**：
1. `grep -rn "硬编码色值模式"` 确认零遗漏
2. 确认所有页面都有骨架屏（列表页）或加载态（详情页）
3. 确认状态标签颜色全局统一（§4.8 映射表）

## DoD
- [ ] 剩余全部页面刷新完成
- [ ] 全量一致性扫描通过
- [ ] 零硬编码数值
- [ ] 编译无报错
- [ ] 业务功能无回归
- [ ] 任务报告 `UI-REFRESH-4-回答.md`

## 不许做
同 UI-REFRESH-2

## 依赖：UI-REFRESH-1 ✅（视觉标杆）
## 优先级：P2

---
派发时间：2026-04-11
