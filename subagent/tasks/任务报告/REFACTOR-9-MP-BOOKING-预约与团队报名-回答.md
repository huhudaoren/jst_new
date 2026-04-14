# 任务报告 - REFACTOR-9-MP-BOOKING 预约与团队报名逻辑防呆

> Agent: MiniProgram Agent | 完成时间: 2026-04-14

---

## A. 任务前自检（Step 2 答题）

1. **对应原型**: 小程序原型图/reserve.png（预约页），无独立团队报名 PNG（参照 enroll + channel/appointment 风格）
2. **调用接口**:
   - `GET /jst/wx/contest/{id}` — 赛事详情含 appointmentSlotList + teamMinSize/teamMaxSize (27 文档 §3.2)
   - `POST /jst/wx/appointment/apply` — 个人预约(slotId) (§3.5)
   - `GET /jst/wx/appointment/contest/{id}/slots` — 兜底获取时间段 (新增)
   - `POST /jst/wx/enroll/team/submit` — 团队报名提交 (§3.2 扩展)
3. **复用 store/api**: api/appointment.js, api/contest.js, api/enroll.js, api/participant.js
4. **新建文件**: `pages-sub/contest/team-enroll.vue`; **修改文件**: apply.vue, detail.vue, appointment.js, enroll.js, contest.js(utils), pages.json
5. **数据流**: contestDetail → appointmentSlotList → 按日期分组 → 用户选 slot → 确认 → 提交; contestDetail → teamMinSize → 步骤向导 → 提交
6. **双视角**: 否（均为学生可见）
7. **复用样板**: enroll.vue 表单模式 + channel/appointment.vue 成员选择
8. **核心 Token**: `$jst-brand`/`$jst-brand-gradient`(按钮), `$jst-bg-card`+`$jst-shadow-sm`(卡片), `$jst-success`(剩余), `$jst-danger`(已满), `$jst-warning`(快满)

---

## B. 交付物清单

### 新增文件
| 文件 | 说明 |
|---|---|
| `jst-uniapp/pages-sub/contest/team-enroll.vue` | 团队报名 3 步向导页（队长信息 → 队员管理 → 确认提交） |

### 修改文件
| 文件 | 变更说明 |
|---|---|
| `jst-uniapp/pages-sub/appointment/apply.vue` | **全量重写**: date-tab + slot 列表 + 确认弹窗，替换旧的 date-picker + session-picker |
| `jst-uniapp/pages-sub/contest/detail.vue` | 增加「团队报名」入口按钮 + `showTeamEnrollEntry` 计算属性 + `handleTeamEnrollTap` 方法；修复 appointmentTap 中 contestName 字段引用 |
| `jst-uniapp/api/appointment.js` | 新增 `getAppointmentSlots(contestId)` 函数；更新 `applyAppointment` 注释（slotId） |
| `jst-uniapp/api/enroll.js` | 新增 `submitTeamEnroll(data)` 函数 |
| `jst-uniapp/utils/contest.js` | `normalizeContestCard` 增加 teamMinSize/teamMaxSize/teamLeaderFields/teamMemberFields/allowRepeatAppointment/appointmentSlotList 字段 |
| `jst-uniapp/pages.json` | 注册 `pages-sub/contest/team-enroll` 路由 |

---

## C. 联调测试预期

| # | 场景 | 预期 |
|---|---|---|
| 1 | 进入赛事详情，赛事有 appointmentSlotList | 「我要预约」按钮可见 |
| 2 | 进入预约页 | 日期 Tab 展示，选日期切换时间段列表 |
| 3 | 点击已满时间段 | 灰色 + 「已满」角标 + toast 提示「该时间段已满」 |
| 4 | 快满时间段（< 20% 剩余） | 橙色剩余数字 |
| 5 | 选时间段 → 提交预约 | 确认弹窗 → 提交 → 跳转详情/支付 |
| 6 | 重复预约（allowRepeatAppointment=0） | 后端返回 30093 → 展示红色警告 |
| 7 | 赛事 teamMinSize > 0 | 详情页「团队报名」按钮可见 |
| 8 | 团队报名 Step 1 | 队长字段根据 teamLeaderFields 动态渲染 |
| 9 | Step 2 队员管理 | 队长不可移除 + 手动添加/档案选择/微信邀请 |
| 10 | 手机号重复 | 弹窗添加时实时提示 + 档案选择时 toast 提示 |
| 11 | 人数 < min / 人数 = max | 「还需添加 N 人」+ 禁用按钮 / 隐藏添加按钮 + 「已达最大人数」 |
| 12 | Step 3 确认提交 | 团队摘要 + 总费用 → 提交 → 跳转 |
| 13 | 微信分享邀请 | `onShareAppMessage` 配置带 contestId |

---

## D. 视觉对比

- ✅ 预约页：日期 Tab 横向滚动 + 时间段卡片列表 + 已满/快满/可选三态视觉
- ✅ 团队报名：3 步向导 + 步骤指示器 + 卡片风格与 enroll.vue 一致
- ✅ 所有样式使用 `design-tokens.scss` 全局 SCSS 变量，零硬编码色值/间距
- ✅ 底部按钮安全区处理 `env(safe-area-inset-bottom)`

---

## E. 防呆校验覆盖

| 防呆项 | 实现位置 | 状态 |
|---|---|---|
| 团队人数 < min → 不允许提交 | team-enroll.vue `memberShortage` computed + 按钮 `:disabled` | ✅ |
| 团队人数 > max → 不允许继续添加 | team-enroll.vue `isTeamFull` computed + `v-if` 隐藏添加按钮 | ✅ |
| 队员手机号重复 → 提示 | team-enroll.vue `duplicatePhoneWarn` computed + `addMemberFromPick` 中检查 | ✅ |
| 队长不可移除自己 | team-enroll.vue 队长独立展示，无移除操作 | ✅ |
| 预约时间段已满 → 不可选择 | apply.vue `isFull(slot)` + 灰化样式 + 点击 toast | ✅ |
| 重复预约 → 提示 | apply.vue `catch(e.code === 30093)` + `repeatWarning` 红色提示 | ✅ |
| 已报名该赛事 → 提示 | apply.vue `catch(e.code === 30094)` toast 提示 | ✅ |

---

## F. 遗留 TODO

| 项 | 优先级 | 说明 |
|---|---|---|
| 后端 `POST /jst/wx/enroll/team/submit` | **P0** | 团队报名接口需 Backend Agent 实现 |
| 后端 `GET /jst/wx/appointment/contest/{id}/slots` | P1 | 兜底接口，当前优先从 contestDetail.appointmentSlotList 获取 |
| 微信邀请被邀请方自动加入 | P2 | 当前 share 仅传递 contestId，被邀请者需手动进入页面填写 |
| participant.vue 适配 storageKey 参数 | P1 | 从档案选择添加队员需 participant 页支持自定义回传 key |

---

## G. 任务卡之外的修改

- 修复了 detail.vue 中 `handleAppointmentTap` 的 contestName 字段引用（原 `detail.name` → `detail.contestName`，与 `normalizeContestCard` 输出一致）
- `normalizeContestCard` (utils/contest.js) 增加了 6 个 team/slot 相关字段透传

---

## H. 自检确认

- [x] 没有页面内 mock 数据
- [x] 所有 API 通过 api/request.js 封装
- [x] 所有金额/手机号用脱敏展示（maskPhone 方法）
- [x] 没有引入新依赖
- [x] 没有改 RuoYi-Vue 后端代码
- [x] 没有改架构文档
- [x] DOM 标签已转为 view/text/image
- [x] 样式全部应用 design-tokens.scss 变量，未硬编码
- [x] 无中文全角引号
- [x] 无 template 属性内 ES6 模板字符串
- [x] pages.json 合法 JSON（已验证 12 个 subPackages）
- [x] 7 项防呆校验全部覆盖
