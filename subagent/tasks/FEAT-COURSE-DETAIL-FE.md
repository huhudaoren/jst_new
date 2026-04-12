# 任务卡 FEAT-COURSE-DETAIL-FE - 课程详情前端重写

## 阶段
阶段 F-BUG / **jst-uniapp**（MiniProgram Agent）

## 背景
FEAT-COURSE-DETAIL-BE 已补齐后端字段。本卡对照原型重写课程详情页。

## 必读
1. `小程序原型图/course-detail.html` + `course-detail.png` — **必须先 Read PNG**
2. `jst-uniapp/pages-sub/course/detail.vue` — 当前实现
3. `subagent/tasks/任务报告/FEAT-COURSE-DETAIL-BE-回答.md` — 确认字段

## 交付物

**修改** `pages-sub/course/detail.vue`，对照原型实现：

### 页面结构

1. **封面区**：课程封面 + 课程名 + 价格（大字红色）
2. **数据统计行**：3 个 stat cell（`lessonCount` 课时 | `learnerCount` 学习人数 | `totalDuration` 总时长）
3. **讲师卡片**：头像（`u-avatar`）+ 姓名 + 简介
4. **Tab 切换**：课程介绍 | 课程目录
   - 课程介绍：`rich-text` 渲染
   - 课程目录：解析 `chaptersJson`，折叠面板展示章节→课时列表，每课时显示时长 + 免费标记（`u-tag` "免费试看"）
5. **底部操作栏**：收藏 + 立即购买
   - 购买按钮：如果课程购买接口未实现，toast "课程购买即将开放"（不要写 "F9完成后开放"）

### 样式要求
- Design Token 零硬编码
- 骨架屏（`u-skeleton`）
- 讲师卡片有品牌渐变背景或柔和背景
- 课程目录折叠展开有动画

### 空数据兜底
- `chaptersJson` 为空 → 不显示课程目录 Tab
- `teacherName` 为空 → 不显示讲师卡片
- 统计数字为 0 → 显示 "0" 不隐藏

## DoD
- [ ] 对照原型 5 大区块
- [ ] 统计行 + 讲师卡 + 课程目录折叠面板
- [ ] 骨架屏
- [ ] Token 化
- [ ] 任务报告 `FEAT-COURSE-DETAIL-FE-回答.md`

## 依赖：FEAT-COURSE-DETAIL-BE ✅
## 优先级：P0
---
派发时间：2026-04-11
