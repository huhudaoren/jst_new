# 子 Agent 任务卡模板（主 Agent 填写）

> **使用方式**：每次派任务时，主 Agent 复制本模板填空，与 SUBAGENT_PROMPT.md 一起发给用户，
> 用户把两份文档拼起来发给一个新 Claude 会话作为初始 prompt

---

## 任务编号
F{X}: {feature 简短名称}

## 阶段
B (基线域) / C (交易主线) / D (核销/商城) / E (营销/财务/...)

## 涉及模块
jst-{module}

## 业务背景（1-2 段说人话）
{为什么做这个 feature, 解决什么问题, 谁会用}

## 必读上下文（任务相关，主 Agent 列出来子 Agent 必须读）
1. 架构设计/{某文档}.md § {某节}
2. 架构设计/ddl/{某 sql}.sql 表 {jst_xxx}
3. 架构设计/11-状态机定义.md § SM-{x}
4. 参考已有代码：{某 java 文件路径}

## 涉及表（数据库 schema）
- jst_xxx (主表) — 用途
- jst_yyy (关联表) — 用途

## 涉及状态机
SM-{x}（如有）

## 涉及锁名（必须先在 15 文档登记）
- lock:{name}:{id} — 用途
- 等待秒数 / 持有秒数

## 涉及权限标识
- jst:{module}:{resource}:list (查询)
- jst:{module}:{resource}:add (新增)
- ...

## 接口契约（API 列表）

### 接口 1: GET /jst/{module}/xxx/list
- 入参: XxxQueryDTO {字段}
- 出参: TableDataInfo<XxxListResVO>
- 权限: jst:{module}:{resource}:list
- 业务逻辑:
  1. ...
  2. ...

### 接口 2: POST /jst/wx/xxx/yyy ⭐ wx 端
- 入参: XxxReqDTO {字段+JSR-303 注解}
- 出参: XxxResVO
- 权限: hasRole('jst_student')
- 业务逻辑（必须包含锁/事务/状态机）:
  1. SecurityUtils.getUserId() 取当前用户
  2. jstLockTemplate.execute("lock:xxx:{id}", ...)
  3. 查 + 状态机校验
  4. 写库（乐观锁）
  5. 返回 ResVO

## 交付物清单（精确路径）

新增文件:
- `RuoYi-Vue/jst-{module}/src/main/java/com/ruoyi/jst/{module}/enums/XxxStatus.java`
- `RuoYi-Vue/jst-{module}/src/main/java/com/ruoyi/jst/{module}/dto/XxxReqDTO.java`
- `RuoYi-Vue/jst-{module}/src/main/java/com/ruoyi/jst/{module}/vo/XxxResVO.java`
- `RuoYi-Vue/jst-{module}/src/main/java/com/ruoyi/jst/{module}/mapper/XxxMapperExt.java` (扩展)
- `RuoYi-Vue/jst-{module}/src/main/java/com/ruoyi/jst/{module}/service/XxxService.java`
- `RuoYi-Vue/jst-{module}/src/main/java/com/ruoyi/jst/{module}/service/impl/XxxServiceImpl.java`
- `RuoYi-Vue/jst-{module}/src/main/java/com/ruoyi/jst/{module}/controller/XxxController.java` (admin)
- `RuoYi-Vue/jst-{module}/src/main/java/com/ruoyi/jst/{module}/controller/wx/WxXxxController.java` (wx)
- `RuoYi-Vue/jst-{module}/src/main/resources/mapper/{module}/XxxMapperExt.xml`

修改文件:
- `RuoYi-Vue/jst-common/.../BizErrorCode.java` (追加错误码 XXXXX/XXXXX)
- `架构设计/ddl/99-test-fixtures.sql` (追加测试数据)
- `test/api-tests.http` (追加测试块)

## 测试场景（.http 必须覆盖）

### F{X}-1 happy path: ...
### F{X}-2 状态非法跃迁应失败
### F{X}-3 越权访问应 403
### F{X}-4 ...

## DoD 验收标准

- [ ] mvn compile 18 模块全部 SUCCESS
- [ ] 所有 .http 测试用例 ✓ 绿色
- [ ] 自检清单 10 项全部 ✓
- [ ] commit message 格式: `feat(jst-{module}): F{X} {feature 名} (SM-x)`

## 不许做的事（白名单外都不许）

- ❌ 不许改 ddl/*.sql 主文件（仅可改 99-test-fixtures.sql）
- ❌ 不许改父 pom / application.yml
- ❌ 不许引入新依赖
- ❌ 不许写跨模块代码（仅本模块 + jst-common）
- ❌ 不许"顺手"清理生成代码

## 预计工作量
{N 小时} (供参考,实际不强求)

## 优先级
高/中/低

---

(主 Agent 的签名)
派发时间: 2026-04-08
版本: 任务卡 v1
