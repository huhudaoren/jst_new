# 任务卡 E1-CH-6 - 渠道数据统计（轻量看板）

## 阶段 / 端
阶段 E 批 1 / **jst-uniapp**

## 背景
`pages-sub/channel/data.vue` 当前是骨架。原型 `channel-data.html` 定位：**轻量运营看板**，不承担深度经营分析（深度分析走 F-2 批次的 `analysis-home/detail`）。

## PRD 依据
- §7.2 "渠道数据页定位为轻量运营页"
- 原型：`channel-data.html`

## 交付物

### 1. 重构 `pages-sub/channel/data.vue`

#### A. 时间周期选择器
- 本月 / 上月 / 近 3 月 / 近 6 月 / 自定义
- 默认本月

#### B. 4 格 KPI
- 报名人数
- 支付金额
- 退款金额
- 返点金额
- 数据来源：`GET /channel/dashboard/stats?period=xxx`

#### C. 趋势图（简单版）
- 用 uCharts 或简单 canvas 自绘
- X 轴：日期
- Y 轴：报名人数
- 如无图表库不要引入新依赖，用占位 table 替代

#### D. 排行榜（简单版）
- Tab：[热门赛事] [活跃学生]
- 每项 Top 5
- 数据来源：`GET /channel/dashboard/top-contests` / `GET /channel/dashboard/top-students`
- **⚠️ 这两个接口若后端未提供**，写字段缺口反馈文档 `subagent/ui-feedback/2026-04-10-channel-data-字段需求.md`，不做占位

#### E. 底部引导
- "想看更深度分析？" → 跳 `analysis-home`（F-2 批次会做）
- 当前显示为灰标按钮 + "即将上线"

### 2. API 封装
**修改**：`api/channel.js`

```js
export function getChannelStats(params) { return request({ url: '/jst/wx/channel/dashboard/stats', method: 'GET', params }) }
export function getTopContests(params) { return request({ url: '/jst/wx/channel/dashboard/top-contests', method: 'GET', params }) }
export function getTopStudents(params) { return request({ url: '/jst/wx/channel/dashboard/top-students', method: 'GET', params }) }
```

## DoD
- [ ] 时间周期选择器可切换
- [ ] 4 KPI + 趋势图（或占位 table）+ 排行榜
- [ ] 深度分析入口灰标
- [ ] 字段缺口反馈文档（如有）
- [ ] 任务报告 `E1-CH-6-回答.md`

## 不许做
- ❌ 不许引入 npm 图表依赖
- ❌ 不许编造假数据填充图表
- ❌ 不许做深度分析页（F-2 批次）

## 依赖：E0-1 + E1-CH-2
## 优先级：P2

---
派发时间：2026-04-10
