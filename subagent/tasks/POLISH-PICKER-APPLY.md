# POLISH-PICKER-APPLY — 全站剩余 _id 输入框替换为 Picker

> 优先级：**P1** | 预估：**M**（1-1.5 天） | Agent：**Web Admin Agent**
> 派发时间：2026-04-18 | 版本：任务卡 v1
> **前置依赖**：POLISH-PICKER-TEMPLATE-FAMILY **必须先完成**（本卡要用它产出的 4 个 Picker）

---

## 一、业务背景

Plan-06 已经把 56 处 `xxx_id` 输入框改成 RelationPicker，但还有 ~17 处漏网之鱼（主要在模板/优惠券/权益相关字段），用户目前仍在手输数字 ID。本卡是收尾：把所有剩余的 `_id` / `templateId` / `couponTemplateId` / `rightsTemplateId` 从 `<el-input>` / `<el-input-number>` 改成对应 Picker。

---

## 二、必读上下文

1. `CLAUDE.md` § 四 / § 五（编码规范）
2. `subagent/WEB_ADMIN_AGENT_PROMPT.md`
3. **已有框架**（照抄用法）：
   - `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/{Channel,User,Sales,Contest,Participant,Partner,Order,FormTemplate,CertTemplate,CouponTemplate,RightsTemplate}Picker.vue`（11 个，其中后 4 个是前置任务产出的）
   - `RuoYi-Vue/ruoyi-ui/src/components/EntityLink.vue`（展示场景用，本卡基本不用）
4. **参考 plan-06 已完成的改造提交**：
   ```bash
   git log --all --oneline | grep -iE "picker|plan-06|UX-POLISH" | head -20
   # 挑一个 commit 看 diff 风格
   git show <commit> -- '*.vue'
   ```
   改造模式：`<el-input v-model="form.xxxId"/>` → `<channel-picker v-model="form.xxxId"/>` + `import ChannelPicker from '@/components/RelationPicker/ChannelPicker.vue'` + `components: { ChannelPicker }`。

---

## 三、扫描 → 改造 → 验证 三步走

### Step 1：全量扫描产出改造清单

**禁止跳过本步**。先扫出所有待改点，产出一个 CSV 贴在报告里，我（主 Agent）review 后再让你批量改。

#### 1.1 扫描命令

```bash
cd RuoYi-Vue/ruoyi-ui

# 扫所有直接绑 *_id 的 el-input
grep -rEn 'v-model="[^"]*\.(channelId|userId|salesId|contestId|participantId|partnerId|orderId|templateId|formTemplateId|certTemplateId|couponTemplateId|rightsTemplateId|sceneId|issueId|ruleId|batchId|exchangeOrderId|alertId|auditLogId|messageId|noticeId|courseId|goodsId)[^"]*"' src/views/jst src/views/partner src/views/sales

# 扫 el-input-number 绑 _id
grep -rEn 'el-input-number.*v-model="[^"]*Id[^"]*"' src/views/jst src/views/partner src/views/sales

# 扫 prop="xxx_id" 的 table column，这些是展示场景，通常不归本卡管（归 Task 3 MAPPER-NAME-JOIN 处理），但也扫一遍心里有数
grep -rEn 'prop="[a-z_]*_id"' src/views/jst src/views/partner | head -30
```

#### 1.2 分类

扫描结果按实体归类到表格（报告里以 markdown 表格呈现）：

| 文件 | 行号 | 字段 | 应替换成 | 是否编辑态（form 内） |
|---|---|---|---|---|
| `views/jst/xxx/index.vue` | 123 | `form.channelId` | `<channel-picker>` | 是 |
| ... | ... | ... | ... | ... |

**判断规则（非常重要）**：
- **编辑态 / 新建态（Dialog/Drawer 里的 `<el-form>`）**→ 换 Picker（这是本卡的目标）
- **列表筛选区（页顶 `<el-form :inline="true">`）**→ **也换 Picker**（筛选时同样需要搜名字）
- **只读详情展示（`<el-descriptions-item>`、`<el-table-column>`）**→ **不归本卡**，换 `<EntityLink>` 或交给 Task 3（MAPPER-NAME-JOIN）处理
- **系统自动塞的 hidden 字段**（`v-show="false"` 或仅 data 里有、模板里不出现的）→ **不动**
- `role_id` / `menu_id` / `dept_id` / `dict_id` 等 **若依系统表字段 → 不动**（那是若依自己的页面）

#### 1.3 清单交主 Agent review

写完表格后，**先不动代码**，输出清单等我回"OK，开工"后再进 Step 2。

---

### Step 2：批量替换（严格按清单）

#### 2.1 替换套路（**每个文件按这 3 步，不要跳步**）

**原：**
```vue
<el-form-item label="所属渠道" prop="channelId">
  <el-input v-model="form.channelId" placeholder="请输入渠道ID" />
</el-form-item>

<script>
export default {
  components: { /* ... */ }
}
</script>
```

**改后：**
```vue
<el-form-item label="所属渠道" prop="channelId">
  <channel-picker v-model="form.channelId" />
</el-form-item>

<script>
import ChannelPicker from '@/components/RelationPicker/ChannelPicker.vue'
export default {
  components: { /* 原有 */, ChannelPicker }
}
</script>
```

**3 步清单**：
1. template 里 `<el-input>` / `<el-input-number>` 替换为 `<xxx-picker v-model="...">`（保留原 `prop` 供表单校验）
2. `<script>` 顶部 `import XxxPicker from '@/components/RelationPicker/XxxPicker.vue'`
3. `components: { ... XxxPicker }` 追加

#### 2.2 特殊情况处理

| 场景 | 处理 |
|---|---|
| **筛选区 inline form** | 同样换，v-model 绑 `queryParams.xxxId`，Picker props 加 `clearable` |
| **value 类型是 String 但 Picker 默认接 Number** | BasePicker 的 `value: { type: [Number, String] }` 已兼容，直接改即可 |
| **同一表单多次用同一 Picker**（比如渠道 + 上级渠道） | 两个独立 `<channel-picker>`，不要硬 merge |
| **有 `:disabled="isEdit"` 逻辑**（编辑态锁住） | `<channel-picker :disabled="isEdit">` 一样传 |
| **原本有 `@change` 联动** | Picker 也 emit `@change`（传递完整 item 对象），按原逻辑接即可 |
| **原本有自定义校验 rules** | `form.rules` 不动，Picker 兼容（本质还是 `<el-select>`） |

#### 2.3 每批改 5-8 个文件就 commit 一次

避免一次 push 20 个文件挂了回滚痛苦。commit message 格式：
```
feat(admin): POLISH-PICKER-APPLY 批次 N — views/jst/xxx 等 5 文件 _id→Picker
```

---

### Step 3：验证

#### 3.1 编译 / 构建

```bash
cd RuoYi-Vue/ruoyi-ui && npm run build:prod
```

**期望**：`Build complete.` 无 Error，**新增 Warning 不超过 2 条**（超过要排查，大概率是 import 路径写错）。

#### 3.2 功能冒烟（挑 3 个代表性页面手动跑）

选以下 3 种场景各测一个：
1. **新建场景**：打开新建 Dialog，Picker 能搜索、能选中、能保存 → 列表刷新后新记录 OK
2. **编辑场景**：打开编辑 Dialog，Picker 有 hydrate（回显已选值的名字）→ 改选其他值保存 → 列表刷新 OK
3. **筛选场景**：列表页顶部 Picker 筛选 → 清空 Picker → 筛选 reset OK

截图 3 张放到报告附件（用项目已有的 Claude Preview 截图或手动说明即可，没有截图工具就文字描述"已在浏览器验证 3 场景通过"）。

#### 3.3 回归扫描（**最后一步，防漏**）

再跑一次 Step 1 的扫描命令，**期望清单里只剩下 § 1.2 规则判定为"不动"的那类**。如果还有编辑态/筛选区的 `_id` 输入框没改，回 Step 2 补。

---

## 四、DoD 验收标准

- [ ] Step 1 清单文档已提交主 Agent review 并获批
- [ ] Step 2 分批改完，每批独立 commit，改动文件数 ≥ 10（plan-06 改了 56 处，本卡收尾预计 15-20 处）
- [ ] `npm run build:prod` ✅ 无新 Error
- [ ] 3 场景冒烟通过（新建 / 编辑 / 筛选）
- [ ] Step 3.3 回归扫描只剩"不动"类
- [ ] 报告末尾列出"已改文件统计表 + 不动清单 + 不动原因"三张表

---

## 五、红线（不许做的事）

- ❌ 不许改 Picker 组件本身（它是通用组件，有问题回任务 POLISH-PICKER-TEMPLATE-FAMILY）
- ❌ 不许改 `EntityBriefController` 后端（本卡纯前端）
- ❌ 不许改**若依系统表**相关页面（`views/system/`、`views/monitor/`、`views/tool/`）
- ❌ 不许"顺手"改样式、布局、文案——只换组件
- ❌ 不许把详情展示区（`<el-descriptions-item>` / `<el-table-column>`）里的 `_id` 改成 Picker（那是 EntityLink 或 Task 3 的职责）
- ❌ 不许一次性 1 个 commit 改完所有文件——必须分批
- ❌ 不许跳过 Step 1 清单直接开改——清单是防御线

---

## 六、派发附言

本卡对 Agent 的理解力要求：能看懂 Vue 2 单文件组件、会用 grep。不要求后端技能。

如果 Step 2 某个页面改完发现 Picker 的 entity 值对不上（比如发现一个 `exchangeOrderId` 字段但 `EntityBriefController` 没有 `exchangeOrder` 这个 type），**先跳过该条 + 在报告里列出"需后端扩展的实体"**，不要自己去改后端——那需要另起任务卡。

派发时间：2026-04-18 | 主 Agent 签名：竞赛通架构师
