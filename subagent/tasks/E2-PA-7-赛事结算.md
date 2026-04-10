# 任务卡 E2-PA-7 - 赛事结算中心

## 阶段 / 端
阶段 E 批 2 / **ruoyi-ui** + 后端

## 背景
赛事方查看自己的结算数据，确认结算单，等待平台打款。PRD §7.6 + §8.8 定义完整的赛事结算流。

## PRD 依据
- §7.6 赛事方结算
- §8.5 + §8.8 结算数据模型
- 原型：`event-settlement.html`

## 交付物

### Part A — 后端接口

**新建**：`jst-finance/.../controller/partner/PartnerSettlementController.java`

路由 `/jst/partner/settlement/*`：
- `GET /list` — 结算单列表（按状态筛选）
- `GET /{id}` — 结算单详情（完整拆分）
- `POST /{id}/confirm` — 赛事方确认结算单
- `POST /{id}/dispute` — 赛事方发起争议（走工单到平台）

### Part B — 前端页面 `ruoyi-ui/src/views/partner/settlement.vue`

#### 顶部 Hero
- 本月应结
- 累计已结
- 审核中
- 争议中

#### 表格
- 结算单编号
- 关联赛事
- 报名总数
- 订单标价总额
- 优惠券总额
- 积分抵扣总额
- **用户净实付**
- **平台承担优惠金额**
- 渠道返点
- **平台服务费**
- 退款金额
- **合同约定扣项**
- **最终结算金额**（突出）
- 状态
- 操作（查看详情 / 确认 / 争议）

#### 结算单详情抽屉
- 完整财务拆分表
- 关联订单列表（下钻）
- 打款凭证（已打款状态显示）
- 合同/发票文件（灰标 F-2）

### Part C — API

**新建**：`ruoyi-ui/src/api/partner/settlement.js`

## DoD
- [ ] 后端 Controller
- [ ] 前端结算页 + 详情抽屉
- [ ] 财务字段完整拆分
- [ ] mvn compile SUCCESS
- [ ] 任务报告 `E2-PA-7-回答.md`
- [ ] commit: `feat(jst-finance+web): E2-PA-7 赛事方结算中心`

## 不许做
- ❌ 不许让赛事方直接改结算金额
- ❌ 不许真实打通合同发票下载（F-2）

## 依赖：E2-PA-1~6 + PA-9
## 优先级：P1

---
派发时间：2026-04-10
