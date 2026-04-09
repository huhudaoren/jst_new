# D2-2 F-ADDR E2E 联调报告

## 1. 任务结论

- 已完成 `F-USER-ADDRESS` 真实 HTTP 联调回归。
- 已完成冷启动复验，未再出现 `contestLookupMapper` 的 `ConflictingBeanDefinitionException`。
- `F-ADDR-0 ~ F-ADDR-9.1` 主链路全部通过。
- 重点补充校验全部通过：
  - 默认地址切换会清除其他地址的 `is_default`
  - 删除走软删除 `del_flag='2'`
  - 新增/更新手机号入库为密文
  - 历史明文 fixture 可正常读取
  - 非本人地址 `get/update/delete` 均返回 `10031`
  - 删除不存在地址返回 `10031`

## 2. 启动复验

实际执行：

```bash
cd D:/coding/jst_v1/RuoYi-Vue/ruoyi-admin
mvn spring-boot:run -DskipTests
```

结果：

- 新进程 PID：`30852`
- 端口 `8080` 已重新监听
- 日志出现：
  - `Starting RuoYiApplication`
  - `Starting ProtocolHandler ["http-nio-8080"]`
  - `Started RuoYiApplication in 181.815 seconds`
- 本轮冷启动日志中未出现 `ConflictingBeanDefinitionException`

## 3. F-ADDR 用例结果

### 主链路

| 用例 | 结果 | 关键点 |
|---|---|---|
| F-ADDR-0 | 通过 | `MOCK_1001` 登录成功，`userId=1001` |
| F-ADDR-1 | 通过 | 地址列表为空，`count=0` |
| F-ADDR-2 | 通过 | 首次新增默认地址成功，`addressId=99310`，`isDefault=1` |
| F-ADDR-3 | 通过 | 列表 1 条，`count=1` |
| F-ADDR-4 | 通过 | 再新增 1 条非默认地址成功，`addressId=99311`，`isDefault=0` |
| F-ADDR-5 | 通过 | 设为默认成功，`addressId=99311`，`isDefault=1` |
| F-ADDR-6 | 通过 | 地址详情返回成功，`addressId=99311`，`isDefault=1`，脱敏手机号正确 |
| F-ADDR-7 | 通过 | 地址修改成功，`addressId=99311`，`isDefault=1`，脱敏手机号正确 |
| F-ADDR-8 | 通过 | 删除成功 |
| F-ADDR-9 | 通过 | 删除后列表仅剩 1 条，`remainingId=99310` |
| F-ADDR-9.1 | 通过 | `MOCK_9001` 命中 `userId=9001`，fixture 默认地址存在，`count=1`，`isDefault=1` |

### 补充校验

| 用例 | HTTP | 业务码 | 结果 | 说明 |
|---|---:|---:|---|---|
| AUTH-GET-OTHER | 200 | 10031 | 通过 | `1002` 查看 `1001` 地址被拒绝 |
| AUTH-PUT-OTHER | 200 | 10031 | 通过 | `1002` 修改 `1001` 地址被拒绝 |
| AUTH-DEL-OTHER | 200 | 10031 | 通过 | `1002` 删除 `1001` 地址被拒绝 |
| DEL-NOT-FOUND | 200 | 10031 | 通过 | 删除不存在地址返回“收货地址不存在” |
| PLAINTEXT-FIXTURE | 200 | 200 | 通过 | 9001 明文 fixture 手机号可正常回显并脱敏 |
| ENCRYPTED-READBACK | 200 | 200 | 通过 | 1001 新增地址手机号可正常解密回显并脱敏 |

说明：

- 该接口族错误场景走 `AjaxResult`，HTTP 仍为 `200`，业务失败通过 `code=10031` 识别。

## 4. 数据库验证

联调结束后数据库实际状态：

```json
[
  [99301, 9001, "13800009001", 1, "0", "fixture"],
  [99310, 1001, "LeKOSFU/t9EI9RfxTDwIJ65Ky0urs5ka93nU/3YMAO4=", 0, "0", "1001"],
  [99311, 1001, "v6xZLIBRULHktZJ1v90kjZGedEoV9AguEyveX+UaDv4=", 1, "2", "1001"]
]
```

校验结论：

- `99310`：
  - `user_id=1001`
  - `receiver_mobile` 为密文
  - `is_default=0`
  - `del_flag='0'`
- `99311`：
  - `user_id=1001`
  - `receiver_mobile` 为密文
  - `del_flag='2'`
  - 说明删除为软删除
- `99301`：
  - `user_id=9001`
  - `receiver_mobile` 为明文 fixture
  - 列表接口仍可正常返回明文 + 脱敏手机号

由此可确认：

- 默认地址切换后，旧地址已被清成 `is_default=0`
- 新地址删除后未物理删库
- 新数据密文存储、旧明文数据兼容读取均符合预期

## 5. 联调中发现的环境问题

### 5.1 测试库 fixture 漂移

本轮联调前，测试库中 `MOCK_9001` 首次登录会落到自动创建用户 `9213`，而不是夹具用户 `9001`。根因是当前测试库数据状态与 [99-test-fixtures.sql](D:/coding/jst_v1/架构设计/ddl/99-test-fixtures.sql) 不一致，不是地址功能代码问题。

本轮已在测试库中手动恢复：

- `openid_test_9001 -> user_id=9001`
- `9001` 的默认地址 fixture

因此本轮 `F-ADDR-9.1` 已在正确前置下回归通过。

### 5.2 `wx-tests.http` 编号与任务卡存在轻微偏差

当前 [wx-tests.http](D:/coding/jst_v1/test/wx-tests.http) 的 `F-ADDR` 块里，`F-ADDR-9` 实际是“fixture 登录”，未单独落“删除后列表”断言。本轮联调按任务卡要求额外补跑了“删除后列表”校验，并记录为 `F-ADDR-9`。

本卡未修改业务代码；本次交付以联调结论和报告为主。

