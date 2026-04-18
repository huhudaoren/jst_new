# PATCH-1 — CORS 白名单兼容 dev server 自定义端口

> 优先级：**🔴 P0** | 预估：**XS（30-60min）** | Agent：**Backend Agent**
> 派发时间：2026-04-18 | 版本：任务卡 v1
> 起因：POLISH-PICKER-APPLY §7.2/8.2 报告 dev server 在 81 端口下 POST/PUT 全部 `403 Invalid CORS request`

---

## 一、问题根因

`RuoYi-Vue/ruoyi-admin/src/main/resources/application-dev.yml` 当前白名单：

```yaml
jst:
  cors:
    allowed-origins: "http://localhost,http://localhost:80,http://localhost:1024,http://localhost:8080,http://localhost:3000,http://127.0.0.1,http://127.0.0.1:80,http://127.0.0.1:1024,http://127.0.0.1:8080,http://127.0.0.1:3000"
```

**漏掉端口**：81（subagent 实测端口）、5173（Vite）、8081/8082、4173 等常见 dev 端口。
**漏掉端口段**：用户可能用任意 port（vue.config.js `port = process.env.port || 80`）。

后端 `ResourcesConfig.java:60` 用的是 `addAllowedOriginPattern`（**支持通配符**），但配置里写的是字面量。

---

## 二、必读上下文

1. `RuoYi-Vue/ruoyi-framework/src/main/java/com/ruoyi/framework/config/ResourcesConfig.java`（CORS bean，**不动**）
2. `RuoYi-Vue/ruoyi-admin/src/main/resources/application-dev.yml`（dev 白名单，**主战场**）
3. `RuoYi-Vue/ruoyi-admin/src/main/resources/application-prod.yml`（prod 白名单，**确认是否同样问题**）
4. `RuoYi-Vue/ruoyi-admin/src/main/resources/application-test.yml`
5. `RuoYi-Vue/ruoyi-admin/src/main/resources/application.yml`（默认值）
6. `RuoYi-Vue/ruoyi-ui/vue.config.js`（看默认 port）

---

## 三、修复方案

### Step 1：dev 白名单改用通配模式

**文件**：`RuoYi-Vue/ruoyi-admin/src/main/resources/application-dev.yml`

**改为**：

```yaml
jst:
  cors:
    # 开发环境 CORS 白名单：用 Spring CorsConfiguration#addAllowedOriginPattern 通配
    # 任意端口的 localhost / 127.0.0.1 都允许（dev 专用，prod 仍走严格白名单）
    allowed-origins: "http://localhost:*,http://127.0.0.1:*,http://localhost,http://127.0.0.1"
```

**为什么 OK**：
- `addAllowedOriginPattern` 支持 `*` 通配符（`ResourcesConfig.java:66`）
- dev 环境只对 localhost/127.0.0.1 通配 → 不会泄漏到外网
- 保留无端口形式（浏览器在默认 80 时省略端口）

### Step 2：test 白名单同步加 81 / 通配

**文件**：`RuoYi-Vue/ruoyi-admin/src/main/resources/application-test.yml`

把：
```yaml
allowed-origins: ${JST_CORS_ORIGINS:http://localhost:80,http://localhost:1024}
```
改为：
```yaml
allowed-origins: ${JST_CORS_ORIGINS:http://localhost:*,http://127.0.0.1:*}
```

### Step 3：prod **不动**

**重要**：prod 白名单 `${JST_CORS_ORIGINS:https://admin.jingsaitong.com,https://jingsaitong.com}` 是生产域名，**保持原样**。生产严禁通配。

### Step 4：默认 application.yml 兜底

**文件**：`RuoYi-Vue/ruoyi-admin/src/main/resources/application.yml`

把：
```yaml
allowed-origins: ${JST_CORS_ORIGINS:http://localhost:80,http://localhost:1024}
```
改为：
```yaml
# 默认值仅 dev/test 用；生产必须通过 JST_CORS_ORIGINS 环境变量显式覆盖
allowed-origins: ${JST_CORS_ORIGINS:http://localhost:*,http://127.0.0.1:*}
```

---

## 四、验证（**必须真实复现 + 验证修复**）

### Step V1：编译

```bash
cd D:/coding/jst_v1/RuoYi-Vue && mvn -pl ruoyi-admin -am clean compile -DskipTests
```

期望：`BUILD SUCCESS`。

### Step V2：启动后端 + 真实 curl 验证

启动 ruoyi-admin（端口 8080）。然后**模拟 81 端口前端发起跨域 POST**：

```bash
curl -i -X OPTIONS http://localhost:8080/login \
  -H "Origin: http://localhost:81" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: content-type"
```

**修复前**期望：响应头无 `Access-Control-Allow-Origin` 或 `Invalid CORS request`。
**修复后**期望：
- 状态码 200
- 响应头有 `Access-Control-Allow-Origin: http://localhost:81`

再测一个 5173（Vite 端口）：
```bash
curl -i -X OPTIONS http://localhost:8080/login \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: content-type"
```

期望同样通过。

### Step V3：反例验证（不许任意外网 origin 通过）

```bash
curl -i -X OPTIONS http://localhost:8080/login \
  -H "Origin: https://evil.example.com" \
  -H "Access-Control-Request-Method: POST"
```

**期望**：响应头**没有** `Access-Control-Allow-Origin: https://evil.example.com`（CorsFilter 不会回 allow）。

把 3 段 curl 输出贴进报告。

---

## 五、DoD

- [ ] 4 个 yml 文件按方案修改（prod 严禁动）
- [ ] `mvn -pl ruoyi-admin -am compile` ✅
- [ ] V2 两个端口（81, 5173）OPTIONS 都返 200 + 正确 Allow-Origin
- [ ] V3 外网 origin 被拒
- [ ] 报告附 3 段 curl 输出
- [ ] commit：`fix(framework): PATCH-1 CORS 白名单 dev/test 改用 :* 通配兼容任意端口`

---

## 六、红线

- ❌ **不许改 prod 白名单**
- ❌ **不许改 ResourcesConfig.java**（bean 实现已正确，问题在配置）
- ❌ **不许加 setAllowCredentials(true)**——若依走 JWT header 不需要 cookie，开了反而把 `*` 通配废掉
- ❌ 不许扩 dev 通配到 0.0.0.0:* 或 *.* （安全风险）
- ❌ 不许只测一个端口就交付，必须 V2+V3 都跑

派发时间：2026-04-18 | 主 Agent：竞赛通架构师
