# 任务卡 FIX-COMPILE - 小程序编译与运行时问题批量修复

## 阶段
阶段 F-BUG / **jst-uniapp**（MiniProgram Agent）

## 背景
HBuilderX 编译到微信小程序后，存在多类警告和错误。需要批量扫描修复，确保零编译错误、零运行时报错。

## 必读
1. `CLAUDE.md`
2. 微信小程序 WXML 模板语法限制：https://uniapp.dcloud.io/use?id=key

## 问题清单与修复要求

### 问题 1：`:key` 表达式不支持（警告，约 20 处）

微信小程序不支持 `:key` 中使用表达式（如 `'sk-'+i`、`item.id+'-dot'`、`item.$orig.bindingId||item.$orig.studentId`）。

**修复方法**：改用简单字段或 index。
```
// 错误
:key="'sk-'+i"
:key="item.label+'-'+index"
:key="item.$orig.bindingId||item.$orig.studentId"

// 正确
:key="i"
:key="index"
:key="item.bindingId || index"  // 如果有唯一字段用字段，否则用 index
```

**需要修复的文件**（grep 全量扫描确认）：
- pages/index/index.vue — `'sk-'+i`
- pages/course/list.vue — `'sk-'+i`
- pages/contest/list.vue — `'sk-'+i`
- pages/notice/list.vue — `'sk-'+i`
- pages/my/index.vue — `'sk-'+i`
- pages-sub/channel/appointment.vue — `stu.$orig.bindingId||stu.$orig.studentId` / `'m-'+idx`
- pages-sub/channel/batch-enroll.vue — `stu.$orig.bindingId||stu.$orig.studentId` / `'sp-'+idx`
- pages-sub/channel/data.vue — `item.contestId||idx` / `item.$orig.enrollId||item.$orig.orderId`
- pages-sub/appointment/writeoff-record.vue — `item.$orig.writeoffItemId||item.$orig.id`
- pages-sub/appointment/detail.vue — `item.$orig.writeoffItemId||idx`
- pages-sub/points/detail.vue — `item.$orig.ledgerId||idx`
- pages-sub/my/score.vue — `item.$orig.scoreId||item.$orig.enrollId`
- pages-sub/my/refund-detail.vue — `item.label+'-'+index`
- pages-sub/my/order-detail.vue — `item.label+'-'+index`
- pages-sub/my/enroll-detail.vue — `item+'-'+index`
- components/jst-banner-swiper/jst-banner-swiper.vue — `item.id+'-dot'`
- components/jst-form-render/jst-form-render.vue — `item.m7+'-'+index` / `option.m15+'-'+option.$orig.value` 等

### 问题 2：`new Date()` iOS 兼容性（约 5-10 处）

iOS 不支持 `new Date("2026-04-11 19:48:47")`（空格分隔），必须用 `T` 分隔或 `/` 分隔。

**修复方法**：所有 `new Date(str)` 调用前，先 `.replace(/ /g, 'T')`。

**全量扫描**：`grep -rn "new Date(" pages/ pages-sub/ components/ utils/ --include="*.vue" --include="*.js"`，逐个检查是否传入了带空格的字符串。

建议在 `utils/` 中新建一个安全的日期解析函数供全局复用：
```js
// utils/date.js
export function safeParseDate(value) {
  if (!value) return null
  const d = new Date(String(value).replace(/ /g, 'T'))
  return isNaN(d.getTime()) ? null : d
}
```

### 问题 3：template 中的特殊字符（已修 1 处，需全量扫描）

`<` 字符在 template 的文本节点中需要转义为 `&lt;`，否则微信编译器把它当成标签。

**全量扫描**：检查所有 `.vue` 文件 template 中是否有裸 `<` 字符（不是标签开头的），改为 `&lt;` 或用 unicode `‹`。

### 问题 4：中文引号残留（已修 3 个文件，需全量确认）

中文双引号 `""` 和单引号 `''` 在微信小程序模板中不合法。

**全量扫描**：`grep -Pn '[\u201c\u201d\u2018\u2019]'` 所有 .vue 文件，替换为英文直引号。

### 问题 5：`$jst-*` 变量 Undefined（已修 16 个文件，需全量确认）

使用了 `$jst-*` SCSS 变量但缺少 `@import '@/styles/design-tokens.scss'`。

**全量扫描**：找出所有 `<style lang="scss">` 中用了 `$jst-` 但没有 import 的文件。

## 执行步骤

1. 对每个问题类型，先 grep 全量扫描确认完整文件列表
2. 逐文件修复
3. 修完后 HBuilderX 编译到微信小程序 → **零 Error**
4. 微信开发者工具控制台 → **零红色错误**（警告可接受）

## DoD
- [ ] `:key` 表达式全部改为简单值（零 "不支持表达式" 警告）
- [ ] `new Date()` 全部 iOS 兼容
- [ ] template 中无裸 `<` 字符
- [ ] 零中文引号残留
- [ ] 零 `$jst-*` Undefined
- [ ] HBuilderX 编译微信小程序零 Error
- [ ] 微信开发者工具零红色错误
- [ ] 任务报告 `FIX-COMPILE-回答.md`

## 不许做
- ❌ 不许改业务逻辑
- ❌ 不许改后端
- ❌ 不许引入新依赖

## 优先级：P0（阻塞测试验证）
---
派发时间：2026-04-12
