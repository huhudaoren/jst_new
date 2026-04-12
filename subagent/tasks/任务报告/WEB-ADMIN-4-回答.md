# 任务报告 WEB-ADMIN-4 — 渠道管理与数据看板

## 状态：✅ 完成

## 交付物清单

### API 层（5 文件）
| 文件 | 说明 |
|---|---|
| `src/api/admin/channel-auth.js` | 渠道认证审核 API（list/detail/approve/reject/suspend） |
| `src/api/admin/channel.js` | 渠道列表 API（list/detail/update） |
| `src/api/admin/binding.js` | 绑定管理 API（list/force-unbind） |
| `src/api/admin/appointment.js` | 预约管理 API（list/detail/delete） |
| `src/api/admin/dashboard.js` | 看板聚合 API（复用已有 list 接口取 total + Top 5） |

### 页面层（5 文件）
| 文件 | 说明 |
|---|---|
| `views/jst/channel-auth/index.vue` | 渠道认证审核列表（搜索+表格+审核通过/驳回弹窗/暂停+材料抽屉） |
| `views/jst/channel/index.vue` | 已认证渠道列表（搜索+表格+详情抽屉+停用/启用+导出） |
| `views/jst/binding/index.vue` | 学生-渠道绑定列表（搜索+表格+强制解绑弹窗含警告提示） |
| `views/jst/appointment/index.vue` | 预约记录列表（搜索+表格+详情抽屉+取消操作+导出） |
| `views/jst/dashboard/index.vue` | 平台数据看板（4 KPI 卡 + 3 待办入口 + 双排行榜纯 CSS 进度条） |

### SQL
| 文件 | 说明 |
|---|---|
| `架构设计/ddl/99-migration-admin-menus.sql` | 菜单注册（9900-9999 段） + admin 角色绑定，INSERT IGNORE 幂等 |

## DoD 校验

| 项 | 状态 |
|---|---|
| 5 个管理模块 + 1 个数据看板页面完成 | ✅ |
| 菜单注册 SQL 幂等 | ✅ INSERT IGNORE |
| 审核操作有二次确认 | ✅ 所有审核/驳回/暂停/解绑/取消均有 $confirm |
| `npm run build:prod` SUCCESS | ✅ Build complete |
| 响应式手机适配 | ✅ 所有页面：搜索垂直堆叠/表格横向滚动/抽屉全屏/KPI 卡 xs:12 |

## 设计决策

1. **重写而非增强自动生成页**：原 `views/jst/user/jst_channel/` 等自动生成页质量太低（label 带 DDL 注释、暴露内部 ID、无响应式、权限 prefix 错误），选择新建独立管理页面
2. **看板纯 CSS 实现**：虽然 echarts 已安装，但看板数据量简单（4 KPI + 3 待办 + 2×Top 5），用纯 CSS 进度条 + 数字更轻量，避免引入图表库的渲染开销
3. **API 复用策略**：dashboard 聚合数据通过已有 list 接口 + `pageSize=1` 取 total 实现，无需新增后端端点
4. **menu_id 段**：9900-9999（紧接赛事方的 9700-9799 段，留出间隔避免冲突）
5. **channel/index.vue 覆盖**：原 `views/jst/channel/` 下只有 rebate 相关子目录，新 `index.vue` 是渠道列表主页，不会影响已有页面

## 后端 API 依赖

| 接口 | 对应 Controller | 权限 |
|---|---|---|
| `/jst/organizer/channel/apply/*` | ChannelAuthApplyAdminController | `jst:organizer:channelApply:*` |
| `/jst/user/jst_channel/*` | JstChannelController | `jst:user:channel:*` |
| `/jst/user/binding/*` | BindingAdminController | `jst:user:binding:*` |
| `/jst/order/jst_appointment_record/*` | JstAppointmentRecordController | `jst:order:appointment_record:*` |
| `/jst/channel/withdraw/list` | ChannelWithdrawAdminController | `jst:channel:withdraw:list` |

## 未覆盖项（不在本卡范围）

- 赛事管理、订单管理、内容管理等管理模块的菜单注册（已有代码生成器产出）
- 管理端首页替换为看板（需后端路由配置支持，当前看板挂在独立菜单下）
- echarts 图表增强（如需趋势图，可在后续卡中追加）
