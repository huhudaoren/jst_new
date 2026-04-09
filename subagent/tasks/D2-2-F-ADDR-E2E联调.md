# 任务卡 D2-2 - F-USER-ADDRESS 真实 HTTP 联调回归

## 阶段 / 模块
阶段 D 批 2 / **jst-user**（全栈联调）

## 背景
F-USER-ADDRESS 任务卡已完成代码 + DDL + 前端 + compile SUCCESS，但原 Agent 因 `contestLookupMapper` Bean 冲突阻塞，未跑真实 HTTP 联调。Bean 冲突已由 F-USER-ADDRESS 顺手修复（rename 为 `MarketingContestLookupMapper`），本卡重启服务后做完整回归。

## 交付物

### 1. 重启后端
```bash
cd D:/coding/jst_v1/RuoYi-Vue
# 确认启动成功，无 ConflictingBeanDefinitionException
mvn -pl ruoyi-admin spring-boot:run
# 或按项目约定的 restart-admin.ps1
```

- 若仍有 Bean 冲突，**立即停止**，grep 所有剩余 `contestLookupMapper` / `@Mapper` 同名定义，在报告里列出
- 启动成功后 tail 日志确认无 ERROR

### 2. 执行 F-ADDR 测试段
**文件**：`test/wx-tests.http`

按顺序执行 `F-ADDR-0 ~ F-ADDR-9.1` 全部用例，覆盖：
- F-ADDR-0 登录（拿 token）
- F-ADDR-1 空列表
- F-ADDR-2 首次新增默认地址
- F-ADDR-3 列表（1 条）
- F-ADDR-4 再新增 1 条非默认
- F-ADDR-5 设为默认
- F-ADDR-6 详情
- F-ADDR-7 修改
- F-ADDR-8 删除
- F-ADDR-9 删除后列表
- F-ADDR-9.1 夹具账号 9001 查看预置默认地址

**每条用例记录**：HTTP code / 响应 body 关键字段 / 是否符合预期。

### 3. 重点验证项
- [ ] 设为默认时自动取消其他地址的 `is_default`（锁 `lock:user:address:default:{userId}` 生效）
- [ ] 删除走 `del_flag`（软删除），DB 直接查可验证
- [ ] 手机号在 DB 存储为密文（JstCipher），VO 返回时解密回明文
- [ ] 历史明文 fixture 能兼容读（不抛解密异常）
- [ ] 非本人地址无法 get/update/delete（越权防护）
- [ ] 删除不存在或已软删的地址返回 `JST_USER_ADDRESS_NOT_FOUND`

### 4. 联调发现的问题处理
- **小 bug（≤5 行可修）**：直接修，记录在报告
- **大 bug / 设计缺陷**：不修，记录在报告 §遗留，由主 Agent 决策下一张卡
- **前端问题**：若 address-list.vue / address-edit.vue 真机预览异常，记录截图描述，但**不动 mall/detail.vue**

## DoD
- [ ] 后端启动无 Bean 冲突
- [ ] F-ADDR 11 条用例全绿（或逐条说明失败原因）
- [ ] 手机号加解密行为验证通过
- [ ] 越权场景验证通过
- [ ] 任务报告 `D2-2-F-ADDR-E2E-回答.md` 含每条用例结果
- [ ] 若有代码修正：commit: `fix(jst-user): D2-2 F-USER-ADDRESS 联调修复 <简述>`
- [ ] 无修正时仍写报告，commit: `test(jst-user): D2-2 F-USER-ADDRESS 联调通过`

## 不许做
- ❌ 不许改 `jst_user_address` 表结构
- ❌ 不许改 HMAC / 加密密钥
- ❌ 不许跳过某条用例，必须逐条跑
- ❌ 不许动前端地址页视觉（那是 UI Polish Agent 的活）

## 依赖：无
## 优先级：高

---
派发时间：2026-04-09
