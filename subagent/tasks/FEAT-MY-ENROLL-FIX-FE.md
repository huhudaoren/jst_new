# 任务卡 FEAT-MY-ENROLL-FIX - 我的页面布局 + 报名流程修复

## 阶段
阶段 F-BUG / **jst-uniapp**（MiniProgram Agent，纯前端）

## 背景
用户反馈 my/index 布局问题（积分重复、渠道入口位置、姓名溢出）和报名页交互问题（缺步骤条、参赛人选择体验差、必填标注不明显、提交失败）。

## 必读
1. `小程序原型图/my.png` + `my.html`
2. `小程序原型图/enroll.png` + `enroll.html`
3. `jst-uniapp/pages/my/index.vue`
4. `jst-uniapp/pages-sub/contest/enroll.vue`
5. `RuoYi-Vue/doc/bug.md` — 用户原始反馈

## 交付物

### Part A — 我的页面布局

**修改** `pages/my/index.vue`：

1. **头部简化**：
   - 姓名加 `text-overflow: ellipsis`，最大宽度限制，不挤压头像
   - 编辑按钮改为明显的图标按钮（铅笔图标 + 浅色背景圆形）
   - 移除头部积分/等级小标签（和下方成长值模块重复）
   - 头部只保留：头像 + 姓名（截断） + 手机号 + 编辑按钮
2. **成长值模块**：保留为独立卡片（积分 + 成长值 + 等级进度条）
3. **渠道入口调整**：
   - 非渠道用户：不显示顶部"学生/渠道方"切换器，改为底部功能区独立 cell："申请成为渠道方"（图标 + 文字 + 箭头）
   - 已认证渠道用户：保留顶部切换器

### Part B — 报名页优化

**修改** `pages-sub/contest/enroll.vue`：

1. **步骤条**：顶部 `u-steps`（填写信息 → 确认支付 → 报名完成），当前步骤 active=0
2. **参赛人选择改善**：
   - 点击选择区域弹出 `u-popup` 列表，展示已有参赛人
   - 列表为空时提示"暂无参赛人" + "去添加"按钮（跳转 `pages-sub/my/participant`）
   - 选中后自动回填姓名等字段
3. **必填项红色标注**：`*` 用 `color: $jst-danger; font-size: $jst-font-md;`
4. **提交失败排查**：
   - 检查提交 API 的请求参数是否完整（console.log 请求体）
   - 检查后端报错信息（如 500 则看具体缺什么字段）
   - 在报告中说明失败原因和修复方式
5. **动态表单**："联调后显示"文案全部移除，确认 `jst-form-render` 正常渲染

### Part C — 全局"查看更多"统一

`grep -rn "查看更多\|更多"` 所有 .vue 文件，统一改为带箭头图标：
```vue
<view class="jst-more-link" @tap="...">
  <text>查看更多</text>
  <u-icon name="arrow-right" size="24rpx" />
</view>
```

在 `styles/` 中补全局 class。

## DoD
- [ ] my/index 头部简化 + 成长值卡片独立 + 渠道入口下移
- [ ] enroll 步骤条 + 参赛人弹窗选择 + 必填红色标注
- [ ] 报名提交可用（排查并修复失败原因）
- [ ] "联调后显示"文案零残留
- [ ] "查看更多"全局统一箭头
- [ ] 任务报告 `FEAT-MY-ENROLL-FIX-FE-回答.md`

## 不许做
- ❌ 不许改后端
- ❌ 不许 mock 数据
- ❌ 不许改支付逻辑

## 依赖：无（纯前端）
## 优先级：P1
---
派发时间：2026-04-11
