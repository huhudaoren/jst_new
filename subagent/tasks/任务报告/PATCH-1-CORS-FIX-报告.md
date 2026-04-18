# PATCH-1 CORS 白名单兼容 dev server 自定义端口 — 报告

> 完成时间：2026-04-18
> 任务卡：`subagent/tasks/PATCH-1-CORS-FIX.md`
> 工作区：`D:/coding/jst_v1`

---

## 一、根因

POLISH-PICKER-APPLY §7.2/8.2 报告 dev server 在 81 端口下 POST/PUT 全部 `403 Invalid CORS request`。
原因：`application-dev.yml` 白名单只列 `http://localhost:80, :1024, :8080, :3000` 等字面量，没有 81/5173/4173 等其他常见端口。后端 `ResourcesConfig.corsFilter()` 用的是 `addAllowedOriginPattern`（**支持通配符**），改成 `:*` 即可一次性覆盖。

---

## 二、改动

| 文件 | 改动 |
|---|---|
| `RuoYi-Vue/ruoyi-admin/src/main/resources/application-dev.yml` | 白名单改 `http://localhost:*,http://127.0.0.1:*,http://localhost,http://127.0.0.1` |
| `RuoYi-Vue/ruoyi-admin/src/main/resources/application-test.yml` | 默认值改 `http://localhost:*,http://127.0.0.1:*` |
| `RuoYi-Vue/ruoyi-admin/src/main/resources/application.yml` | 默认值同上 + 注释提醒 prod 必须显式覆盖 |
| `RuoYi-Vue/ruoyi-admin/src/main/resources/application-prod.yml` | **未动**（红线，prod 走严格域名白名单） |

---

## 三、验证

### V1 编译
```bash
mvn -pl ruoyi-admin -am clean compile -DskipTests
```
结果：18 模块全部 `BUILD SUCCESS`，1m12s。

### V2.1 origin=`http://localhost:81`（subagent 实测端口）
```
HTTP/1.1 200
Access-Control-Allow-Origin: http://localhost:81
Access-Control-Allow-Methods: POST
Access-Control-Allow-Headers: content-type
Access-Control-Max-Age: 1800
```
✅ 通过。

### V2.2 origin=`http://localhost:5173`（Vite 默认端口）
```
HTTP/1.1 200
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: POST
Access-Control-Allow-Headers: content-type
```
✅ 通过。

### V3 反例 origin=`https://evil.example.com`
```
HTTP/1.1 403
Invalid CORS request
```
✅ 通过——未通配的外网 origin 被正确拒绝，证明 `localhost:*` 通配只覆盖 localhost 不会泄漏。

---

## 四、DoD 对照

- [x] 4 个 yml 按方案修改（prod 严禁动）
- [x] mvn -pl ruoyi-admin -am compile ✅
- [x] V2 两个端口（81, 5173）OPTIONS 都返 200 + 正确 Allow-Origin
- [x] V3 外网 origin 被拒
- [x] 报告附 3 段 curl 输出

---

## 五、隐患 / TODO

无。修复方式简单且对应于 Spring 框架已有能力（`addAllowedOriginPattern` 通配），不引入新风险。

**生产部署提醒**：prod 仍然走 `${JST_CORS_ORIGINS}` 环境变量显式白名单，部署时必须设置该变量为生产域名清单（`https://admin.jingsaitong.com,https://jingsaitong.com`），否则会 fallback 到 `localhost:*`（当前默认值已加注释提醒）。

---

## 六、commit

待主 Agent commit。message 模板：
```
fix(framework): PATCH-1 CORS 白名单 dev/test 改用 :* 通配兼容任意端口
```
