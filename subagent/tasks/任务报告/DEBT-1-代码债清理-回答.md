# 任务报告 - DEBT-1 代码债清理

## A. 任务前自检
1. 涉及表：`sys_user_role`、`sys_role`、`jst_event_partner`、`jst_enroll_record`
2. 涉及状态机：
   - 渠道认证审批后的角色授予链路修正，不新增状态机
   - 报名补件仍遵循 `SM-6`
3. 涉及权限标识：无新增，仅修复内部实现
4. 涉及锁名：沿用既有 `lock:org:apply:{applyId}`、`lock:enroll:submit:{userId}:{contestId}`、`lock:enroll:audit:{enrollId}`
5. 事务边界：`WxAuthServiceImpl.login`、`PartnerApplyServiceImpl.approve`、`EnrollRecordServiceImpl.supplement`
6. ResVO 字段：仅按任务卡新增 `EnrollDetailVO.supplementRemark`
7. 复用样板：
   - 轻量跨域只读 SQL 复用 `PartnerLookupMapper`
   - organizer 内部扩展写库复用 `SysUserExtMapper + xml` 模式

## B. 交付物清单
新增文件：
- `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/mapper/RoleLookupMapper.java`
- `RuoYi-Vue/jst-organizer/src/main/java/com/ruoyi/jst/organizer/mapper/EventPartnerWriteMapper.java`
- `RuoYi-Vue/jst-organizer/src/main/resources/mapper/organizer/EventPartnerWriteMapper.xml`

修改文件：
- `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/auth/impl/WxAuthServiceImpl.java`
- `RuoYi-Vue/jst-organizer/pom.xml`
- `RuoYi-Vue/jst-organizer/src/main/java/com/ruoyi/jst/organizer/service/impl/PartnerApplyServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/domain/JstEnrollRecord.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/mapper/EnrollRecordMapperExt.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/EnrollRecordServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/EnrollDetailVO.java`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/JstEnrollRecordMapper.xml`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/EnrollRecordMapperExt.xml`
- `test/admin-tests.http`
- `test/wx-tests.http`

## C. 实现说明
1. 债务 1 已修复：
   - `WxAuthServiceImpl.buildRoles` 不再根据 `user_type` 或渠道档案推断角色
   - 新增 `RoleLookupMapper`，从 `sys_user_role + sys_role` 读取真实角色
   - 默认仍补 `jst_student`，数据库角色按 `role_key` 去重追加
2. 债务 2 已修复：
   - `jst-organizer/pom.xml` 已删除 `jst-event` 依赖
   - `PartnerApplyServiceImpl` 不再 import `com.ruoyi.jst.event.*`
   - 新增 `EventPartnerWriteMapper`，用轻量 SQL 完成 `jst_event_partner` 的 insert / update
   - `ChannelAuthApplyServiceImpl` 已检查，无类似跨域 entity/service 依赖，无需改动
3. 债务 3 已修复：
   - `supplementRemark` 已切到 `jst_enroll_record.supplement_remark`
   - `JstEnrollRecord`、`JstEnrollRecordMapper.xml`、`EnrollRecordMapperExt.xml`、`EnrollDetailVO` 均已同步
   - `EnrollRecordServiceImpl.supplement` 改为走 `updateSupplementRecord`
   - 更新时显式将 `remark = null`，避免补件说明继续污染旧备注字段

## D. 验证结果
1. `mvn compile -DskipTests`
   - 执行目录：`D:\coding\jst_v1\RuoYi-Vue`
   - 结果：18 模块 `BUILD SUCCESS`
   - 完成时间：`2026-04-08 22:13:26 +08:00`
   - 总耗时：`34.221 s`
2. 模块边界核验：
   - `jst-organizer/pom.xml` 中已无 `jst-event`
   - `Get-ChildItem ... | Select-String "com.ruoyi.jst.event"` 在 `jst-organizer/src/main/java` 下返回 0 条
3. `.http` 测试块：
   - `test/admin-tests.http` 已追加 `DEBT-1-T1/T2`
   - `test/wx-tests.http` 已追加 `DEBT-1-W1/W2`
   - 按任务卡豁免规则，本轮未实际启动 `ruoyi-admin`，也未执行 `.http`

## E. 风险 / 遗留说明
- `RoleLookupMapper` 基于 `sys_user_role` 的真实授权读取角色，前提是 F6 审核流已正确写入 `sys_user_role`；本轮未额外改动该链路。
- `EventPartnerWriteMapper.insertEventPartner` 通过 MyBatis `useGeneratedKeys` 回填 `partnerId`；编译已通过，运行态主键回填需在阶段联调时随 F5/F6 一并验证。

## F. 任务卡外说明
- 未修改 `BizErrorCode.java`
- 未修改任何 controller 签名
- 未改 DDL，也未引入新依赖

## G. 自检清单确认
- [x] `WxAuthServiceImpl.buildRoles` 不再依赖 `user.getUserType()`
- [x] `jst-organizer/pom.xml` 不再包含 `jst-event`
- [x] `jst-organizer` 源码中 `import com.ruoyi.jst.event` 为 0
- [x] `supplement_remark` 已完成实体、Mapper、VO、Service 全链路切换
- [x] `mvn compile` 18 模块成功
- [x] 已按任务卡补充 `.http` 测试块
