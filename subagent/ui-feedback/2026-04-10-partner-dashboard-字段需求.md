# 2026-04-10 赛事方工作台首页接口字段需求

## 背景

- 任务卡：[E2-PA-2-赛事方工作台首页.md](D:/coding/jst_v1/subagent/tasks/E2-PA-2-赛事方工作台首页.md)
- 前端页面已实现：[home.vue](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/views/partner/home.vue)
- 前端 API 接线已预留：[dashboard.js](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/api/partner/dashboard.js)

## 检查结论

2026-04-10 在本仓库后端模块中检索 `jst/partner/dashboard/*`、`jst/partner/notice/recent`，未发现已实现控制器或映射，当前需要后端补齐以下 3 个接口。

## 1. GET `/jst/partner/dashboard/summary`

### 用途

赛事方工作台首页 Hero 区 4 个 KPI。

### 建议返回

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "pendingEnrollCount": 12,
    "monthEnrollCount": 186,
    "pendingScoreCount": 8,
    "pendingCertificateCount": 25
  }
}
```

### 字段说明

- `pendingEnrollCount`: 待审报名数
- `monthEnrollCount`: 本月报名总数
- `pendingScoreCount`: 待发成绩数
- `pendingCertificateCount`: 待领证书数

## 2. GET `/jst/partner/dashboard/todo`

### 用途

工作台首页待办区，展示：
- 待审报名 Top 5
- 待发成绩 Top 5

### 建议返回

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "pendingEnrollList": [
      {
        "enrollId": 10001,
        "contestName": "2026 春季艺术赛",
        "studentName": "张三",
        "submitTime": "2026-04-10 09:30:00"
      }
    ],
    "pendingScoreList": [
      {
        "scoreId": 20001,
        "contestName": "2026 春季艺术赛",
        "stageName": "复赛",
        "deadlineTime": "2026-04-12 18:00:00"
      }
    ]
  }
}
```

### 字段说明

- `pendingEnrollList`: 待审报名列表，最多返回 5 条
- `pendingEnrollList[].enrollId`: 报名单 ID，前端用于跳转 `/partner/enroll-manage?enrollId=...`
- `pendingEnrollList[].contestName`: 赛事名称
- `pendingEnrollList[].studentName`: 学生姓名
- `pendingEnrollList[].submitTime`: 提交时间
- `pendingScoreList`: 待发成绩列表，最多返回 5 条
- `pendingScoreList[].scoreId`: 成绩记录 ID，前端用于跳转 `/partner/score-manage?scoreId=...`
- `pendingScoreList[].contestName`: 赛事名称
- `pendingScoreList[].stageName`: 赛段 / 轮次名称
- `pendingScoreList[].deadlineTime`: 预计处理截止时间

## 3. GET `/jst/partner/notice/recent`

### 用途

工作台首页平台通知区，展示最近 3 条公告。

### 建议返回

```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "noticeId": 30001,
      "title": "平台结算时间调整通知",
      "summary": "4 月结算处理时间调整为每周五统一执行。",
      "publishTime": "2026-04-09 15:20:00"
    }
  ]
}
```

### 字段说明

- `noticeId`: 公告 ID
- `title`: 公告标题
- `summary`: 公告摘要
- `publishTime`: 发布时间

## 4. 联调说明

- 当前前端已按上述字段名接线，后端若采用不同字段名，前端还需再做一轮兼容映射。
- 若计划复用现有公告表，请确认只返回赛事方可见公告，且按发布时间倒序取最近 3 条。
- 待办接口建议在服务端直接裁剪为 Top 5，避免首页再次拉全量数据。
