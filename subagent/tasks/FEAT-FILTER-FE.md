# 任务卡 FEAT-FILTER-FE - 多级筛选前端

## 阶段
阶段 F-BUG / **jst-uniapp**（MiniProgram Agent）

## 背景
FEAT-FILTER-BE 已补齐分类接口和列表多条件查询。本卡实现筛选组件和列表页改造。

## 必读
1. `小程序原型图/contest-list.html` + `contest-list.png`
2. `jst-uniapp/pages/contest/list.vue`
3. `jst-uniapp/pages/course/list.vue`
4. `subagent/tasks/任务报告/FEAT-FILTER-BE-回答.md`

## 设计决策
**筛选模式**：顶部筛选栏 + 下拉面板（美团/大众点评风格）

## 交付物

### Part A — 通用筛选组件

**新建** `components/jst-filter-bar/jst-filter-bar.vue`

Props：
```js
{
  // 筛选项配置
  filters: [
    { key: 'category', label: '分类', type: 'grid', options: [{label, value}] },
    { key: 'sortBy', label: '排序', type: 'radio', options: [{label, value}] },
    { key: 'more', label: '筛选', type: 'multi', groups: [{title, key, options}] }
  ],
  // 当前值
  value: { category: '', sortBy: 'default', eventType: '' }
}
```

Events：`@change(newValue)` — 任意筛选变化时触发

UI 行为：
- 顶部一行 3 个按钮，文字 + 上下箭头图标
- 有活动筛选时按钮高亮（品牌色）
- 点击展开下拉面板：半透明遮罩 + 内容区从顶部滑下
- `type: 'grid'`：4 列网格布局，单选
- `type: 'radio'`：单选列表，带选中勾
- `type: 'multi'`：分组多选 + 底部"重置/确认"按钮
- 点击遮罩关闭面板

### Part B — 赛事列表改造

**修改** `pages/contest/list.vue`：

1. 搜索栏下方加 `jst-filter-bar`，配置：
   - 分类：从 `/jst/wx/dict/contest-categories` 接口获取选项
   - 排序：综合 / 最新上架 / 热门推荐 / 报名截止临近
   - 筛选：赛事形式（线上/线下/混合）
2. 筛选变化时重新请求列表（带 category/sortBy/eventType 参数）
3. 赛事卡片增加多彩标签行（`recommendTags`）
4. 搜索美化：加搜索图标，`u-search` 样式对齐原型

### Part C — 课程列表改造

**修改** `pages/course/list.vue`：

1. 复用 `jst-filter-bar`
2. 分类：从 `/jst/wx/dict/course-categories` 获取
3. 排序：综合 / 最新 / 最热

### Part D — API 封装

```js
// api/dict.js（如不存在则新建）
export function getContestCategories() { return request({ url: '/jst/wx/dict/contest-categories', method: 'GET' }) }
export function getCourseCategories() { return request({ url: '/jst/wx/dict/course-categories', method: 'GET' }) }
```

## DoD
- [ ] jst-filter-bar 通用组件完成
- [ ] 赛事列表 3 维筛选 + 多彩标签
- [ ] 课程列表 2 维筛选
- [ ] 筛选联动列表请求
- [ ] 搜索栏美化
- [ ] Token 化
- [ ] 任务报告 `FEAT-FILTER-FE-回答.md`

## 依赖：FEAT-FILTER-BE ✅
## 优先级：P1
---
派发时间：2026-04-11
