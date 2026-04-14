# REFACTOR-4-ENROLL 前端 — 交付报告

> Agent: Web Admin Agent | 日期: 2026-04-14 | 状态: ✅ 完成

---

## 一、任务概述

为报名审核管理页面增加：成绩打分一体化、卡片滑动审核视图、评审老师配置。

## 二、改动文件

| 文件 | 类型 | 说明 |
|------|------|------|
| `src/api/partner/enroll.js` | 修改 | +`listScoreItems(contestId)` |
| `src/api/partner/contest.js` | 修改 | +`listContestReviewers` / `saveContestReviewers` / `searchSystemUsers` |
| `src/views/partner/enroll-manage.vue` | 修改 | 列表增强 + 筛选 + 卡片审核模式 |
| `src/views/partner/components/EnrollDetailDrawer.vue` | 修改 | 审核+打分一体化弹窗 |
| `src/views/partner/contest-edit.vue` | 修改 | 新增 Tab G "评审老师" |

## 三、功能明细

### 3.1 enroll-manage.vue 列表增强

**新增 3 列**：
- 总分（`scoreTotal`）— 有分数时高亮绿色
- 证书编号（`certNo`）— 未生成显示灰色标签
- 评审老师（`reviewerName`）

**新增筛选条件**：
- 分数范围（`scoreMin` / `scoreMax`）— 双 input-number
- 评审老师姓名
- 证书状态（已生成/未生成）

### 3.2 审核+打分一体化弹窗

**EnrollDetailDrawer 改造**：
- "通过"按钮改为调用 `handleApprove()`
- 自动检测赛事是否有成绩项（`listScoreItems`）
- 有成绩项 → 弹出打分面板（el-dialog）
  - 每项一行：项目名 | 满分 | 权重 | 输入分数
  - 自动计算加权总分（绿色高亮）
  - 确认后一次性提交 `audit + scores`
- 无成绩项 → 直接走原有审核流程

### 3.3 卡片滑动审核视图

**全屏 el-dialog**，布局：
- **顶部**：标题 + 快捷键提示 + 关闭按钮
- **左侧（3/5）**：报名详情卡片（学生信息 + 表单快照 + 作品材料）
- **右侧（2/5）**：成绩打分面板 + 审核备注 + 通过/驳回按钮
- **底部**：进度条 + 上一条/下一条 导航

**快捷键**：
| 键 | 功能 |
|----|------|
| `←` | 上一条 |
| `→` | 下一条 |
| `Enter` | 通过（需不在输入框中） |
| `Esc` | 关闭审核视图 |

**操作后自动前进**：审核操作成功后 500ms 自动切到下一条，最后一条完成时自动关闭。

**响应式**：手机浏览器下左右区域改为上下堆叠，快捷键提示隐藏。

### 3.4 赛事编辑增加评审老师配置

在 `contest-edit.vue` 新增 **Tab G "评审老师"**（原 G 常见问题改为 Tab H）：
- el-select remote 搜索系统用户（`/system/user/list`）
- 选中后自动填充姓名
- 角色切换：主评（`chief_reviewer`）/ 副评（`reviewer`）
- 状态开关：启用/停用
- 独立保存按钮（调用 `saveContestReviewers`）
- 编辑模式自动加载已有评审老师

## 四、构建验证

```
npm run build:prod → DONE Build complete ✅
```

## 五、DoD 检查

- [x] enroll-manage.vue 列表增强（证书编号、总分、评审老师列）
- [x] 审核+打分一体化弹窗
- [x] 卡片滑动审核视图（含键盘快捷键）
- [x] 赛事编辑增加评审老师配置
- [x] `npm run build:prod` 通过
- [x] 响应式适配
- [x] 报告交付
