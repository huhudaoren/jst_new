# 任务卡 FIX-3 - 小程序端缺失页面补齐（8 页）

## 阶段
阶段 E 收尾 / **jst-uniapp**

## 背景
Test Agent Layer 1 发现用户端范围内有 8 个原型页面完全缺失（不含暂缓的 AI 课程/经营分析/合同发票/播放器）。这些页面在"我的"页面应该有入口但目前没有。

## PRD 依据
- my-score.html / my-cert.html：§7.9 个人中心"我的成绩""我的证书"
- my-message.html / notice-msg.html：§7.9 + §8.9 消息中心
- settings.html / privacy.html：§7.9 账号设置与隐私
- score-query.html / cert-verify.html：§7.1 公开查询（成绩/证书）

## 交付物

### 1. 我的成绩 `pages-sub/my/score.vue`
- 列表：按赛事分组，每条显示赛事名 / 成绩 / 排名 / 评语 / 发布时间
- 空状态："暂无成绩记录"
- API：`GET /jst/wx/enroll/my` 已有（复用报名列表，筛选已出成绩的）或新封装 `GET /jst/wx/score/my`
- 若后端缺接口，写字段缺口反馈文档

### 2. 我的证书 `pages-sub/my/cert.vue`
- 列表：每条显示赛事名 / 证书名 / 等级 / 证书编号 / 发证时间
- 操作：查看（大图预览）/ 下载（`uni.downloadFile` + `uni.saveFile`）
- API：`GET /jst/wx/cert/my`
- 若后端缺接口，写反馈文档

### 3. 个人消息 `pages-sub/my/message.vue`
- Tab：[系统通知] [业务消息]
- 卡片：标题 / 内容摘要 / 时间 / 已读状态
- 点击标记已读 + 跳对应业务页
- API：`GET /jst/wx/message/my` + `PUT /jst/wx/message/{id}/read`
- 若后端缺接口（大概率缺），写反馈文档。前端先做页面壳 + "暂无消息"空状态

### 4. 公共消息中心 `pages-sub/notice/message.vue`
- 合并通知 + 公告的统一入口
- 或直接复用 notice/list 改造成 Tab 切换（公告 / 消息）
- 若独立页面，从首页/TabBar 可达

### 5. 设置页 `pages-sub/my/settings.vue`
- 功能项列表：
  - 修改密码（渠道方/赛事方可能需要）
  - 清除缓存
  - 关于我们
  - 隐私政策（跳 privacy）
  - 用户协议
  - 退出登录（从 my/index 移过来或保留两处）
- 版本号展示

### 6. 隐私政策 `pages-sub/my/privacy.vue`
- 纯展示页：富文本渲染隐私政策内容
- 内容暂用占位文本（"隐私政策内容待运营提供"），结构完整
- 从 settings 页跳入

### 7. 成绩公开查询 `pages-sub/public/score-query.vue`
- 输入：姓名 + 准考证号 / 手机号
- 查询按钮 → `GET /jst/public/score/query?name=&idNo=`
- 结果：赛事名 / 成绩 / 排名
- 无结果提示
- 从首页或帮助页可达
- 若后端缺公开查询接口，写反馈文档

### 8. 证书验真 `pages-sub/public/cert-verify.vue`
- 输入：证书编号
- 查询按钮 → `GET /jst/public/cert/verify?certNo=`
- 结果：证书信息 + 真伪状态
- 从首页或帮助页可达
- 若后端缺公开查询接口，写反馈文档

### 9. 入口接入
**修改**：`pages/my/index.vue`

学生视角"我的服务"追加：
- "我的成绩" → `/pages-sub/my/score`
- "我的证书" → `/pages-sub/my/cert`
- "消息中心" → `/pages-sub/my/message`
- "设置" → `/pages-sub/my/settings`

首页或帮助页追加：
- "成绩查询" 入口
- "证书验真" 入口

**修改**：`pages.json` 注册 8 个新页面

### 10. API 封装
**新建**：`api/score.js` + `api/cert.js` + `api/message.js`

或追加到已有 api 文件中。

## DoD
- [ ] 8 个 .vue 文件全部新建
- [ ] pages.json 注册 8 条路由
- [ ] my/index 入口追加 4 个 tile
- [ ] 首页/帮助页入口追加 2 个
- [ ] 后端缺口反馈文档（预计 4~6 个接口需补）
- [ ] HBuilderX H5 验证
- [ ] 任务报告 `FIX-3-回答.md`

## 不许做
- ❌ 不许改后端
- ❌ 不许编造假数据（后端没接口就显示空状态）
- ❌ 不许跳过任何一个页面（8 个全部要做）
- ❌ 隐私政策内容用占位文本（"待运营提供"），不要瞎编

## 依赖：无（与 FIX-1/FIX-2 并行）
## 优先级：P2

---
派发时间：2026-04-10
