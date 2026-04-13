# WEB-ADMIN-POLISH 交付报告（页面精品化）
## 1. 任务概览
- 任务卡：`subagent/tasks/WEB-ADMIN-POLISH-页面精品化.md`
- 目标：10 个后台页面统一升级为精品页标准（Hero、`query-panel`、PC/手机双列表、详情 Drawer、帮助 Popover）。
- 重点：`course/edit.vue` 改造为 5 Tab，体验对齐 `partner/contest-edit.vue`，并保持业务逻辑不变。

## 2. 已完成内容（10 页）
### 2.1 课程模块
- `RuoYi-Vue/ruoyi-ui/src/views/jst/course/edit.vue`
  - 从平铺卡片改为 Hero + 5 Tab：
    - 基本信息、讲师信息、课程目录、课程介绍、统计信息
  - 每个 Tab 顶部增加简短帮助文案。
  - 保持脚本业务链路不变（增删改排与提交流程保持原调用）。
- `RuoYi-Vue/ruoyi-ui/src/views/jst/course/index.vue`
  - 新增 Hero、帮助 Popover、刷新入口。
  - 查询区域统一为 `query-panel`。
  - 统一桌面表格 + 手机卡片交互。

### 2.2 优惠券与权益模块
- `RuoYi-Vue/ruoyi-ui/src/views/jst/coupon/template.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/rights/template.vue`
  - 新增 Hero + 帮助 + `query-panel`。
  - 编辑由 `el-dialog` 改为 `el-drawer`（PC 55% / Mobile 100%）。
- `RuoYi-Vue/ruoyi-ui/src/views/jst/coupon/issued.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/rights/issued.vue`
  - 新增 Hero + 帮助 + `query-panel`。
  - 补充只读详情 Drawer（最小 UI 状态：当前行数据 + 开关状态）。

### 2.3 商城模块
- `RuoYi-Vue/ruoyi-ui/src/views/jst/mall/index.vue`
  - 新增 Hero + 帮助 + `query-panel`。
  - 编辑由 Dialog 改为 Drawer（PC 55% / Mobile 100%）。
  - 增加库存预警视觉（低库存高亮，表格与手机卡片均生效）。
- `RuoYi-Vue/ruoyi-ui/src/views/jst/mall/exchange.vue`
  - 新增 Hero + 帮助 + `query-panel`。
  - 保留并统一原有详情 Drawer 体验。

### 2.4 表单模板与公告模块
- `RuoYi-Vue/ruoyi-ui/src/views/jst/form-template/index.vue`
  - 新增 Hero + 帮助 + `query-panel`。
  - 编辑由 Dialog 改为 Drawer。
  - 保留 JSON Schema 输入与预览能力。
- `RuoYi-Vue/ruoyi-ui/src/views/jst/notice/index.vue`
  - 新增 Hero + 帮助 + `query-panel`。
  - 编辑由 Dialog 改为 Drawer。
  - 保留发布/下线业务流程。

## 3. 边界与约束执行情况
- 未新增后端接口、未修改 API 地址、未调整权限点字符串。
- 业务流程保持不变（审核、上/下架、提审、发货、保存等入口仍为原方法链路）。
- 响应式统一使用 `@media (max-width: 768px)`，移动端按钮触控尺寸已优化。

## 4. 构建验证
- 执行目录：`D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui`
- 命令：`npm run build:prod`
- 结果：**通过**
- 说明：仅有历史体积 warning（asset/entrypoint size limit），不影响构建通过。

## 5. 交付文件清单
- `RuoYi-Vue/ruoyi-ui/src/views/jst/course/edit.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/course/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/coupon/template.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/coupon/issued.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/rights/template.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/rights/issued.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/mall/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/mall/exchange.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/form-template/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/notice/index.vue`
