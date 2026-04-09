# 任务报告 - D2-4 DEBT-4 基线完整性

## A. 任务前自检
1. 涉及文件：`23-基础数据初始化.sql`、`97-migration-baseline-roles.sql`、`CLAUDE.md`
2. 本卡只做 SQL / 文档，不改 Java 代码
3. 目标角色基线：`100=jst_student`、`101=jst_partner`、`102=jst_channel`、`103=jst_platform_op`

## B. 实现结果
### 1. 修复 23 基线
- 在 [23-基础数据初始化.sql](D:/coding/jst_v1/架构设计/23-基础数据初始化.sql) 修正：
  - `sys_role` 基线角色改为 `student / partner / channel / platform_op`
  - 新增 `role_id=103, role_key=jst_platform_op`
  - `sys_role_menu` 硬编码 role_id 同步对齐
  - line 173 附近注释改为“一级目录由基线初始化，手写 Controller 权限点走 `ddl/95-migration-phase-*.sql` 补齐”
- 只调整了角色初始化与相关注释，未改动其他基础字典或参数数据

### 2. 新增兼容 migration
- 新建 [97-migration-baseline-roles.sql](D:/coding/jst_v1/架构设计/ddl/97-migration-baseline-roles.sql)
- 脚本特性：
  - 仅补缺失角色
  - 不覆盖已有 `role_name`
  - 不删除任何历史角色
  - 可重复执行
  - 对 role_id 冲突环境保持保守，不做覆盖式修复，留给人工评估

### 3. CLAUDE.md 同步
- 在 [CLAUDE.md](D:/coding/jst_v1/CLAUDE.md) 的 DDL 清单中补充 `97-migration-baseline-roles.sql` 说明

## C. 测试库验证
测试库：`jdbc:mysql://59.110.53.165:3306/jst_new`

执行前快照：
```text
100    jst_student     学生账号
101    jst_partner     赛事方账号
102    jst_channel     渠道方账号
```

验证动作：
1. 执行 [97-migration-baseline-roles.sql](D:/coding/jst_v1/架构设计/ddl/97-migration-baseline-roles.sql)
2. 再执行一次同脚本，验证幂等

执行后快照：
```text
100    jst_student       学生账号
101    jst_partner       赛事方账号
102    jst_channel       渠道方账号
103    jst_platform_op   平台运营
```

结论：
- 第 1 次执行成功补齐 `jst_platform_op`
- 第 2 次执行无重复插入，幂等成立

## D. 交付物清单
新增文件：
- [97-migration-baseline-roles.sql](D:/coding/jst_v1/架构设计/ddl/97-migration-baseline-roles.sql)

修改文件：
- [23-基础数据初始化.sql](D:/coding/jst_v1/架构设计/23-基础数据初始化.sql)
- [CLAUDE.md](D:/coding/jst_v1/CLAUDE.md)

## E. 风险与说明
- 本次 migration 采用“只补缺失角色”的保守策略，不会覆盖历史环境中已存在的自定义 `role_name`
- 若某个旧环境已经把 `100/101/102/103` 占给了其他 `role_key`，本脚本不会强行纠正，需要人工评估后再处理
- 为完成测试库验证，我使用 JDBC 方式执行了脚本两次；`jshell` 直接读取中文路径会报 `InvalidPathException`，因此改为复制到 ASCII 临时路径后执行，再删除临时文件

## F. 自检清单
- [x] 23 基线修复问题 1/2/3
- [x] 97 migration 可重复执行
- [x] 测试库执行前/执行后快照已记录
- [x] CLAUDE.md 已同步
- [x] 未改 Java 代码
- [x] 未修改 95/96 既有 migration
- [x] 任务报告已输出
