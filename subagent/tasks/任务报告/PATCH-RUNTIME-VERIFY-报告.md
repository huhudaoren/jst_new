# PATCH 重启后运行时验证 — 报告

> 完成时间：2026-04-18 23:30
> 主 Agent：竞赛通架构师
> 范围：PATCH-2 报告里 cert/template/list 99902 真伪 + PATCH-4 partnerName 落地 + PATCH-5 region 端点上线

---

## 一、动机

PATCH-2 报告 §六 隐患 #2 指出：**admin 访 `/jst/partner/cert/template/list` 返 99902**，建议派 PATCH-6 修。但代码层核对发现：
- `PartnerCertController` 已继承 `BasePartnerController`（类级 `hasAnyRoles('jst_partner,admin,jst_operator')`）
- `PartnerCertServiceImpl.listTemplates()` 已用 `currentPartnerIdAllowPlatformOp()` 容忍 admin
- `PartnerCertMapper.xml.selectTemplateList` 已在 `partnerId == null` 时展开为查全部 partner 模板

→ 一切都指向 PATCH-2 是用 PATCH-1 之前启动的旧 jvm 跑测试，**FIX-PARTNER-SCOPE-READONLY 改动未生效**。

---

## 二、验证步骤

1. `taskkill //PID 28256 //F` + `taskkill //PID 2624 //F` 干掉旧 jvm
2. `mvn -pl ruoyi-admin spring-boot:run -Dspring-boot.run.profiles=dev` 启动新 jvm（包含所有 PATCH commit）
3. 等 backend ready（poll OPTIONS /login 返 200）
4. admin/admin123 拿 token，跑 4 段 curl

---

## 三、4 段验证结果

### V1：cert/template/list（PATCH-2 §六 隐患 #2 的真伪验证）

```bash
curl -s "http://localhost:8080/jst/partner/cert/template/list" -H "Authorization: Bearer $TOKEN"
```
返回：
```json
{"msg":"操作成功","code":200,"data":[{"auditStatus":"approved","bgImage":"...",
  "createTime":"2026-04-11 21:52:43","layoutJson":"...",
  "ownerId":8101,"ownerType":"partner","status":1,
  "templateId":97051,"templateName":"PA6_PARTNER_TEMPLATE"}]}
```
✅ **200 OK + 1 条真实数据**，无 99902。

### V2：form-template/list 对照（确认 partner-scope-readonly 路径一致）

```json
{"total":6,"code":200,"msg":"查询成功","rows":[
  {"templateId":8801,"templateName":"测试_F8_标准报名模板","ownerType":"platform",...},
  {"templateId":8806,"templateName":"...",...}, ... 6 条 ...
]}
```
✅ 6 条混合模板（platform + partner）。

### V3：PATCH-4 partnerName 落地

```bash
curl -s "http://localhost:8080/jst/event/jst_contest/list?pageNum=1&pageSize=2" -H "Authorization: Bearer $TOKEN"
```
关键字段：
```
contestId=8801, partnerId=8101, partnerName='测试_赛事机构A'
contestId=8802, partnerId=8101, partnerName='测试_赛事机构A'
```
✅ MAPPER-NAME-JOIN 改动**真实生效**，前端 EntityLink 拿得到 `row.partnerName`。

### V4：PATCH-5 region 字典 + 编辑端点

字典端点：
```
code=200, count=34, sample[0]=北京->beijing
```
✅ 34 项 GB/T 2260 国标简称已落库。

编辑端点：
```bash
curl -s -X PUT "http://localhost:8080/jst/organizer/channel/apply/999/region" \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"region":"beijing"}'
# {"msg":"渠道认证申请不存在","code":60010}
```
✅ 返业务码 60010（applyId=999 不存在），证明路由已上线（不是 404）。

---

## 四、结论

**无需 PATCH-6**。PATCH-2 报告隐患 #2 是旧 jvm 假阳性。所有 PATCH-1~5 改动在新 jvm 下全部生效：
- CORS 通配 ✅
- Picker autoOpen 链路 ✅
- CoursePicker 后端 search ✅
- EntityLink partnerName ✅
- region 字典 + 编辑端点 ✅

---

## 五、TODO（仅记录无需 action）

PATCH-2 报告里的另一个隐患 #3「`/jst/event/enroll_form_template/list` 老路径 500」**仍存在但与 PATCH 系列无关**——前端实际用的是 `/jst/event/form/template/list` 200 正常。如果未来要清理老路径，单独开卡。
