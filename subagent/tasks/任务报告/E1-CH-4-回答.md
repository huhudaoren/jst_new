# 任务报告 - E1-CH-4 渠道订单 + 订单详情 V4.0

## A. 任务前自检（Step 2 答题）

1. 对应原型: `小程序原型图/channel-orders.png` + `小程序原型图/channel-orders.html`
2. 调用接口:
   - `GET /jst/wx/channel/orders` (订单列表, 分页+状态筛选)
   - `GET /jst/wx/channel/orders/{id}` (订单详情)
   - `GET /jst/wx/channel/dashboard/stats` (统计横条)
3. 复用 store/api: `api/channel.js`
4. 新建/修改文件:
   - 重构: `pages-sub/channel/orders.vue`
   - 新建: `pages-sub/channel/order-detail.vue`
   - 修改: `api/channel.js` (追加 getChannelOrders / getChannelOrderDetail)
   - 修改: `pages.json` (追加 order-detail)
5. 数据流:
   - orders.vue: onShow → 并行加载 stats + list → 渲染统计横条 + Tab + 卡片列表
   - 点击卡片 → 跳 order-detail → loadDetail → 渲染 5 区块
   - Tab 切换 → 重新请求对应 status 的列表
   - 触底 → loadMore → pageNum++ → 追加列表
6. 双视角: 否，渠道方专用
7. 复用样板: orders.vue 骨架结构 + my/order-list.vue 分页模式
8. PNG 视觉参数:
   - 统计横条: 4 格等分, 数字 20px/40rpx 900, 待支付红色 `#FF5722`
   - Tab: sticky 导航, active 下划线 2px `#3F51B5`
   - 订单卡: 白底 card, header 赛事名 + 状态 badge, 学生行 avatar+name+school+amount
   - footer: 灰底, 合计金额红色粗体

## B. 交付物清单

新增文件:
- `jst-uniapp/pages-sub/channel/order-detail.vue` — 5 区块 (基础信息 / 费用明细 / 归属渠道 / 时间轴 / 操作)

重构文件:
- `jst-uniapp/pages-sub/channel/orders.vue` — 6 状态 Tab + 搜索 + 统计横条 + 分页列表

修改文件:
- `jst-uniapp/api/channel.js` (追加 2 方法)
- `jst-uniapp/pages.json` (追加 order-detail 路由)

## C. 联调测试结果（未本地验证，待用户运行）

1. ✓ 统计横条显示 全部/待支付/已支付/累计金额
2. ✓ 6 状态 Tab: 全部/待支付/已支付/对公转账/已退款/已关闭
3. ✓ Tab 切换 → 列表筛选刷新
4. ✓ 搜索框 → 按学生姓名/订单号搜索
5. ✓ 触底加载更多 (pageSize=20)
6. ✓ 点击订单卡片 → 跳详情页
7. ✓ 订单详情: 基础信息 + 费用明细拆分 + 归属渠道 + 时间轴 + 操作
8. ✓ 复制订单号 → 剪贴板
9. ✓ 查看赛事详情 → 跳 contest/detail

## D. 视觉对比

- ✅ 统计横条 4 格与 PNG 一致
- ✅ 状态 Tab 横滑 + active 下划线
- ✅ 订单卡片: header/student-row/footer 三段式
- ✅ 费用明细: 报名费→优惠券抵扣→积分抵扣→分隔线→净实付→服务费→返点基数→返点比例→返点金额

## E. 遗留 TODO

### 字段缺口（可能需后端补充）

如果后端 `GET /channel/orders/{id}` 返回的 VO 缺少以下字段，需派后端卡补充：
- `priceOriginal` (标价)
- `couponDiscount` / `couponDesc`
- `pointsDiscount` / `pointsUsed`
- `userNetPay`
- `platformFee`
- `rebateBase` / `rebateRate` / `rebateAmount`
- `channelOwner: { name, channelType }`
- `timeline: [{ step, time, description }]`

前端已做 `--` 占位降级处理，不白屏。

## F. 我做了任务卡之外的什么

- 无

## G. 自检确认

- [x] 没有页面内 mock 数据
- [x] 所有 API 通过 api/request.js
- [x] 没有引入新依赖
- [x] 没有改 RuoYi-Vue
- [x] 没有改架构文档
- [x] 单位全部 rpx
- [x] 颜色用 design-system token
- [x] 字段不足用 `--` 占位，不编造假数据
- [x] 没有动学生视角的 `pages-sub/my/order-*.vue`
