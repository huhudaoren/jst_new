# REFACTOR-4-ENROLL — 报名审核 + 成绩打分 + 评审系统

> 优先级：P1 | 预估：L | Agent：Backend Agent + Web Admin Agent（分两阶段）
> 依赖：REFACTOR-1-DDL（成绩项子表）、REFACTOR-6-BIZ-NO（证书编号）

---

## 一、背景

当前报名审核仅支持基础通过/驳回，缺少：成绩打分、评审老师分配、快速审核视图、证书编号生成。

## 二、Backend 改造

### 2.1 新增评审老师表

```sql
CREATE TABLE IF NOT EXISTS jst_contest_reviewer (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  contest_id     BIGINT NOT NULL COMMENT 'FK jst_contest',
  user_id        BIGINT NOT NULL COMMENT 'FK sys_user（评审老师账号）',
  reviewer_name  VARCHAR(50) COMMENT '老师姓名',
  role           VARCHAR(20) DEFAULT 'reviewer' COMMENT 'chief_reviewer/reviewer',
  status         TINYINT DEFAULT 1 COMMENT '1启用 0停用',
  create_by      VARCHAR(64) DEFAULT '',
  create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_contest_user (contest_id, user_id),
  INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事评审老师';
```

### 2.2 审核接口增强

`PUT /jst/partner/enroll/{enrollId}/audit` 增加可选参数：
- `scores`: `[{itemId, score}]` — 各成绩项的打分（来自 jst_score_item）
- 审核通过时写入 `jst_score_record`

### 2.3 批量审核增强

`PUT /jst/partner/enroll/batch-audit` 支持批量通过+统一打分（同一分数）

### 2.4 评审老师列表接口

- `GET /jst/partner/contest/{contestId}/reviewers` — 获取赛事评审老师列表
- `POST /jst/partner/contest/{contestId}/reviewers` — 配置评审老师
- 评审老师登录后 `GET /jst/partner/enroll/list` 自动过滤只返回其负责赛事的报名

### 2.5 证书编号自动生成

审核通过 + 打分完成 → 自动调用 `BizNoGenerateService.nextNo("cert_no")` 生成证书编号，写入 `jst_cert_record`

## 三、Web Admin 改造

### 3.1 enroll-manage.vue 增强

**列表信息扩充**：
- 新增列：证书编号（cert_no）、总分（score_total）、评审老师
- 筛选条件增加：分数范围、评审老师、证书状态

**审核+打分一体化**：
- 审核通过时，自动弹出打分面板（el-dialog）
- 打分面板：
  - 展示该赛事的所有成绩项（从 `jst_score_item` 获取）
  - 每项一行：项目名 | 满分 | 输入分数（el-input-number）
  - 自动计算加权总分
  - 确认后一次性提交 audit + scores

### 3.2 卡片滑动审核视图

新增视图切换按钮（列表 / 卡片审核）：

**卡片审核模式**（el-dialog 全屏或大弹窗）：
- 左侧：报名详情卡片（学生信息 + 表单快照 + 作品材料）
- 右侧：操作区
  - 成绩打分面板（各项分数输入）
  - 审核备注
  - 通过 / 驳回按钮
- 底部：进度条（第 N/总数 条）+ 上一条/下一条 导航
- 快捷键：← 上一条、→ 下一条、Enter 通过、Esc 驳回
- 操作后自动切到下一条（带 0.5s 切换动画）

### 3.3 赛事编辑增加评审老师配置

在 contest-edit.vue 的 Tab D 或新增 Tab 中：
- 评审老师选择：el-select multiple，从系统用户中搜索选择
- 显示已选老师列表（姓名 + 角色）
- 主评/副评角色切换

## 四、DoD

- [ ] jst_contest_reviewer 建表
- [ ] 审核接口支持打分
- [ ] 评审老师 CRUD 接口
- [ ] enroll-manage.vue 列表增强（证书编号、总分、评审老师列）
- [ ] 审核+打分一体化弹窗
- [ ] 卡片滑动审核视图
- [ ] 赛事编辑增加评审老师配置
- [ ] `mvn compile` + `npm run build:prod` 通过
- [ ] 报告交付
