# F-USER-ADDRESS 用户地址 CRUD 任务报告

## A. 任务完成情况

已完成用户地址 CRUD 全链路实现，覆盖后端接口、服务层、数据表迁移、UniApp 页面与商城收货地址接入，并补充了测试夹具与 `.http` 验收块。

## B. 本次交付

### 1. 后端新增

- [96-migration-user-address.sql](D:/coding/jst_v1/架构设计/ddl/96-migration-user-address.sql)
- [JstUserAddress.java](D:/coding/jst_v1/RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/domain/JstUserAddress.java)
- [UserAddressSaveDTO.java](D:/coding/jst_v1/RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/dto/UserAddressSaveDTO.java)
- [UserAddressVO.java](D:/coding/jst_v1/RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/vo/UserAddressVO.java)
- [JstUserAddressMapperExt.java](D:/coding/jst_v1/RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/mapper/JstUserAddressMapperExt.java)
- [JstUserAddressMapperExt.xml](D:/coding/jst_v1/RuoYi-Vue/jst-user/src/main/resources/mapper/user/JstUserAddressMapperExt.xml)
- [IJstUserAddressService.java](D:/coding/jst_v1/RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/IJstUserAddressService.java)
- [JstUserAddressServiceImpl.java](D:/coding/jst_v1/RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/JstUserAddressServiceImpl.java)
- [WxUserAddressController.java](D:/coding/jst_v1/RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/controller/wx/WxUserAddressController.java)

### 2. 前端新增

- [address.js](D:/coding/jst_v1/jst-uniapp/api/address.js)
- [address-list.vue](D:/coding/jst_v1/jst-uniapp/pages-sub/my/address-list.vue)
- [address-edit.vue](D:/coding/jst_v1/jst-uniapp/pages-sub/my/address-edit.vue)

### 3. 修改文件

- [BizErrorCode.java](D:/coding/jst_v1/RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/exception/BizErrorCode.java)
- [pages.json](D:/coding/jst_v1/jst-uniapp/pages.json)
- [detail.vue](D:/coding/jst_v1/jst-uniapp/pages-sub/mall/detail.vue)
- [index.vue](D:/coding/jst_v1/jst-uniapp/pages/my/index.vue)
- [99-test-fixtures.sql](D:/coding/jst_v1/架构设计/ddl/99-test-fixtures.sql)
- [wx-tests.http](D:/coding/jst_v1/test/wx-tests.http)

## C. 关键实现说明

### 1. 后端能力

- 提供小程序侧地址列表、详情、新增、修改、删除、设为默认接口。
- 控制器权限按任务卡要求使用 `@PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:user:address:my')")`。
- 默认地址变更使用锁 `lock:user:address:default:{userId}`，并在服务层事务内处理唯一默认地址。
- 新增错误码 `JST_USER_ADDRESS_NOT_FOUND`。

### 2. 数据安全

- 手机号在写入时通过 `JstCipher` 加密。
- 读取时优先解密，若历史夹具数据为明文则兼容回显，避免已有测试数据失效。

### 3. UniApp 接入

- 新增“收货地址”列表页与编辑页。
- “我的”页面新增收货地址入口。
- 商城商品详情页改为跳转地址列表选择地址，并回填下单快照。

## D. 测试与验证

### 1. 编译验证

已执行：

```bash
mvn compile
```

结果：

- `BUILD SUCCESS`
- 总耗时约 `40.403 s`

### 2. 数据库验证

已将 [96-migration-user-address.sql](D:/coding/jst_v1/架构设计/ddl/96-migration-user-address.sql) 执行到测试库，并确认：

- `jst_user_address` 表创建成功
- 索引 `idx_user_id` 存在
- 夹具地址数据已插入

### 3. HTTP 验收块

已在 [wx-tests.http](D:/coding/jst_v1/test/wx-tests.http) 追加 `F-ADDR-0 ~ F-ADDR-9.1`，覆盖：

- 登录
- 空列表
- 首次新增默认地址
- 再次新增非默认地址
- 设为默认
- 详情
- 修改
- 删除
- 夹具账号查看默认地址

## E. 当前阻塞

本轮未能完成真实接口联调，原因不是本任务代码本身，而是项目在安装最新模块后启动时命中既有 Bean 冲突：

- `contestLookupMapper` 在 `jst-marketing` 与 `jst-channel` 中存在同名 Mapper 定义
- Spring 启动报错 `ConflictingBeanDefinitionException`

因此：

- `.http` 验收块已补齐
- 数据表迁移已验证
- 代码编译已通过
- 真实 HTTP 请求联调被现有启动问题阻塞，待该公共冲突修复后可继续回归

## F. 额外说明

- [99-test-fixtures.sql](D:/coding/jst_v1/架构设计/ddl/99-test-fixtures.sql) 与 [wx-tests.http](D:/coding/jst_v1/test/wx-tests.http) 原文件编码不适合直接用补丁工具安全修改，本次采用编码兼容脚本方式追加内容，最终文件已落盘。

