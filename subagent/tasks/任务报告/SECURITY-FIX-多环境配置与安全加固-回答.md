# 任务报告 - SECURITY-FIX 多环境配置拆分 + 生产安全加固

> 完成时间：2026-04-13 | Agent：Backend Agent | 优先级：P0

## A. 任务前自检（Step 2 答题）

1. **涉及表**: 无（配置层+框架安全层修改，不涉及业务表）
2. **涉及状态机**: 无
3. **涉及权限标识**: 无新增（`/tool/gen/**` 在 prod 下 denyAll 是路径级，非权限标识）
4. **涉及锁名**: 无
5. **事务边界**: 无
6. **ResVO 字段**: 无
7. **复用样板**: 无（本任务非业务 CRUD）

## B. 交付物清单

### 新增文件 (3)

| 文件 | 说明 |
|---|---|
| `ruoyi-admin/src/main/resources/application-dev.yml` | 开发环境配置（明文凭证，DEBUG 日志，Swagger/Druid 开启） |
| `ruoyi-admin/src/main/resources/application-test.yml` | 测试环境配置（环境变量+默认值，Druid 限 IP） |
| `ruoyi-admin/src/main/resources/application-prod.yml` | 生产环境配置（全环境变量无默认值，Swagger/Druid/Gen 禁用） |

### 修改文件 (17)

| 文件 | 修改内容 |
|---|---|
| `application.yml` | 移除 token.secret / aes-key / redis host+password 硬编码；profile 改 `${SPRING_PROFILES_ACTIVE:dev}`；添加 `spring.profiles.group`；添加 `jst.cors.allowed-origins`；swagger 默认 false；日志默认 INFO |
| `application-druid.yml` | 移除 master url/username/password 和 statViewServlet login 凭证，保留连接池通用参数 |
| `SwaggerConfig.java` | 添加 `@ConditionalOnProperty(name="springdoc.swagger-ui.enabled", havingValue="true")` |
| `ResourcesConfig.java` | CORS 改为配置化白名单 `@Value("${jst.cors.allowed-origins:*}")` |
| `SecurityConfig.java` | 添加 `@Value("${ruoyi.generator.enabled:true}")`，prod 下 `denyAll("/tool/gen/**")` |
| `GlobalExceptionHandler.java` | 新增 `SQLException` 处理器（返回通用"数据处理异常"）；`RuntimeException`/`Exception` 改为不泄露 `e.getMessage()` |
| `JobInvokeUtil.java` | 添加 `ALLOWED_PACKAGES` 白名单（`com.ruoyi.jst` / `com.ruoyi.quartz.task`），非白名单 Bean 抛 SecurityException |
| `MallExchangeAdminController.java` | ship/complete 补 @Log (2 方法) |
| `MallAftersaleAdminController.java` | approve/reject 补 @Log (2 方法) |
| `RefundAdminController.java` | approve/reject/execute/specialRefund 补 @Log (4 方法) |
| `PartnerApplyAdminController.java` | approve/reject/supplement 补 @Log (3 方法) |
| `ChannelAuthApplyAdminController.java` | approve/reject/suspend 补 @Log (3 方法) |
| `PartnerContestController.java` | create/update/submit/offline/delete 补 @Log (5 方法) |
| `PartnerEnrollController.java` | audit/batchAudit 补 @Log (2 方法) |
| `PartnerScoreController.java` | import/save/submitReview/correction 补 @Log (4 方法) |
| `PartnerCertController.java` | template(JSON)/template(multipart)/batchGrant/submitReview 补 @Log (4 方法) |
| `PartnerSettlementController.java` | confirm/dispute 补 @Log (2 方法) |

## C. mvn compile 结果

```
[INFO] Reactor Summary for ruoyi 3.9.2:
[INFO] ruoyi .............................................. SUCCESS
[INFO] ruoyi-common ....................................... SUCCESS
[INFO] ruoyi-system ....................................... SUCCESS
[INFO] ruoyi-framework .................................... SUCCESS
[INFO] ruoyi-quartz ....................................... SUCCESS
[INFO] ruoyi-generator .................................... SUCCESS
[INFO] jst-common ......................................... SUCCESS
[INFO] jst-user ........................................... SUCCESS
[INFO] jst-order .......................................... SUCCESS
[INFO] jst-event .......................................... SUCCESS
[INFO] jst-channel ........................................ SUCCESS
[INFO] jst-points ......................................... SUCCESS
[INFO] jst-organizer ...................................... SUCCESS
[INFO] jst-marketing ...................................... SUCCESS
[INFO] jst-message ........................................ SUCCESS
[INFO] jst-risk ........................................... SUCCESS
[INFO] jst-finance ........................................ SUCCESS
[INFO] ruoyi-admin ........................................ SUCCESS
[INFO] BUILD SUCCESS (18/18 模块)
[INFO] Total time: 50.305 s
```

## D. 启动验证结果（dev profile）

```
启动日志:
  The following 2 profiles are active: "dev", "druid"
  DruidDataSource {dataSource-1} inited
  Initializing ProtocolHandler ["http-nio-8080"]
  Port 8080 Listen (PID 56552)

接口验证:
  GET  /captchaImage → {"code":200,"captchaEnabled":false} ✓
  POST /login        → {"code":200,"token":"eyJ..."} ✓
```

## E. 遗留 TODO

- 首次启动时遇到 Spring Boot 4.x 不允许在 profile-specific 文件中使用 `spring.profiles.include`，已改为 `spring.profiles.group` 方案解决
- 无其他阻塞项

## F. 我做了任务卡之外的什么

1. **GlobalExceptionHandler 增强**：`RuntimeException` 和 `Exception` 的处理也改为不泄露 `e.getMessage()` 给前端（原来直接返回异常消息可能包含 SQL/堆栈信息），改为通用"系统内部错误"提示
2. **@Log 补齐范围扩大**：除任务卡明确提到的 `MallExchangeAdminController`，还为所有手写 Controller 的写操作补齐了 @Log（共 31 个方法，覆盖 9 个 Controller）

## G. 自检清单确认

- [x] 所有敏感值已从 application.yml 移除
- [x] prod.yml 中敏感值无默认值（不配置则启动失败）
- [x] dev.yml 保留弱密钥方便开发
- [x] CORS 改为配置化白名单
- [x] SwaggerConfig 加 @ConditionalOnProperty
- [x] 代码生成器 prod 禁用 (denyAll)
- [x] 定时任务白名单 (com.ruoyi.jst / com.ruoyi.quartz.task)
- [x] GlobalExceptionHandler 补 SQLException + 通用异常不泄露堆栈
- [x] 缺失 @Log 的写操作已补齐 (31 方法)
- [x] mvn compile 通过 (18/18)
- [x] dev profile 启动验证通过 (login 200)

## H. 审计问题修复映射

| 审计编号 | 问题 | 修复方式 | 状态 |
|---|---|---|---|
| 🔴 #1 | CORS `*` 通配 | `jst.cors.allowed-origins` 配置化白名单 | ✅ |
| 🔴 #2 | Token secret 弱密钥硬编码 | 移至各环境 yml，prod 无默认值 | ✅ |
| 🔴 #3 | AES key 硬编码 | 移至各环境 yml，prod 无默认值 | ✅ |
| 🔴 #4 | 数据库凭证硬编码 | 移至各环境 yml，prod 全环境变量 | ✅ |
| 🔴 #5 | 代码生成器生产可访问 | `ruoyi.generator.enabled=false` + SecurityConfig denyAll | ✅ |
| 🟡 #6 | Swagger 生产开启 | `@ConditionalOnProperty` + prod.yml `enabled: false` | ✅ |
| 🟡 #7 | Druid 监控弱密码 | dev 改 admin123，test 环境变量，prod 禁用 | ✅ |
| 🟡 #8 | 日志级别 DEBUG | 公共 INFO，dev 覆盖 DEBUG，prod WARN | ✅ |
| 🟡 #9 | 定时任务无白名单 | `JobInvokeUtil.ALLOWED_PACKAGES` 校验 | ✅ |
| 🟡 #12 | 写操作缺 @Log | 31 个方法补齐 | ✅ |
| 🟡 #13 | SQLException 信息泄露 | 专项 handler + RuntimeException/Exception 不返回原始消息 | ✅ |

## I. 生产环境变量清单

### 必须配置（无默认值，不配置则启动失败）

| 环境变量 | 说明 | 生成方式 |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | 激活 profile | 设为 `prod` |
| `DB_URL` | MySQL 连接 URL | `jdbc:mysql://host:3306/jst_prod?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8` |
| `DB_USER` | 数据库用户名 | DBA 分配 |
| `DB_PASS` | 数据库密码 | DBA 分配（强密码） |
| `REDIS_HOST` | Redis 地址 | 运维分配 |
| `REDIS_PASSWORD` | Redis 密码 | 运维分配（强密码） |
| `TOKEN_SECRET` | JWT 签名密钥 | `openssl rand -hex 32`（≥32 字符） |
| `JST_AES_KEY` | AES-256 字段加密密钥 | `openssl rand -base64 32` |
| `JST_WX_APPID` | 微信小程序 AppID | 微信后台获取 |
| `JST_WX_SECRET` | 微信小程序 Secret | 微信后台获取 |
| `JST_WXPAY_MCHID` | 微信支付商户号 | 微信支付后台 |
| `JST_WXPAY_APPID` | 微信支付 AppID | 微信支付后台 |
| `JST_WXPAY_APIV3` | 微信支付 APIv3 密钥 | 微信支付后台 |
| `JST_WXPAY_PRIVKEY` | 微信支付私钥文件路径 | 运维部署 |
| `JST_SMS_AK` | 短信服务 AccessKey | 阿里云控制台 |
| `JST_SMS_SK` | 短信服务 SecretKey | 阿里云控制台 |

### 可选配置（有合理默认值）

| 环境变量 | 默认值 | 说明 |
|---|---|---|
| `REDIS_PORT` | `6379` | Redis 端口 |
| `JST_CORS_ORIGINS` | `https://admin.jingsaitong.com,https://jingsaitong.com` | CORS 白名单 |

### 启动命令

```bash
java -jar ruoyi-admin.jar --spring.profiles.active=prod
```

或通过环境变量：

```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar ruoyi-admin.jar
```
