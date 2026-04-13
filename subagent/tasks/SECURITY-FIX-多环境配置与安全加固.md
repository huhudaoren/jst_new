# SECURITY-FIX — 多环境配置拆分 + 生产安全加固

> 优先级：P0 | 预估：M | Agent：Backend Agent

---

## 一、背景

生产上线审计发现 5 个 🔴 禁止上线问题 + 10 个 🟡 强烈建议修复问题，大部分是配置层面。当前项目只有一份 `application.yml`，开发/测试/生产共用同一配置，导致：
- 数据库凭证、JWT 密钥、AES 密钥硬编码在代码仓库中
- Swagger、Druid 监控在所有环境都开启
- 日志级别固定为 DEBUG
- 无法按环境切换行为

## 二、目标

1. 按 Spring Profiles 拆分为 dev / test / prod 三套配置
2. 修复全部 5 个 🔴 审计问题
3. 修复 10 个 🟡 审计问题中的配置相关项

## 三、配置文件拆分方案

### 3.1 文件结构（最终）

```
ruoyi-admin/src/main/resources/
├── application.yml              # 公共配置（不含敏感信息）
├── application-druid.yml        # 数据源公共配置（连接池参数等）
├── application-dev.yml          # 开发环境（本地开发用）
├── application-test.yml         # 测试环境
└── application-prod.yml         # 生产环境
```

### 3.2 `application.yml`（公共）

保留所有非敏感的公共配置，移除敏感值改为环境变量占位：

```yaml
# 原来硬编码的改为环境变量
token:
  secret: ${TOKEN_SECRET:please-set-in-env}
  expireTime: 30

# jst 业务配置（密钥全部环境变量注入）
jst:
  security:
    aes-key: ${JST_AES_KEY:please-set-in-env}
  oss:
    access-key-id: ${JST_OSS_AK:}
    access-key-secret: ${JST_OSS_SK:}
    # ... 其余保持
  wxauth:
    mock: ${JST_WXAUTH_MOCK:true}
    appid: ${JST_WX_APPID:}
    secret: ${JST_WX_SECRET:}
  wxpay:
    enabled: ${JST_WXPAY_ENABLED:false}
    mch-id: ${JST_WXPAY_MCHID:}
    # ... 其余保持
  sms:
    enabled: ${JST_SMS_ENABLED:false}
    access-key-id: ${JST_SMS_AK:}
    access-key-secret: ${JST_SMS_SK:}
    # ... 其余保持

# 日志默认 INFO（dev 覆盖为 DEBUG）
logging:
  level:
    com.ruoyi: info
    com.ruoyi.jst: info
```

### 3.3 `application-dev.yml`（开发环境）

```yaml
spring:
  profiles:
    include: druid

# 数据源 - 本地/测试服务器
spring.datasource:
  druid:
    master:
      url: jdbc:mysql://59.110.53.165:3306/jst_new?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
      username: jst_new
      password: J8zZpAa4zG8y6a7e

# Redis - 本地
spring.data.redis:
  host: localhost
  port: 6379
  password:

# Druid 监控 - 开发开启
spring.datasource.druid.stat-view-servlet:
  enabled: true
  login-username: admin
  login-password: admin123

# Swagger - 开发开启
springdoc:
  swagger-ui:
    enabled: true

# 日志 - 开发用 DEBUG
logging:
  level:
    com.ruoyi: debug
    com.ruoyi.jst: debug

# Token - 开发用弱密钥（仅本地，不影响安全）
token:
  secret: dev-only-secret-key-do-not-use-in-prod-12345678

# AES - 开发用
jst:
  security:
    aes-key: "bE8Ru+Z5tKOwHQG5c+gogb4tC3C/QbehGpKL1dot1TQ="
  wxauth:
    mock: true
```

### 3.4 `application-test.yml`（测试环境）

```yaml
spring:
  profiles:
    include: druid

# 数据源 - 测试服务器
spring.datasource:
  druid:
    master:
      url: ${DB_URL:jdbc:mysql://localhost:3306/jst_test}
      username: ${DB_USER:jst_test}
      password: ${DB_PASS:}

# Redis
spring.data.redis:
  host: ${REDIS_HOST:localhost}
  port: ${REDIS_PORT:6379}
  password: ${REDIS_PASSWORD:}

# Druid 监控 - 测试开启但限 IP
spring.datasource.druid.stat-view-servlet:
  enabled: true
  login-username: ${DRUID_USER:admin}
  login-password: ${DRUID_PASS:StrongPass123!}
  allow: 127.0.0.1,10.0.0.0/8

# Swagger - 测试开启
springdoc:
  swagger-ui:
    enabled: true

# 日志 - INFO
logging:
  level:
    com.ruoyi: info

# Token
token:
  secret: ${TOKEN_SECRET:test-secret-key-change-in-prod-12345678}

# jst
jst:
  security:
    aes-key: ${JST_AES_KEY:bE8Ru+Z5tKOwHQG5c+gogb4tC3C/QbehGpKL1dot1TQ=}
  wxauth:
    mock: ${JST_WXAUTH_MOCK:true}
```

### 3.5 `application-prod.yml`（生产环境）

```yaml
spring:
  profiles:
    include: druid

# 数据源 - 全部环境变量，无默认值（强制配置）
spring.datasource:
  druid:
    master:
      url: ${DB_URL}
      username: ${DB_USER}
      password: ${DB_PASS}

# Redis - 强制密码
spring.data.redis:
  host: ${REDIS_HOST}
  port: ${REDIS_PORT:6379}
  password: ${REDIS_PASSWORD}

# Druid 监控 - 生产禁用
spring.datasource.druid.stat-view-servlet:
  enabled: false

# Swagger - 生产禁用
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false

# 日志 - 生产 WARN（减少磁盘 IO）
logging:
  level:
    com.ruoyi: warn
    com.ruoyi.jst: info

# Token - 强制环境变量（无默认值，不配置则启动失败）
token:
  secret: ${TOKEN_SECRET}

# jst - 全部强制环境变量
jst:
  security:
    aes-key: ${JST_AES_KEY}
  wxauth:
    mock: false
    appid: ${JST_WX_APPID}
    secret: ${JST_WX_SECRET}
  wxpay:
    enabled: true
    mch-id: ${JST_WXPAY_MCHID}
    app-id: ${JST_WXPAY_APPID}
    api-v3-key: ${JST_WXPAY_APIV3}
    private-key-path: ${JST_WXPAY_PRIVKEY}
  sms:
    enabled: true
    access-key-id: ${JST_SMS_AK}
    access-key-secret: ${JST_SMS_SK}
  wxmsg:
    enabled: true
```

### 3.6 `application.yml` profile 切换

```yaml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
```

启动方式：
- 开发：默认 `dev`（本地直接运行）
- 测试：`java -jar app.jar --spring.profiles.active=test`
- 生产：`java -jar app.jar --spring.profiles.active=prod`

## 四、安全加固（🔴 审计修复）

### 4.1 CORS 白名单（🔴 #1）

修改 `ResourcesConfig.java`：

```java
// 旧：config.addAllowedOriginPattern("*");
// 新：
String allowedOrigins = env.getProperty("jst.cors.allowed-origins", "http://localhost:80,http://localhost:1024");
for (String origin : allowedOrigins.split(",")) {
    config.addAllowedOriginPattern(origin.trim());
}
```

在 `application.yml` 中添加：
```yaml
jst:
  cors:
    allowed-origins: ${JST_CORS_ORIGINS:http://localhost:80,http://localhost:1024}
```

prod.yml 中覆盖：
```yaml
jst:
  cors:
    allowed-origins: ${JST_CORS_ORIGINS:https://admin.jingsaitong.com,https://jingsaitong.com}
```

### 4.2 Swagger 条件化（🟡 #6）

修改 `SwaggerConfig.java`，添加注解：
```java
@ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", havingValue = "true", matchIfMissing = false)
```

### 4.3 代码生成器禁用（🔴 #5）

在 `application-prod.yml` 中禁用代码生成器路由：
```yaml
# 生产环境禁用代码生成器
ruoyi:
  generator:
    enabled: false
```

或更直接：在 `SecurityConfig.java` 中对 `/tool/gen/**` 在 prod 下拒绝访问。

### 4.4 定时任务白名单（🟡 #9）

修改 `JobInvokeUtil.java`，添加包名白名单：
```java
private static final Set<String> ALLOWED_PACKAGES = Set.of(
    "com.ruoyi.jst",
    "com.ruoyi.quartz.task"
);

// 在 invokeMethod() 开头添加
if (!isAllowedBean(beanName)) {
    throw new SecurityException("定时任务不允许调用此 Bean: " + beanName);
}
```

### 4.5 GlobalExceptionHandler SQL 异常处理（🟡 #13）

补充 SQLException 专项捕获：
```java
@ExceptionHandler(java.sql.SQLException.class)
public AjaxResult handleSqlException(SQLException e, HttpServletRequest request) {
    log.error("数据库操作异常: {}", request.getRequestURI(), e);
    return AjaxResult.error("数据处理异常，请稍后重试");
}
```

### 4.6 补齐 @Log 注解（🟡 #12）

为以下缺失 @Log 的写操作 Controller 方法补齐：
- `MallExchangeAdminController` 的 ship/complete
- 其他 jst-* Controller 中缺失的 POST/PUT/DELETE 方法

搜索 `@PostMapping\|@PutMapping\|@DeleteMapping` 中不含 `@Log` 的方法，逐个补齐。

## 五、配置清理

### 5.1 从 application.yml 移除的敏感值

| 配置项 | 原值 | 移到 |
|---|---|---|
| `token.secret` | `abcdefghijklmnopqrstuvwxyz` | dev.yml（弱密钥）/ prod.yml（环境变量） |
| `jst.security.aes-key` | `bE8Ru+Z5tKOw...` | dev.yml / prod.yml（环境变量） |
| `spring.datasource` 凭证 | 明文用户名密码 | dev.yml（本地）/ prod.yml（环境变量） |
| `druid.stat-view-servlet` 密码 | `123456` | dev.yml（admin123）/ prod.yml（禁用） |

### 5.2 application-druid.yml 改造

保留连接池通用参数（initialSize/minIdle/maxActive/maxWait 等），移除数据源 URL/用户名/密码到各环境 yml。

## 六、DoD

- [ ] 新建 application-dev.yml / application-test.yml / application-prod.yml
- [ ] application.yml 移除所有敏感硬编码值
- [ ] application-druid.yml 移除数据源凭证
- [ ] CORS 改为配置化白名单
- [ ] SwaggerConfig 加 @ConditionalOnProperty
- [ ] 代码生成器 prod 禁用
- [ ] 定时任务白名单
- [ ] GlobalExceptionHandler 补 SQLException 处理
- [ ] 缺失 @Log 的写操作补齐
- [ ] `mvn compile -DskipTests` 通过
- [ ] 启动后端（dev profile）验证功能正常
- [ ] 报告交付（含 prod 部署环境变量清单）
