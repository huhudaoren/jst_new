# 任务卡 F-USER-ADDRESS - 用户收货地址 CRUD

## 阶段 / 模块
阶段 D / **jst-user** + **jst-uniapp**

## 背景
C8 积分商城实物商品需要收货地址，当前前端 `mall/detail.vue` 用 `uni.chooseAddress` 微信原生兜底。本卡补齐用户地址管理全链路：建表 + 后端 CRUD + 前端列表/编辑页 + mall 对接。

## ⚠️ Schema 现状（主 Agent 已 probe 活库）
- `jst_user_address` 表**不存在**
- 本卡需新建该表（迁移脚本）

## CCB 决策（已定）

| # | 决策 | 选择 |
|---|---|---|
| 1 | 表名 | `jst_user_address` |
| 2 | 是否支持多地址 | 是，每用户多条 |
| 3 | 默认地址 | 一个用户至多 1 条 `is_default=1`；设置默认时自动取消其他 |
| 4 | 字段 | id / user_id / receiver_name / receiver_mobile / province / city / district / address_detail / postal_code / is_default / 审计列 |
| 5 | 敏感字段加密 | mobile 参考 jst-common `JstEncryptUtils` 按项目惯例处理（grep 现有 user 手机号加密方式） |
| 6 | 软删除 | 走 `del_flag` |
| 7 | 删除最后一条默认地址 | 允许，用户下次下单再补 |

## 交付物

### Part A — DDL 迁移（必须）
**新建**：`架构设计/ddl/96-migration-user-address.sql`

幂等：`CREATE TABLE IF NOT EXISTS jst_user_address (...)`
字段：
- `address_id BIGINT AUTO_INCREMENT PRIMARY KEY`
- `user_id BIGINT NOT NULL`
- `receiver_name VARCHAR(50) NOT NULL`
- `receiver_mobile VARCHAR(64) NOT NULL`（预留加密长度）
- `province VARCHAR(30) NOT NULL`
- `city VARCHAR(30) NOT NULL`
- `district VARCHAR(30) NOT NULL`
- `address_detail VARCHAR(200) NOT NULL`
- `postal_code VARCHAR(10)`
- `is_default TINYINT(1) NOT NULL DEFAULT 0`
- 审计列：`create_by / create_time / update_by / update_time / remark / del_flag`
- 索引：`KEY idx_user_id (user_id, del_flag)`

**执行**：任务结束前在测试库跑一次（凭据见 CLAUDE.md），确认建表成功。

### Part B — 后端模块（jst-user）
新增：
- `JstUserAddress.java` entity
- `dto/UserAddressSaveDTO.java`（@NotBlank 校验 + `@Pattern` 校验手机号）
- `vo/UserAddressVO.java`
- `mapper/JstUserAddressMapperExt.java` + xml
- `service/IJstUserAddressService.java` + Impl
- `controller/wx/WxUserAddressController.java`

接口：
- `GET /jst/wx/user/address/list` — 我的地址列表（默认地址排第一）
- `GET /jst/wx/user/address/{id}` — 详情
- `POST /jst/wx/user/address` — 新增
- `PUT /jst/wx/user/address/{id}` — 编辑
- `DELETE /jst/wx/user/address/{id}` — 删除
- `POST /jst/wx/user/address/{id}/default` — 设为默认（同时取消其他）

权限：`@PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:user:address:my')")` 仅登录用户可访问。

事务：设默认接口 `@Transactional(rollbackFor=Exception.class)`，锁 `lock:user:address:default:{userId}`。

### Part C — 前端 uniapp

**新建**：
- `jst-uniapp/api/address.js` — 封装 6 个接口
- `jst-uniapp/pages-sub/my/address-list.vue` — 列表页
  - 卡片：receiver / mobile（展示时脱敏）/ 完整地址 / 默认 badge
  - 顶部"新增地址"按钮
  - 单卡操作：编辑 / 删除 / 设为默认
  - 入口 query `mode=pick` 模式：点卡片 setStorage `ua_picked_address` + navigateBack（对齐 participant pick 模式）
- `jst-uniapp/pages-sub/my/address-edit.vue` — 新增/编辑
  - 姓名、手机号、省市区（uni.chooseLocation 或三级级联组件，本期用简单 picker 兜底）
  - 详细地址
  - 默认地址 switch
  - 保存 → 调 POST/PUT → navigateBack

**修改**：
- `jst-uniapp/pages.json` 注册 2 个新页面到 pages-sub/my
- `jst-uniapp/pages-sub/mall/detail.vue` 实物商品结算：改原生 `uni.chooseAddress` 为跳 `/pages-sub/my/address-list?mode=pick`，onShow 读 `ua_picked_address`
- `jst-uniapp/pages/my/index.vue` "我的服务" grid 追加"收货地址"tile → `address-list`（或并入 POLISH-BATCH2 的折叠组；若 POLISH-BATCH2 已合并则加到折叠组内；若未合并则先加到主 grid）

### Part D — 测试
- `test/wx-tests.http` 追加 F-ADDR 段：
  - list (空) / create / list (1 条) / create 第 2 条 / set default / get / update / delete
- `架构设计/ddl/99-test-fixtures.sql` 追加用户 9001 预置 1 条默认地址

### Part E — fixture 脚本幂等性
追加 DELETE 前置：`DELETE FROM jst_user_address WHERE create_by='fixture';`

## DoD
- [ ] 96-migration 在测试库执行成功
- [ ] mvn compile SUCCESS（全仓）
- [ ] .http 段全绿（手动或自动都行）
- [ ] 前端 2 页 + mall/detail.vue 对接完成
- [ ] 任务报告 `F-USER-ADDRESS-回答.md`
- [ ] commit: `feat(jst-user+wx): F-USER-ADDRESS 用户地址 CRUD`

## 不许做
- ❌ 不许改 23-基础数据.sql
- ❌ 不许直接 import jst-user entity 到别的模块（商城走 address 快照 JSON）
- ❌ 不许把地址 snapshot 的旧字段 `addressSnapshot` 改名（C8 已落码）
- ❌ 不许做省市区数据库，picker 硬编码一份简化数据即可（或用 uni.chooseLocation 原生）

## 依赖：无（与 POLISH-BATCH2 / DEBT-3 并行）
## 优先级：高（MVP 收尾）

---
派发时间：2026-04-09
