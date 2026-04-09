# 任务卡 D2-1a - uqrcode payload 配置化（后端）

## 阶段 / 模块
阶段 D 批 2 / **jst-event**（核销链路）

## 背景
当前核销二维码 payload 由后端 `WriteoffService` 生成 HMAC 签名字符串，前端 canvas 画成 QR。上线前需要让 payload 可以通过配置切换"纯签名字符串"与"带 base URL 的完整链接"两种形态，方便运营扫码跳 H5 时可用，测试/生产可切。

本卡**仅做配置化**，不改 HMAC 规则，不改扫码核销接口。

## CCB 决策

| # | 决策 | 选择 |
|---|---|---|
| 1 | 配置项名 | `jst.qrcode.writeoff.base-url` |
| 2 | 默认值 | 空字符串（保持当前纯签名字符串形态，向后兼容） |
| 3 | 拼接规则 | 非空时 → `{baseUrl}?t={签名字符串}`；空时 → 仍返回原签名字符串 |
| 4 | 扫码核销接口 | **不改**。接收到 payload 时先判断是否 http(s):// 开头，是则提取 `t` 参数再校验 HMAC；不是则按原逻辑直接校验 |
| 5 | 影响面 | 仅 `jst_appointment_writeoff` / `jst_team_writeoff` 关联的 `qrCode` 字段生成 |

## 交付物

### 1. 配置项
**修改**：`RuoYi-Vue/ruoyi-admin/src/main/resources/application.yml`

```yaml
jst:
  qrcode:
    writeoff:
      base-url: ""   # 测试环境可填 http://127.0.0.1:8080/wx/writeoff；生产填正式域名
```

### 2. 后端生成侧
**修改**：`jst-event` 中生成 QR payload 的方法（grep `qrCode` 或 `HmacUtils` 定位，大概率在 `WriteoffServiceImpl` / `AppointmentServiceImpl`）

- 新增 `@Value("${jst.qrcode.writeoff.base-url:}")` 注入
- 生成签名后：
  ```java
  String signed = hmacSign(...);
  String qrCode = StringUtils.isNotBlank(baseUrl)
      ? baseUrl + (baseUrl.contains("?") ? "&" : "?") + "t=" + signed
      : signed;
  ```

### 3. 后端扫码接收侧
**修改**：`WxWriteoffController.scan` 或其 service

- 接收 payload 后先兜底提取：
  ```java
  String token = payload;
  if (payload.startsWith("http://") || payload.startsWith("https://")) {
      // 从 query 提取 t 参数
      int idx = payload.indexOf("?t=");
      if (idx < 0) idx = payload.indexOf("&t=");
      if (idx >= 0) {
          token = payload.substring(idx + 3).split("&")[0];
      }
  }
  // 后续走原 HMAC 校验 token
  ```

### 4. 测试补充
**修改**：`test/wx-tests.http`

在现有 writeoff scan 测试块追加一组 "URL 形态 payload" 用例：
- `POST /jst/wx/event/writeoff/scan` body `{"qrCode": "http://127.0.0.1:8080/wx/writeoff?t={{签名}}"}`
- 验证 200 OK，与原纯签名形态等效

## DoD
- [ ] application.yml 新增配置项（默认空）
- [ ] 生成侧根据配置拼 URL 或返回纯签名
- [ ] 扫码侧兼容两种 payload 形态
- [ ] mvn compile SUCCESS
- [ ] wx-tests.http 追加 URL 形态测试
- [ ] 任务报告 `D2-1a-UQRCODE-BE-回答.md`
- [ ] commit: `feat(jst-event): D2-1a 核销二维码 payload 配置化`

## 不许做
- ❌ 不许改 HMAC 签名规则
- ❌ 不许改 `jst_appointment_writeoff` / `jst_team_writeoff` 表结构
- ❌ 不许改前端任何文件（前端是 D2-1b 的范围）
- ❌ 不许把 base-url 硬编码成 127.0.0.1

## 依赖：无
## 优先级：中（配合 D2-1b 一起上线）

---
派发时间：2026-04-09
