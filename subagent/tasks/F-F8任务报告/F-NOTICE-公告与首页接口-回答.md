# 任务报告 - F-NOTICE 公告 + 首页 banner + 字典缓存接口

## A. 任务前自检
1. 涉及表：`jst_notice`、`sys_dict_data`
2. 涉及状态机：无（公告仅 `draft/published/offline` 简单状态，本次未单独建状态机类）
3. 涉及权限标识：`jst:message:notice:list`、`jst:message:notice:add`、`jst:message:notice:edit`、`jst:message:notice:publish`、`jst:message:notice:offline`、小程序公开接口 `@Anonymous`
4. 涉及锁名：无
5. 事务边界：`NoticeServiceImpl.addNotice`、`NoticeServiceImpl.editNotice`、`NoticeServiceImpl.publishNotice`、`NoticeServiceImpl.offlineNotice`，均为 `@Transactional(rollbackFor = Exception.class)`
6. ResVO 字段：`WxNoticeCardVO(noticeId/title/category/coverImage/topFlag/publishTime/summary)`、`WxNoticeDetailVO(noticeId/title/category/coverImage/topFlag/publishTime/content)`、`BannerVO(id/type/title/image/linkUrl)`、字典项 `{label,value,cssClass}`
7. 复用样板：`RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java`

## B. 交付物清单
新增文件：
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/dto/NoticeSaveReqDTO.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/dto/NoticeQueryReqDTO.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/dto/WxNoticeQueryDTO.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/vo/WxNoticeCardVO.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/vo/WxNoticeDetailVO.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/vo/BannerVO.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/mapper/NoticeMapperExt.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/mapper/DictLookupMapper.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/service/NoticeService.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/service/impl/NoticeServiceImpl.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/controller/wx/WxNoticeController.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/controller/wx/WxIndexController.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/controller/wx/WxDictController.java`
- `RuoYi-Vue/jst-message/src/main/resources/mapper/message/NoticeMapperExt.xml`

修改文件：
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/controller/JstNoticeController.java`
- `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/exception/BizErrorCode.java`
- `架构设计/ddl/99-test-fixtures.sql`
- `test/api-tests.http`

## C. mvn compile 结果
`[INFO] BUILD SUCCESS`

`[INFO] Total time: 01:09 min`

## D. .http 测试结果
测试块已追加，等待用户阶段汇总时统一跑。

已补充 `F-NOTICE-1 ~ F-NOTICE-7`，覆盖创建并发布公告、公告列表、公告详情、banner、业务字典、非白名单字典拦截。

## E. 遗留 TODO
- 按任务卡豁免规则，本次未实际启动 `ruoyi-admin` 和执行 `.http`，运行结果待阶段汇总统一验证。
- 公告富文本清洗当前复用若依现有 `EscapeUtil.clean()/HTMLFilter`，若后续前端依赖更丰富的标签白名单，可能需要再细化过滤策略。

## F. 我做了任务卡之外的什么
- 新增了 `NoticeQueryReqDTO.java`，因为任务契约里管理端 `/list` 已明确是 `NoticeQueryReqDTO`，任务卡文件清单未列出该最小辅助类。
- 调整了 `BizErrorCode` 中 `8xxxx` 号段：新增 `JST_MSG_NOTICE_NOT_FOUND(80001)`，顺延模板/发送错误码到 `80002/80003`。

## G. 自检清单确认
- [x] 管理端方法已显式 `@PreAuthorize`，公开接口已加 `@Anonymous`
- [x] ReqDTO 已补 JSR-303
- [x] 小程序出参已用 VO / 字典对象返回
- [x] 不涉及敏感字段明文输出
- [x] 写操作已加 `@Transactional`
- [x] 本任务无高并发写场景，无需加锁
- [x] 无 `${}` 拼 SQL
- [x] `DictLookupMapper` 仅 `SELECT`
- [x] 字典白名单已限制为 `jst_*`
- [x] 已追加 fixture 与 `.http` 测试块
