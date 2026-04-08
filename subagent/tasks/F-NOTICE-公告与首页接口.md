# 任务卡 F-NOTICE - 公告 + 首页 banner + 字典缓存接口

## ⚠️ 第二波豁免 (主 Agent 临时规则)
本任务卡**豁免**「BACKEND_AGENT_PROMPT.md §.http 测试规约」中的本地启动 + 跑测试要求。
原因:第二波 4 个 Agent 同时跑测试会抢 8080 端口,改为用户在阶段汇总时统一跑。
你需要做的:
- ✅ 仍然必须 mvn compile 18 模块 SUCCESS
- ✅ 仍然必须**追加** ### 测试块到 test/api-tests.http (语法正确,变量提取正确)
- ❌ **不要**尝试 kill 8080 / 启动 ruoyi-admin / 跑 .http 测试
- ✅ 报告 D 段写"测试块已追加,等待用户阶段汇总时统一跑"
- ✅ 报告中如有任何"我担心 X 接口可能跑不通"必须明确写出来

⚠️ **常见陷阱避坑**(从第一波 4 个 Agent 的踩坑总结,必读):
1. Mapper.xml 文件名**必须**含 "Mapper" 字样: `XxxMapper.xml` 或 `XxxMapperExt.xml`,**禁止** `XxxExt.xml`
2. 含 AES 加密字段的 ResultMap 必须声明 `typeHandler="com.ruoyi.jst.common.crypto.AesTypeHandler"`
3. 公开接口必须加 `@Anonymous` 注解
4. Mapper interface 与 xml namespace 必须**完全一致** (全限定类名)

## 阶段 / 模块
阶段 B / jst-message + jst-common (字典)

## 业务背景
本任务实现**前端首页 + 公告 tab** 所必需的最小后端接口集:
1. 公告 CRUD + 小程序查询
2. 首页 banner (临时方案: 取 top_flag=1 的公告作为 banner)
3. 业务字典缓存接口 (前端取下拉选项,如赛事分类)

**为什么把 3 件事合一**:都很轻,放一个任务卡更高效

## 必读上下文
1. `13` `15` `16`
2. `27-用户端API契约.md` § 3.9
3. `ddl/08-jst-message.sql` 表 `jst_notice`
4. `ddl/23-基础数据初始化.sql` (sys_dict_data 已就位的字典)
5. `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/controller/JstNoticeController.java` (gen 已生成,可改造)
6. `ParticipantClaimServiceImpl.java` 样板

## 涉及表
- jst_notice
- sys_dict_data (查字典,只读)

## 涉及状态机
无 (公告状态简单 draft/published/offline,不写状态机类)

## 涉及锁
无

## 涉及权限
- admin: `jst:message:notice:add`,`:edit`,`:publish`,`:offline`
- wx: `@Anonymous` (公告/字典/banner 全公开)

## 接口契约

### 1. (admin) POST /jst/message/notice/add
**用途**: 平台运营创建公告
**入参**: NoticeSaveReqDTO {@NotBlank title, @NotBlank category, @NotBlank content, String coverImage, Integer topFlag}
**业务**: INSERT, status=draft

### 2. (admin) PUT /jst/message/notice/edit
**业务**: 仅 draft/offline 状态可编辑

### 3. (admin) POST /jst/message/notice/{id}/publish
**业务**: status draft→published, publish_time=now()

### 4. (admin) POST /jst/message/notice/{id}/offline
**业务**: published→offline

### 5. (admin) GET /jst/message/notice/list
**入参**: NoticeQueryReqDTO {String category, String status, ...}

### 6. (wx) GET /jst/wx/notice/list ⭐
**用途**: 小程序公告列表
**入参**: WxNoticeQueryDTO {String category, Integer pageNum, Integer pageSize}
**出参**: TableDataInfo<WxNoticeCardVO>
```java
{
  Long noticeId;
  String title;
  String category;
  String coverImage;
  Boolean topFlag;
  Date publishTime;
  String summary;  // content 截取前 60 字
}
```
**业务**: 仅 status=published, 默认按 top_flag DESC, publish_time DESC

### 7. (wx) GET /jst/wx/notice/{id}
**用途**: 公告详情
**出参**: WxNoticeDetailVO (含完整 content)

### 8. (wx) GET /jst/wx/index/banner ⭐
**用途**: 首页轮播 banner
**出参**: AjaxResult<List<BannerVO>>
```java
{
  Long id;
  String type;     // 'notice' (本期只有公告)
  String title;
  String image;    // cover_image
  String linkUrl;  // /pages-sub/notice/detail?id=xxx
}
```
**业务**: 取 top_flag=1 AND status=published, 限 5 条
**权限**: `@Anonymous`

### 9. (wx) GET /jst/wx/dict/{dictType} ⭐
**用途**: 取业务字典(供小程序下拉/分类用)
**入参**: `@PathVariable String dictType`
**出参**: AjaxResult<List<{label, value, cssClass}>>
**业务**:
- 白名单校验:仅允许 jst_* 开头的字典(防越权读取系统字典)
- 走 ruoyi-system 的 SysDictDataMapper 查 dict_type=xxx AND status=0
- 注: 由于 jst-message 默认不依赖 ruoyi-system, 需轻量 SQL Mapper 跨域 SELECT (类似 PartnerLookupMapper 写法,在 jst-message 内建 DictLookupMapper, @Select 直接写 SQL)
**权限**: `@Anonymous`
**例**: GET /jst/wx/dict/jst_contest_category → 返回 8 个赛事分类

## 交付物清单

新增文件:
- `jst-message/.../dto/NoticeSaveReqDTO.java`
- `jst-message/.../dto/WxNoticeQueryDTO.java`
- `jst-message/.../vo/WxNoticeCardVO.java`
- `jst-message/.../vo/WxNoticeDetailVO.java`
- `jst-message/.../vo/BannerVO.java`
- `jst-message/.../mapper/NoticeMapperExt.java` + xml
- `jst-message/.../mapper/DictLookupMapper.java` (跨域 SELECT 字典)
- `jst-message/.../service/NoticeService.java`
- `jst-message/.../service/impl/NoticeServiceImpl.java`
- `jst-message/.../controller/wx/WxNoticeController.java`
- `jst-message/.../controller/wx/WxIndexController.java` (只放 banner 接口)
- `jst-message/.../controller/wx/WxDictController.java`

修改文件:
- gen 出来的 `JstNoticeController.java`: 改造或保留(本任务可保留 gen 出的 admin Controller,但增加 publish/offline 业务方法)
- `BizErrorCode.java`: 追加 `JST_MSG_NOTICE_NOT_FOUND(80001)`
- `99-test-fixtures.sql`: 追加 3 条 published 公告(1 条 top_flag=1 用于 banner 测试)
- `test/api-tests.http`: 追加 ### F-NOTICE 测试块

## 测试场景

### N-1 (admin) 创建公告 → 发布
### N-2 (wx) 公告列表 应有 fixture 中的 3 条
### N-3 (wx) 公告详情
### N-4 (wx) 首页 banner 应有 1 条 (top_flag=1)
### N-5 (wx) 字典 jst_contest_category 应返回 8 项
### N-6 (wx) 字典 sys_user_sex 应被白名单拦截 (返回空 / 错误)

## DoD
- [ ] mvn SUCCESS
- [ ] api-tests.http N-* 全部 ✓
- [ ] DictLookupMapper 仅 SELECT,无任何 INSERT/UPDATE
- [ ] 字典白名单生效

## 不许做的事
- ❌ 不许动 ddl
- ❌ 不许引入新依赖
- ❌ 不许实现 message_template / message_log (后续 H 阶段做)
- ❌ 不许"顺手"改 sys_dict 表

## 优先级
中高 (前端首页 + 公告 tab 阻塞依赖)

## 预计工作量
4-5 小时
