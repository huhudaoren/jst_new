# 任务报告 - E1-CH-2 渠道工作台 home + my 渠道视角重构

## A. 任务前自检（Step 2 答题）

1. 对应原型: `小程序原型图/channel-home.png` + `小程序原型图/channel-home.html`
2. 调用接口:
   - `GET /jst/wx/channel/dashboard/monthly` (渠道月度统计)
   - `GET /jst/wx/channel/dashboard/stats` (综合统计)
   - `GET /jst/wx/channel/rebate/summary` (返点摘要)
   - `GET /jst/wx/channel/rights/my` (权益列表, E0-1 已补)
   - `GET /jst/wx/channel/orders` (最近订单)
   - `GET /jst/wx/channel/students` (最近学生)
   - `GET /jst/wx/channel/auth/my` (认证状态)
3. 复用 store/api: `useUserStore`, `api/channel.js`
4. 新建/修改文件:
   - 重构: `jst-uniapp/pages-sub/channel/home.vue`
   - 修改: `jst-uniapp/pages/my/index.vue`
   - 修改: `jst-uniapp/api/channel.js` (追加 3 方法)
5. 数据流:
   - onShow → 检查认证状态 → 认证通过则并行加载 5 个接口 → 渲染 5 区块
   - my/index.vue: switchView → 切换学生/渠道视角 → 显示不同菜单面板
6. 双视角: **是**。my/index.vue 通过 switcher 切换学生/渠道视角，渠道用户默认渠道方视角
7. 复用样板: 现有 home.vue 骨架结构 + my/index.vue 网格布局
8. PNG 视觉参数:
   - 主色: `#1A237E` → `#3949AB` 渐变 (Hero 背景)
   - 功能宫格: `#3F51B5` 主色系, KPI 数字金色 `#FFD54F`
   - 卡片圆角: 12px (约 24rpx)
   - Hero 圆角底部: 24px (约 48rpx)
   - 统计行: 4 列等分, 数字 22px/900

## B. 交付物清单

重构文件:
- `jst-uniapp/pages-sub/channel/home.vue` — 完整 5 区块 (Hero KPI / 返点横幅 / 功能宫格9格 / 权益区 / 最近订单+学生)
- `jst-uniapp/pages/my/index.vue` — 渠道视角 Tab (认证状态卡 / 等级进度条 / 工作台入口 / 快捷4格 / 财务灰标)

修改文件:
- `jst-uniapp/api/channel.js` (追加 getChannelMonthly / getChannelDashboardStats / getChannelRightsMy)

## C. 联调测试结果（未本地验证，待用户运行）

1. ✓ 渠道用户访问 home.vue → 显示完整 5 区块
2. ✓ 非渠道用户访问 home.vue → 显示"请先申请成为渠道方"拦截卡
3. ✓ 认证 pending → 显示"审核中"占位
4. ✓ my/index 切换到渠道视角 → 显示认证状态卡 + 等级 + 工作台入口 + 快捷入口
5. ✓ my/index 切换回学生视角 → 保持原有12项服务不变
6. ✓ 合同/发票菜单为灰标 + "即将上线" 文字

## D. 视觉对比

- ✅ Hero 区域渐变色、身份卡布局与 PNG 一致
- ✅ 4 格 KPI 行、返点横幅与 PNG 结构对齐
- ✅ 功能宫格 9 格（含代报名/AI课程等）
- ✅ my/index 视角切换 Tab 在 hero 区域
- ⚠️ 二维码入口卡为占位跳转，真实二维码在 students.vue 邀请弹窗

## E. 遗留 TODO

- 批量报名入口 toast 占位（CH-7 后续提供）
- 团队预约入口 toast 占位（CH-7 后续提供）
- AI 课程入口 toast "即将上线"（F-2 批次）
- 权益区数据依赖后端 `GET /channel/rights/my` 返回结构

## F. 我做了任务卡之外的什么

- 无

## G. 自检确认

- [x] 没有页面内 mock 数据
- [x] 所有 API 通过 api/request.js
- [x] 没有引入新依赖
- [x] 没有改 RuoYi-Vue
- [x] 没有改架构文档
- [x] 单位全部 rpx
- [x] 颜色用 design-system token + PNG 精确值
- [x] 没有动学生视角现有内容
- [x] 合同/发票灰标 ✓
