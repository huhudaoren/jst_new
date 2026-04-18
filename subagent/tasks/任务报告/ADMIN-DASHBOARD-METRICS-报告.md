# ADMIN-DASHBOARD-METRICS 任务报告

> 交付日期：2026-04-18
> 任务卡：`subagent/tasks/ADMIN-DASHBOARD-METRICS.md`
> 承接角色：Web Admin Agent
> 本次范围：admin 销售看板 3 个核心指标 + 用户确认追加的“渠道地区字段链路”

---

## 一、执行摘要

本卡已完成 admin 销售看板 3 个核心指标落地：

1. 提成成本趋势
2. J 上限压缩触发率
3. 渠道业绩热力图

同时，执行过程中先按任务卡要求对 `jst_sales_commission_ledger`、`jst_channel`、`jst_order_main` 做了真实 `DESCRIBE`，确认任务卡中的若干字段名与本地库实际不一致，随后按“以 DESCRIBE 为准”的原则调整实现。

本卡还有一个明确的范围外扩，是由用户在确认阶段亲自追加的：

> `confirm 2，渠道申请的时候需要必填一个地区，目前没有 需要留出该字段`

因此，除 3 个 dashboard 指标外，本次还补齐了：

- `jst_channel_auth_apply.region`
- `jst_channel.region`
- 渠道申请 DTO / Mapper / Service / 小程序申请表单的 `region` 采集与落库
- admin 渠道认证页的 `region` 展示

结论：

- 后端全量 `mvn clean compile -DskipTests` 已通过
- 前端 `npm run build:prod` 已通过
- 数据库迁移已在本地执行并确认 `region` 字段生效
- 3 个指标的核心 SQL 已通过“事务内临时样本 + 回滚”的方式完成自检

---

## 二、任务卡与真实环境的偏差结论

本卡是典型的“任务卡描述与真实库不完全一致”的场景，必须先写清楚，否则后续维护者会误判。

### 2.1 `jst_sales_commission_ledger` 真实字段与任务卡差异

任务卡写法中提到了：

- `commission_amount`
- `compression_amount`
- `original_amount`
- `accrue_time`

但本地实际 `DESCRIBE jst_sales_commission_ledger` 结果中，相关字段为：

| 实际字段 | 含义 |
|---|---|
| `base_amount` | 提成基数 |
| `applied_rate` | 实际费率 |
| `raw_amount` | 未压缩前提成金额 |
| `compress_ratio` | 压缩比例 |
| `amount` | 实际入账提成金额 |
| `accrue_at` / `accrued_at` | 计提时间 |
| `status` | 台账状态 |

结论：

- 实表没有 `compression_amount`
- 实表没有 `original_amount`
- 实表没有 `commission_amount`
- 本卡必须按任务卡 §七“降级方案 / 同义字段方案”处理，不能改表

本次最终口径：

- `commissionTotal = SUM(amount)`
- `originalTotal = SUM(raw_amount)`
- `compressedAmount = SUM(raw_amount - amount)`
- 触发条件：`raw_amount > amount` 或 `compress_ratio < 1`

### 2.2 `jst_channel` 真实字段与任务卡差异

任务卡默认热力图按地区维度来自：

- `province`
- `city`
- `region`
- `area`

但本地实际 `DESCRIBE jst_channel` 初始并没有这些地区字段，只有：

| 实际字段 | 说明 |
|---|---|
| `channel_id` | 渠道 ID |
| `user_id` | 关联用户 |
| `channel_type` | 渠道类型 |
| `channel_name` | 渠道名称 |
| 其他认证/结算相关字段 | 与热力图无直接关系 |

结论：

- 原始库里 `jst_channel` 没有地区字段，无法直接做“地区 × 业务类型”热力图
- 用户确认后，显式要求“渠道申请必填地区，需要留出该字段”
- 因此本次新增 `jst_channel.region`，并在热力图里优先使用该字段

### 2.3 `jst_order_main` 真实字段与任务卡差异

本地 `DESCRIBE jst_order_main` 结果确认存在：

| 实际字段 | 用途 |
|---|---|
| `channel_id` | 订单归属渠道 |
| `business_type` | 业务类型 |
| `net_pay_amount` | 实付金额 |
| `pay_time` | 支付时间 |
| `order_status` | 订单状态 |

结论：

- 业务类型维度可直接从 `jst_order_main.business_type` 获取
- 不需要降级成单维度热力图

### 2.4 额外发现的环境偏差

#### 偏差 1：前端 API 文件路径与任务卡不一致

任务卡写的是：

- `RuoYi-Vue/ruoyi-ui/src/api/jst/salesDashboard.js`

实际项目已有文件是：

- `RuoYi-Vue/ruoyi-ui/src/api/admin/sales/dashboard.js`

本次按项目真实路径追加 API，不新建第二套文件。

#### 偏差 2：业务类型字典名与任务卡不一致

任务卡写的是：

- `jst_business_type`

本地实际存在并被页面使用的是：

- `jst_sales_business_type`

字典值实测为：

- `enroll`
- `course`
- `mall`
- `appointment_team`
- `appointment_personal`

本次前端热力图和筛选器按真实字典 `jst_sales_business_type` 接入。

---

## 三、用户确认后的范围外扩说明

原任务卡只要求做 admin dashboard 3 指标，明确红线之一是：

- 不改 `jst_sales_commission_ledger` 表结构

这一点本次严格遵守，ledger 没有任何 schema 变更。

但用户在确认阶段追加了新的业务决策：

> 渠道申请时地区必填，目前没有，需要留出该字段

因此本次新增了一个小范围 schema 扩展，只作用于渠道申请 / 渠道档案：

- `jst_channel_auth_apply.region`
- `jst_channel.region`

这不是擅自改表，而是用户明确确认后的范围扩展。

为便于复用与审计，本次单独补了迁移脚本：

- `架构设计/ddl/99-migration-channel-region.sql`

并已在本地库执行，执行后复核确认：

- `jst_channel.region` 已存在
- `jst_channel_auth_apply.region` 已存在

---

## 四、实际交付内容

## 4.1 后端接口交付

在原有 `AdminSalesDashboardController` 上追加 3 个接口，没有新建 Controller：

| 接口 | 说明 |
|---|---|
| `GET /admin/sales/dashboard/commission-trend` | 提成成本趋势 |
| `GET /admin/sales/dashboard/compression-stats` | J 上限压缩统计 |
| `GET /admin/sales/dashboard/channel-heatmap` | 渠道业绩热力图 |

对应实现位于：

- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/controller/admin/AdminSalesDashboardController.java`
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/AdminSalesDashboardService.java`
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/AdminSalesDashboardServiceImpl.java`
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/mapper/JstSalesCommissionLedgerMapper.java`
- `RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/JstSalesCommissionLedgerMapper.xml`

实现风格保持与现有模块一致：

- Controller 只收参和出参
- 日期解析、除零保护、bucket 校验在 ServiceImpl
- 聚合逻辑放 Mapper XML
- 没有在 Java 中做大批量 client-side groupBy

## 4.2 指标 1：提成成本趋势

### 口径

- 计提总额：`SUM(l.amount)`
- 收入总额：`SUM(o.net_pay_amount)`
- 成本率：`commissionTotal / orderNetTotal`
- bucket 支持：`day / week / month`

### 时间口径

- 提成按 `COALESCE(l.accrued_at, l.accrue_at)` 分桶
- 订单按 `o.pay_time` 分桶

### 说明

本指标本质是“看板经营指标”，不是严格会计配比。

原因：

- 销售提成计提时间与订单支付时间天然可能错位
- 同一个日桶里可能只出现支付、不出现计提
- 或只出现计提、不出现支付

因此图表呈现的是运营趋势，而不是财务结转意义上的逐日一一对应关系。

## 4.3 指标 2：J 上限压缩触发率

### 口径

- `totalCount = COUNT(*)`
- `triggeredCount = SUM(CASE WHEN raw_amount > amount OR compress_ratio < 1 THEN 1 ELSE 0 END)`
- `compressedAmount = SUM(CASE WHEN raw_amount > amount THEN raw_amount - amount ELSE 0 END)`
- `originalTotal = SUM(raw_amount)`
- `triggerRate = triggeredCount / totalCount`
- `compressedRate = compressedAmount / originalTotal`

### 说明

这是按真实字段完成的“同义字段实现”，没有新增任何 ledger 字段。

## 4.4 指标 3：渠道业绩热力图

### 最终口径

- 行维度：地区
- 列维度：`business_type`
- 格子值：`SUM(net_pay_amount)`
- 附带字段：`COUNT(DISTINCT channel_id)` 作为 tooltip / TOP 表补充

### 地区取值优先级

1. `jst_channel.region`
2. `jst_user_address.province`
3. `'未设置'`

说明：

- 用户要求“需要留出地区字段”，所以最终正确主口径是 `jst_channel.region`
- 为兼容旧数据，本次保留地址省份 fallback
- 旧数据全部回填后，可考虑移除 fallback，统一口径

## 4.5 前端 admin 看板交付

修改文件：

- `RuoYi-Vue/ruoyi-ui/src/api/admin/sales/dashboard.js`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/sales/dashboard.vue`

新增 UI 区块顺序：

1. 提成成本趋势
2. J 上限压缩触发率
3. 渠道业绩热力图

前端实现点：

- 继续使用项目已有 `import * as echarts from 'echarts'`
- 没有引入 `vue-echarts` 或其他新包
- 趋势图支持 `day/week/month` 切换
- 压缩指标区做了 2x2 卡片与预警色
- 热力图支持 tooltip 和 TOP10 表格
- 所有图表都补了 `resize` / `dispose`
- 无数据场景统一走空态，不留空白画布

## 4.6 地区字段链路补齐

### 数据库

- `jst_channel_auth_apply.region`
- `jst_channel.region`

### 后端链路

- 渠道申请 DTO 增加 `region`
- 渠道申请 domain / VO 增加 `region`
- 申请入库时写入 `region`
- 审核通过生成 `jst_channel` 时回写 `region`
- 渠道 Mapper 读写 `region`

### 前端 / 小程序

- 小程序渠道申请表单新增“所在地区 *”
- 表单提交时强制带 `region`
- 驳回重提场景会回填 `region`
- admin 渠道认证页补展示 `region`

## 4.7 顺手修复的隐性问题

在补地区链路时发现一个非本卡核心但非常值得修的 bug：

- 旧的 `resubmit` 流程会修改内存对象，但没有真正把新值持久化回 `jst_channel_auth_apply`

本次补了：

- `ChannelAuthApplyMapperExt.resubmitApply(...)`

让驳回重提时的：

- `channelType`
- `applyName`
- `region`
- `materialsJson`

能够真正落库。

这属于顺手修复，不改主业务语义，但能避免重提申请时表单改了却没保存的问题。

---

## 五、实际修改文件清单

## 5.1 dashboard 直接相关

| 文件 | 作用 |
|---|---|
| `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/controller/admin/AdminSalesDashboardController.java` | 追加 3 个接口 |
| `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/AdminSalesDashboardService.java` | 追加 3 个服务方法 |
| `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/AdminSalesDashboardServiceImpl.java` | 日期解析、bucket 校验、结果组装 |
| `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/mapper/JstSalesCommissionLedgerMapper.java` | Mapper 方法声明 |
| `RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/JstSalesCommissionLedgerMapper.xml` | 3 组聚合 SQL |
| `RuoYi-Vue/ruoyi-ui/src/api/admin/sales/dashboard.js` | 新增 3 个前端 API |
| `RuoYi-Vue/ruoyi-ui/src/views/jst/sales/dashboard.vue` | 新增 3 个看板区块 |

## 5.2 地区字段链路相关

| 文件 | 作用 |
|---|---|
| `架构设计/ddl/99-migration-channel-region.sql` | 迁移脚本 |
| `RuoYi-Vue/jst-organizer/src/main/java/com/ruoyi/jst/organizer/dto/ChannelAuthApplyReqDTO.java` | 申请 DTO 新增 `region` |
| `RuoYi-Vue/jst-organizer/src/main/java/com/ruoyi/jst/organizer/dto/ChannelAuthResubmitReqDTO.java` | 重提 DTO 新增 `region` |
| `RuoYi-Vue/jst-organizer/src/main/java/com/ruoyi/jst/organizer/domain/JstChannelAuthApply.java` | domain 新增 `region` |
| `RuoYi-Vue/jst-organizer/src/main/java/com/ruoyi/jst/organizer/vo/ChannelAuthApplyVO.java` | 返回 VO 新增 `region` |
| `RuoYi-Vue/jst-organizer/src/main/java/com/ruoyi/jst/organizer/mapper/ChannelAuthApplyMapperExt.java` | 新增重提更新方法 |
| `RuoYi-Vue/jst-organizer/src/main/resources/mapper/organizer/ChannelAuthApplyMapperExt.xml` | 申请 / 查询 / 重提 SQL 补 `region` |
| `RuoYi-Vue/jst-organizer/src/main/java/com/ruoyi/jst/organizer/service/impl/ChannelAuthApplyServiceImpl.java` | submit / approve / resubmit 链路补 `region` |
| `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/domain/JstChannel.java` | 渠道 domain 新增 `region` |
| `RuoYi-Vue/jst-user/src/main/resources/mapper/user/JstChannelMapper.xml` | 渠道读写补 `region` |
| `RuoYi-Vue/ruoyi-ui/src/views/jst/channel-auth/index.vue` | admin 认证页展示地区 |
| `jst-uniapp/pages-sub/channel/apply-form.vue` | 小程序申请表单新增地区必填 |

---

## 六、验证过程与结果

## 6.1 DESCRIBE 核验

本卡开工前已执行以下核验：

- `DESCRIBE jst_sales_commission_ledger`
- `DESCRIBE jst_channel`
- `DESCRIBE jst_order_main`

后续又执行：

- `DESCRIBE jst_channel_auth_apply`

用于确认地区迁移字段生效。

核验结果：

- `ledger` 字段与任务卡有明显偏差，已按实表口径实现
- `order_main` 有 `business_type`
- `channel` 初始无地区字段，迁移后已存在 `region`
- `channel_auth_apply` 迁移后已存在 `region`

## 6.2 后端编译验证

先做过局部编译，后又补跑了整仓编译：

```bash
mvn clean compile -DskipTests
```

结果：

- 18 个模块全部 `SUCCESS`
- 包含 `jst-channel`、`jst-organizer`、`jst-user`、`ruoyi-admin`

说明：

- 本卡不只是 `jst-channel` 编译通过
- 用户追加的 `region` 链路相关模块也一起过编译了

## 6.3 前端生产构建验证

执行：

```bash
npm run build:prod
```

结果：

- 构建成功
- 只有原有体积 warning
- 没有新增语法错误或打包失败

## 6.4 SQL 自检验证

由于本地库原始状态中：

- `paid order = 0`
- `jst_sales_commission_ledger = 0`

无法直接拿现有业务数据验证图表，因此本次采用了：

- 事务内插入临时样本
- 执行目标 SQL
- 核对结果
- 最后 `ROLLBACK`

也就是说：

- 验证过真实 SQL 行为
- 但没有把假数据污染进本地库

### 样本验证结果摘要

#### 1. 提成成本趋势（日）

得到的典型结果：

| bucket | commissionTotal | orderNetTotal |
|---|---:|---:|
| `2026-04-03` | `0.00` | `1000.00` |
| `2026-04-10` | `100.00` | `0.00` |
| `2026-04-14` | `100.00` | `0.00` |
| `2026-04-20` | `120.00` | `0.00` |

说明：

- 订单支付日与提成计提日错位时，趋势图是允许出现“单侧有值”的

#### 2. 压缩统计

得到的典型结果：

| totalCount | triggeredCount | compressedAmount | originalTotal |
|---:|---:|---:|---:|
| `4` | `2` | `80.00` | `550.00` |

换算结果：

- `triggerRate = 50%`
- `compressedRate ≈ 14.55%`

#### 3. 热力图

得到的典型结果：

| region | businessType | channelCount | gmv |
|---|---|---:|---:|
| `上海市` | `course` | `1` | `3000.00` |
| `北京市` | `course` | `1` | `2000.00` |
| `上海市` | `enroll` | `1` | `1500.00` |
| `北京市` | `enroll` | `1` | `1000.00` |

#### 4. 额外筛选验证

本次还额外验证了：

- `bucket = week`
- `region = 北京`
- `businessType = enroll`
- `region = 上海 + businessType = course`

结果均符合预期，说明筛选条件已真正进入 SQL，而不是前端假筛。

## 6.5 本次未完成的验证

以下项本次没有做到“完整闭环”：

1. 未启动 admin 服务并走登录态真实 HTTP 调用
2. 未录制 UI 操作视频
3. 未补 3 张截图到报告
4. 未做自动化 UI 回归

这 4 项不是说代码没做，而是“交付证据”还不够完整，需在后续收口。

---

## 七、DoD 对照

| DoD | 结果 | 说明 |
|---|---|---|
| 先 DESCRIBE 三表 | 已完成 | 并写入本报告 |
| 后端 3 个端点追加到既有 Controller | 已完成 | 未新建 Controller |
| SQL 与 DESCRIBE 一致 | 已完成 | 按真实字段改写 |
| `mvn` 通过 | 已完成 | 全量 reactor `SUCCESS` |
| `npm run build:prod` 通过 | 已完成 | 仅体积 warning |
| 无数据场景优雅展示 | 已完成 | 前端补空态 |
| Happy / 无数据 / 跨月手测 | 部分完成 | 完成 SQL 样本与筛选验证，未完成登录态 HTTP |
| 报告列出 3 个端点路径 + 3 个 SQL + 3 张截图 | 部分完成 | 已列端点与 SQL 口径，截图未补 |
| 不改 ledger 表结构 | 已完成 | 严格遵守 |

结论：

- “代码交付 + 编译验证 + SQL 级验证”已完成
- “HTTP 真接口验证 + 截图证据”仍有补齐空间

---

## 八、已知隐患点

## 8.1 热力图地区值当前仍是自由文本，容易碎片化

目前 `region` 是 `VARCHAR(64)` 自由输入，理论上可能出现：

- `北京`
- `北京市`
- `北京-朝阳`
- `Beijing`

这种输入会直接把热力图拆成多行，造成数据割裂。

当前缓解措施：

- 先保证字段存在、链路可跑

但后续必须做：

- 地区标准化
- 或受控省份枚举

## 8.2 旧渠道 / 旧申请数据大概率仍无地区

迁移只是“留出字段”，不会自动回填历史数据。

因此旧数据会出现三种情况：

1. `jst_channel.region` 已填
2. `jst_channel.region` 未填，但 `jst_user_address.province` 可兜底
3. 两边都没有，只能显示 `未设置`

这会导致：

- 热力图统计口径短期内不够稳定
- 管理层看到“未设置”聚合行

## 8.3 趋势图中的“提成日”与“收入日”并非同一天然配比

当前设计是：

- 收入用 `pay_time`
- 提成用 `accrued_at / accrue_at`

如果业务方误以为“同一天的成本率就是那天订单的真实提成率”，可能产生误解。

建议：

- 在产品说明或 tooltip 中明确：这是经营看板口径，不是财务结转口径

## 8.4 本次没有真实登录态 HTTP 回归

虽然：

- 编译过了
- SQL 过了
- 打包过了

但仍然没有做到：

- 启动服务
- 拿 token
- 真 GET `/admin/sales/dashboard/*`
- 真点页面截屏

所以理论上仍存在运行时风险，例如：

- 权限点问题
- 参数绑定问题
- 某个环境下 dict 未加载
- 页面 mounted 时序问题

## 8.5 本次没有补截图证据

任务卡提到：

- 报告里应有 3 张截图

本次未产出截图，因此对非研发读者来说，交付直观性不足。

## 8.6 `region` 在 DB 里仍允许为空

这是刻意的保守策略，优点是：

- 不阻塞旧数据
- 不会让迁移失败

缺点是：

- 数据层面尚未强约束
- 仍需要依赖接口校验和前端必填

如果未来有旁路写库脚本或后台导入，仍可能插入空地区。

## 8.7 fallback 到 `jst_user_address.province` 的语义并不完全等价于渠道经营地区

地址省份只能作为兼容兜底，不是严格的渠道经营归属地。

潜在偏差：

- 用户住址在 A 省，但渠道实际经营在 B 省
- 老渠道没填 `region` 时，热力图会按地址省份误归类

所以 fallback 只是过渡策略，不应长期依赖。

---

## 九、后续 TODO

## 9.1 必做 TODO

1. 补一次历史数据回填
   - 目标：尽量为旧 `jst_channel` 和 `jst_channel_auth_apply` 补齐 `region`
   - 优先来源：既有地址省份、管理员人工补录、渠道认证补资料

2. 统一地区枚举
   - 推荐做成省级字典
   - 前端改为下拉选择，避免自由输入碎片化

3. 启动真实 admin 服务做 HTTP smoke
   - 至少验证：
   - `/admin/sales/dashboard/commission-trend`
   - `/admin/sales/dashboard/compression-stats`
   - `/admin/sales/dashboard/channel-heatmap`

4. 补 3 张页面截图
   - 趋势图截图
   - 压缩卡片截图
   - 热力图截图

5. 在产品文案中明确趋势图口径
   - 避免把“计提日 / 支付日错位”误读成图表异常

## 9.2 建议 TODO

1. 为热力图增加“按 GMV / 按渠道数”切换
2. 为热力图增加“只看已设置地区 / 包含未设置”开关
3. 为趋势图增加 tooltip 文案解释“计提口径”
4. 为压缩卡片增加近 7 天 / 近 30 天快捷切换
5. 为 `region` 字段补 admin 端可编辑入口，便于运营修正历史脏数据
6. 历史数据回填完成后，考虑去掉地址省份 fallback，统一只认 `jst_channel.region`

---

## 十、偏离与说明

## 10.1 偏离 1：本卡最终不再是纯只读聚合

原任务卡的 dashboard 部分是纯只读聚合，没有 DDL。

但由于用户明确追加“地区必填、需要留字段”，本次最终交付包含了：

- 一个迁移脚本
- 一条申请链路字段扩展

这不是擅自扩 scope，而是执行用户确认后的新要求。

## 10.2 偏离 2：未按任务卡所写 API 文件路径实施

原因不是偏离需求，而是：

- 任务卡里的前端 API 路径是旧路径
- 项目真实已有路径是 `src/api/admin/sales/dashboard.js`

本次按真实项目结构改，避免重复文件和调用分裂。

## 10.3 偏离 3：字典使用 `jst_sales_business_type`

任务卡写 `jst_business_type`，但本地实际没有该字典。

因此本次按实际存在的：

- `jst_sales_business_type`

完成接入。

---

## 十一、当前工作区说明

当前仓库是共享脏工作区，除本卡外还有其他并行改动。

本次报告只覆盖：

- admin sales dashboard 3 指标
- 渠道地区字段链路

未对其他并行改动做归档、回滚或额外说明。

另外，本次没有单独创建 commit，也没有按任务卡示例提交：

```text
feat(admin): ADMIN-DASHBOARD-METRICS 3 指标（成本趋势/压缩/热力）
```

原因：

- 当前工作区并不干净
- 还有大量其他任务并行改动
- 为避免把不相关文件一起打包，本次先交付代码与报告，不擅自提交

---

## 十二、最终结论

从“代码交付”角度看，本卡已经完成主目标：

- 3 个 admin 指标已落地
- ledger 未改表，严格按降级方案实现
- 地区字段已按用户确认补入申请链路与渠道档案
- `mvn` 与 `npm run build:prod` 均已通过

从“交付闭环”角度看，还剩 2 项建议尽快补齐：

1. 真实登录态 HTTP / 页面截图验证
2. 历史 `region` 数据回填与地区枚举标准化

如果后续有人接手，本报告最重要的两条背景结论是：

1. `jst_sales_commission_ledger` 的字段名和任务卡写法不一致，必须继续以 `raw_amount / amount / compress_ratio` 为准
2. 热力图的“地区”目前是新补链路，短期内仍有历史空值、自由文本和地址兜底带来的口径波动
