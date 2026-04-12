# UI Refresh 报告 — UI-REFRESH-4 商城/公开页/剩余

## 状态：✅ 完成

## 刷新范围
- 页面：18 个（批次 4 全量）+ 3 个主页面补扫（首页/课程/我的）
- Token 新增：6 个（$jst-amber / $jst-amber-light / $jst-amber-gradient / $jst-indigo-gradient / $jst-purple / $jst-purple-dark）
- 组件引入：u-skeleton / u-empty / u-loadmore

## 组件替换统计
| 手写 → 组件 | 替换次数 |
|-------------|---------|
| 手写空态文字 → `u-empty` | 5 |
| 手写 loading/更多 → `u-loadmore` | 4 |
| 无骨架屏 → `u-skeleton` | 1 |
| 手写卡片入场 → CSS 动画 class | 1 |

## Token 化统计
| 类型 | 硬编码消除数 |
|------|------------|
| 颜色（#hex → $jst-*） | 162（153 批次4 + 9 主页补扫） |
| 字号 | ~45 |
| 间距 | ~60 |
| 圆角 | ~15 |
| 阴影 | ~12 |
| 字重 | ~20 |

## 视觉特效统计
| 页面 | 特效 | 实现方式 |
|------|------|---------|
| mall/list | 卡片交错入场 + 按压缩放 + FAB弹入 | CSS animation class + :active |
| mall/detail | 步进器按压反馈 | CSS :active transition |
| exchange-list | 卡片按压缩放 | CSS :active scale |
| exchange-detail | 时间线节点过渡 | CSS transition |
| points/center | 宫格+等级卡按压 | CSS :active scale |
| apply-status | 查询按钮按压 | CSS :active opacity |
| partner-apply | 提交按钮过渡 | CSS transition |
| score-query | Tab 切换过渡 | CSS transition |
| cert-verify | 查询按钮按压 | CSS :active |

## 逐页改动

### A. 积分商城（4 页）
- **mall/list**: 9→0 hex, +u-skeleton骨架屏, +u-empty, +u-loadmore, +stagger入场动画
- **mall/detail**: 7→0 hex, 全部 $jst-amber 替代 #F5A623
- **mall/exchange-list**: 5→0 hex, +u-empty, +u-loadmore, +按压反馈
- **mall/exchange-detail**: 7→0 hex, $jst-amber-gradient 统一

### B. 积分中心（2 页）
- **points/center**: 8→0 hex, 新增 $jst-purple token, +u-empty
- **points/detail**: 3→0 hex, +u-empty, +u-loadmore

### C. 权益中心（3 页）
- **rights/center**: 7→0 hex, darken($jst-success) 绿色渐变
- **rights/detail**: 5→0 hex
- **rights/writeoff-apply**: 6→0 hex

### D. 公开页面（5 页）
- **apply-status**: **30→0 hex**（最大量），状态chip对齐 §4.8 映射
- **partner-apply**: **25→0 hex**, 18处token替换
- **help**: 10→0 hex, icon背景改功能色light
- **score-query**: 6→0 hex
- **cert-verify**: 3→0 hex

### E. 公告/消息 + 课程/营销（4 页）
- **notice/detail**: 7→0 hex
- **notice/message**: 3→0 hex
- **course/detail**: 5 hex 在 `<script>` 富文本 sanitizer — **不可修改**
- **marketing/campaign**: 3→0 hex

### 补扫（主页面 3 个）
- **index/index**: 3→0 hex → $jst-purple-dark/$jst-purple
- **course/list**: 1→0 hex → $jst-brand-dark/$jst-brand
- **my/index**: 5→0 hex → $jst-indigo/$jst-gold

## 全量一致性扫描

### 硬编码色值
- **style 块**：pages/ 0 + pages-sub/ 0 = **全局零硬编码** ✅
- **script 块**：40 处（channel JS 渐变常量 + course 富文本 sanitizer），**均为业务逻辑不可修改**

### 状态标签颜色
全局统一 §4.8：pending→warning / approved→success / rejected→danger / cancelled→info ✅

### 骨架屏/加载态
列表页均有 u-loadmore/u-empty/jst-loading 覆盖 ✅

## Design Token 新增
```scss
$jst-amber: #F5A623;
$jst-amber-light: #FFF8EC;
$jst-amber-gradient: linear-gradient(135deg, #F5A623, #FF9800);
$jst-purple: #7B1FA2;
$jst-purple-dark: #4A0072;
$jst-indigo-gradient: linear-gradient(135deg, #25328f, #3553c4);
```

## 自检确认
- [x] script 块无非视觉改动
- [x] style 块零硬编码数值
- [x] 所有列表有空状态
- [x] 已有 class/id/ref 未删除
- [x] 19 个文件语法检查通过

## 遗留
- `<script>` 中 40 处 JS 渐变常量无法 token 化（业务逻辑红线）
