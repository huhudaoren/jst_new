# 任务卡 DEBT-1 - 代码层技术债清理 (3 项)

## 背景

阶段 B + F9 完成后累积了 3 个**代码层**技术债。本任务卡集中清理。
**与 C2 + P6 完全并行,改的文件路径不重叠**。

## 阶段 / 模块
debt cleanup / jst-user + jst-organizer + jst-event

## 必读上下文

1. `架构设计/13/15/16` 全局
2. `BACKEND_AGENT_PROMPT.md` 强约束 (尤其 §Mapper.xml 规约 + §数据准备规约)
3. 第一波/第二波子 Agent 的实现 (作为修改对象)
4. 本任务卡的"具体清单"段

## 涉及债务清单

### 债务 1: F4 buildRoles 应查 sys_user_role 而非 user_type 推断 ⭐

**文件**: `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/auth/impl/WxAuthServiceImpl.java`

**当前代码** (子 Agent F4 写的):
```java
private List<SysRole> buildRoles(JstUser user) {
    List<SysRole> roles = new ArrayList<>();
    roles.add(buildRole("jst_student"));
    if ("channel".equals(user.getUserType())) {
        roles.add(buildRole("jst_channel"));
    }
    return roles;
}
```

**问题**:
- 角色权威源应该是 `sys_user_role` 表,不是 `jst_user.user_type`
- F6 渠道认证审核通过时**只**写 sys_user_role,**不**改 user_type
- 因此当前 buildRoles 永远不会给 F6 后变成 channel 的用户加 jst_channel 角色 → bug

**正确实现**:
1. jst-user 模块新增 `RoleLookupMapper.java` (轻量 SQL,类似 PartnerLookupMapper)
2. SQL: `SELECT r.role_key FROM sys_user_role ur JOIN sys_role r ON ur.role_id=r.role_id WHERE ur.user_id = #{userId} AND r.del_flag='0' AND r.status='0'`
3. WxAuthServiceImpl.buildRoles 改为:
```java
private List<SysRole> buildRoles(JstUser user) {
    List<SysRole> roles = new ArrayList<>();
    // jst_student 是默认角色,所有 wx 注册用户都有
    roles.add(buildRole("jst_student"));
    // 真实角色从 sys_user_role 查
    List<String> dbRoleKeys = roleLookupMapper.selectRoleKeysByUserId(user.getUserId());
    for (String key : dbRoleKeys) {
        if (!"jst_student".equals(key)) {  // 去重
            roles.add(buildRole(key));
        }
    }
    return roles;
}
```
4. 删除 `if ("channel".equals(user.getUserType()))` 旧逻辑

**测试验证**:
- 用 1001 登录 → roles 应只有 jst_student
- 通过 F6 给 1001 加 jst_channel 角色 → 1001 重新登录 → roles 应有 [jst_student, jst_channel]

### 债务 2: F5 jst-organizer 跨域依赖 jst-event Entity

**文件**: 
- `RuoYi-Vue/jst-organizer/pom.xml`
- `RuoYi-Vue/jst-organizer/src/main/java/com/ruoyi/jst/organizer/service/impl/PartnerApplyServiceImpl.java` (已 import JstEventPartner)

**当前问题**:
- jst-organizer/pom.xml 加了 `jst-event` 依赖
- PartnerApplyServiceImpl 直接 import `com.ruoyi.jst.event.domain.JstEventPartner`
- 这违反"模块间不直 import Entity"的设计原则,导致 jst-organizer → jst-event 强耦合

**正确实现**:
1. jst-organizer 内新增轻量 Mapper (类似已有的 SysUserExtMapper):
   - `EventPartnerWriteMapper.java` + xml
   - 仅包含 INSERT 和 UPDATE jst_event_partner 的 SQL,**不**映射 Entity
   - 用 `Map<String, Object>` 或 `@Param` 单字段传参
2. PartnerApplyServiceImpl.approve 不再 new JstEventPartner Entity,改为构造 `Map<String, Object>` 传给 EventPartnerWriteMapper
3. 删除 jst-organizer/pom.xml 的 `jst-event` 依赖
4. 删除所有 `import com.ruoyi.jst.event.*` 语句
5. 编译验证 18 模块 SUCCESS

**注意**: 同样的问题不要复制到 jst-organizer 的 ChannelAuthApplyServiceImpl,如有类似的跨域 Entity 依赖也一并清理

### 债务 3: F9 supplement_remark 列已就位,代码切换

**主 Agent 已做**: 
- DDL `95-migration-2026-04-08-add-enroll-supplement-remark.sql` 写好了
- `02-jst-event.sql` 已同步更新
- 用户需要在 MySQL 跑迁移 SQL

**子 Agent 要做**:
1. 修改 `EnrollRecordServiceImpl.supplement` 方法,把 `supplementRemark` 落到新加的 `supplement_remark` 字段而非 `remark`
2. 修改 `JstEnrollRecord` Entity 加 `supplementRemark` 字段
3. 修改 `JstEnrollRecordMapper.xml` 的 resultMap + insert + update 加 `supplement_remark` 列映射
4. 修改 `EnrollRecordMapperExt.xml` 如有相关 SQL 同步更新
5. 修改 `EnrollDetailVO` 加 supplementRemark 字段
6. 编译验证

## 涉及表
- jst_enroll_record (新加 supplement_remark 列,已 DDL 就位)
- sys_user_role (只读,跨域 SQL)
- sys_role (只读)
- jst_event_partner (写,通过新建的 EventPartnerWriteMapper)

## 涉及锁
无 (本任务都是已有逻辑修复,不涉及新锁)

## 涉及权限
无 (仅修复内部实现,不涉及新接口)

## 交付物清单

新增文件:
- `jst-user/.../mapper/RoleLookupMapper.java` (轻量 SQL 查 sys_user_role)
- (注: RoleLookupMapper 用 @Select 注解写 SQL 即可,无需单独 xml)
- `jst-organizer/.../mapper/EventPartnerWriteMapper.java` + xml

修改文件:
- `jst-user/.../auth/impl/WxAuthServiceImpl.java` (修 buildRoles)
- `jst-organizer/pom.xml` (删 jst-event 依赖)
- `jst-organizer/.../service/impl/PartnerApplyServiceImpl.java` (改 import + 改 approve 用 Map 写)
- `jst-organizer/.../service/impl/ChannelAuthApplyServiceImpl.java` (如有类似问题一并处理)
- `jst-event/.../domain/JstEnrollRecord.java` (加 supplementRemark 字段)
- `jst-event/.../mapper/JstEnrollRecordMapper.xml` (resultMap + insert + update 同步)
- `jst-event/.../service/impl/EnrollRecordServiceImpl.java` (supplement 方法落到正确字段)
- `jst-event/.../vo/EnrollDetailVO.java` (加 supplementRemark)
- `jst-common/.../BizErrorCode.java` (如需追加新错码)

## 测试场景

### DEBT-1-T1 (债务 1 验证)
1. 在 admin-tests.http 追加: 用 admin 给 1001 用户的 sys_user_role 加 jst_channel 角色
2. 1001 重新登录 wx → 期望 wxLoginResVO.roles 应包含 jst_channel
3. 不通过 user_type 推断

### DEBT-1-T2 (债务 2 验证)
1. 跑现有的 admin-tests.http F5-D4 (创建 partner apply + approve)
2. 验证: jst_event_partner 新行 user_id 字段非空
3. 验证: jst-organizer 模块编译时不需要 jst-event 包

### DEBT-1-T3 (债务 3 验证)
1. wx-tests.http F9-W4 supplement 测试时
2. UPDATE 后查 jst_enroll_record: supplement_remark 列应有值,remark 列应为 null

## DoD

- [ ] mvn compile 18 模块 SUCCESS
- [ ] 3 个验证场景全过
- [ ] jst-organizer/pom.xml 不再含 `jst-event` 依赖
- [ ] grep `import com.ruoyi.jst.event` 在 jst-organizer 全模块**零结果**
- [ ] WxAuthServiceImpl.buildRoles 不再依赖 user.getUserType()
- [ ] EnrollRecord 的 supplement_remark 列正常读写

## 不许做的事

- ❌ 不许动 ddl/02-jst-event.sql (主 Agent 已经做好)
- ❌ 不许扩范围,不在清单内的"顺手优化"全部禁止
- ❌ 不许引入新依赖
- ❌ 不许修改 controller 接口签名
- ❌ 不许修改 ReqDTO/ResVO 字段(除 EnrollDetailVO 加 supplementRemark)

## ⚠️ 第二波豁免依然适用
本任务卡仍然**豁免**「BACKEND_AGENT_PROMPT.md §.http 测试规约」中的本地启动 + 跑测试要求。
你只追加测试块到 admin-tests.http,由用户阶段汇总时统一跑。

## 与其他任务的并行性

- ✅ 与 C2 完全独立 (C2 改 jst-order,本任务改 jst-user/organizer/event,但只改少数几个文件,不改 BizErrorCode)
- ✅ 与 P6 完全独立 (P6 改前端)
- ⚠️ 与 C2 共享文件: 如果 C2 子 Agent 同时改 BizErrorCode.java,可能冲突。本任务**不**追加新错码,所以 OK
- ⚠️ 与 C2 共享 mvn install: 不要同时跑 mvn

## 优先级
中 (修复已有 bug,但不阻塞业务)

## 预计工作量
0.5-1 天 (3 项独立修复,1 个 Backend Agent)
