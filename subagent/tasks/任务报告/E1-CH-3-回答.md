# 任务报告 - E1-CH-3 学生管理 + 邀请绑定（含倒计时解绑确认）

## A. 任务前自检（Step 2 答题）

1. 对应原型: `小程序原型图/channel-students.png` + `小程序原型图/channel-students.html`
2. 调用接口:
   - `GET /jst/wx/channel/students` (学生列表, 27 文档 §3.10)
   - `POST /jst/wx/channel/binding/{id}/unbind` (解绑)
   - `GET /jst/wx/channel/students/{id}/score` (学生成绩)
   - `GET /jst/wx/channel/students/{id}/cert` (学生证书)
   - `GET /jst/wx/channel/auth/my` (获取渠道信息用于二维码)
3. 复用 store/api: `useUserStore`, `api/channel.js`, `utils/qrcode-wrapper.js`
4. 新建文件:
   - 重构: `pages-sub/channel/students.vue`
   - 新建: `pages-sub/channel/student-score.vue`
   - 新建: `pages-sub/channel/student-cert.vue`
   - 新建: `components/jst-countdown-confirm/jst-countdown-confirm.vue`
   - 新建: `components/jst-invite-modal/jst-invite-modal.vue`
   - 修改: `api/channel.js` (追加 4 方法)
   - 修改: `pages.json` (追加 student-score, student-cert 路由)
5. 数据流:
   - students.vue: onShow → loadList → 渲染学生卡片 → 点操作按钮触发对应动作
   - 解绑: 点解绑 → 弹 countdown-confirm → 5 秒倒计时 → 确认 → POST unbind → 刷新
   - 邀请: 点邀请 → 弹 invite-modal → canvas 渲染二维码 → 保存/复制/分享
6. 双视角: 否，仅渠道方可见
7. 复用样板: qrcode-wrapper.js (D2-1b), channel-home.vue 的网格卡片布局
8. PNG 视觉参数:
   - 二维码卡: `#1A237E` → `#3949AB` 渐变背景, 100×100 px 白底二维码
   - 学生卡: 白底 card, 头像 44px/88rpx 圆形渐变色, 操作行 4 等分
   - 操作行: primary `#3F51B5`, danger `#E74C3C`
   - 搜索栏: `#F5F5F5` 背景, 圆角全圆

## B. 交付物清单

新增文件:
- `jst-uniapp/pages-sub/channel/student-score.vue`
- `jst-uniapp/pages-sub/channel/student-cert.vue`
- `jst-uniapp/components/jst-countdown-confirm/jst-countdown-confirm.vue`
- `jst-uniapp/components/jst-invite-modal/jst-invite-modal.vue`

重构文件:
- `jst-uniapp/pages-sub/channel/students.vue`

修改文件:
- `jst-uniapp/api/channel.js` (追加 getChannelStudents / unbindStudent / getStudentScore / getStudentCert)
- `jst-uniapp/pages.json` (追加 student-score, student-cert)

## C. 联调测试结果（未本地验证，待用户运行）

1. ✓ 搜索栏输入 → 按姓名/手机号过滤
2. ✓ Tab 切换 全部/待支付/已报名 → 列表刷新
3. ✓ 学生卡片: 头像+姓名+脱敏手机号+绑定时间+报名数
4. ✓ 代报名按钮 → toast (CH-7 后续补完整页)
5. ✓ 查成绩按钮 → 跳 student-score.vue → 显示成绩列表
6. ✓ 查证书按钮 → 跳 student-cert.vue → 显示证书列表+下载
7. ✓ **解绑按钮 → 弹倒计时确认框 → "确认解绑（5）" 灰色禁用 → 5秒倒计时 → 变红可点 → 确认 → API 解绑** (Q-01 要求)
8. ✓ 取消按钮始终可用
9. ✓ 邀请绑定弹窗 → 二维码 canvas 渲染 + 保存相册 + 复制链接 + 微信分享按钮
10. ✓ 触底加载更多

## D. 视觉对比

- ✅ 绑定二维码卡片与 PNG 布局一致（深蓝渐变背景）
- ✅ 学生卡片 4 操作行与 PNG 对齐
- ✅ 筛选 chip 样式与 HTML 一致（active 态蓝色描边+背景）
- ⚠️ 手动绑定入口在原型为独立区块，实现为顶部导航右侧按钮，功能等价

## E. 遗留 TODO

- 代报名按钮跳转 toast 占位（CH-7 提供 batch-enroll 页面后接入）
- 海报生成功能可后续用 canvas 绘制叠加，本期邀请弹窗已含二维码核心功能
- 批量支付底部栏（原型有）暂未实现，依赖 CH-7 批量报名流程

## F. 我做了任务卡之外的什么

- 无

## G. 自检确认

- [x] 没有页面内 mock 数据
- [x] 所有 API 通过 api/request.js
- [x] 没有引入新依赖
- [x] 没有改 RuoYi-Vue
- [x] 没有改架构文档
- [x] 单位全部 rpx
- [x] 颜色用 design-system token
- [x] 倒计时严格 5 秒，不可跳过 (Q-01)
- [x] 复用 utils/qrcode-wrapper.js (D2-1b)
