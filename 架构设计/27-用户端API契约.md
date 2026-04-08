# 27. 用户端 API 契约（小程序 / 公开页）

> 范围：微信小程序 + 公开页（证书验真/成绩查询）所有 RESTful 接口
> URL 前缀约定：
> - **`/jst/wx/**`** → 需要登录的小程序接口（自动注入 JWT）
> - **`/jst/public/**`** → 完全公开接口（无需 token）
> 与 admin 端隔离：admin 端走 `/jst/{module}/**` 已在 12 文档定义
> 落点：每个模块的 `Wx*Controller` 类，独立于 admin Controller

---

## 1. 认证与角色

### 1.1 认证方式
沿用若依 JWT，token header `Authorization: Bearer xxx`。

### 1.2 角色（与 sys_role 一致）
| role_key | 中文 | 用途 |
|---|---|---|
| jst_student | 学生/家长 | 默认角色，注册后自动赋予 |
| jst_channel | 渠道方/老师 | 通过身份认证后赋予（不撤销 jst_student） |
| jst_partner | 赛事方 | 通过入驻审核后赋予（仅赛事方工作台用） |
| jst_platform_op | 平台运营 | admin 端 |

**用户可同时持有多个角色**：例如 `[jst_student, jst_channel]` 表示既是学生又是老师。

### 1.3 权限标识规约
小程序端权限**走角色而非细粒度 perms**（细粒度 perms 留给 admin）：
- `@PreAuthorize("hasRole('jst_student')")` → 学生接口
- `@PreAuthorize("hasRole('jst_channel')")` → 老师接口
- `@PreAuthorize("hasAnyRole('jst_student','jst_channel')")` → 都可以

---

## 2. 接口总览（共 60+ 接口）

### 模块分布
| 模块 | 接口前缀 | 接口数 | Controller |
|---|---|---|---|
| 认证/资料 | `/jst/wx/auth` `/jst/wx/user` | 8 | jst-user/WxAuthController + WxUserController |
| 赛事/报名 | `/jst/wx/contest` `/jst/wx/enroll` | 10 | jst-event/Wx* |
| 课程/学习 | `/jst/wx/course` | 6 | jst-event/WxCourseController |
| 订单/支付/退款 | `/jst/wx/order` `/jst/wx/pay` | 9 | jst-order/Wx* |
| 预约/核销 | `/jst/wx/appointment` | 6 | jst-order/WxAppointmentController |
| 积分/商城 | `/jst/wx/points` `/jst/wx/mall` | 7 | jst-points/Wx* |
| 优惠券/权益 | `/jst/wx/coupon` `/jst/wx/rights` | 6 | jst-marketing/Wx* |
| 成绩/证书 | `/jst/wx/score` `/jst/wx/cert` | 5 | jst-event/Wx* |
| 公告/消息 | `/jst/wx/notice` `/jst/wx/message` | 5 | jst-message/Wx* |
| 老师工作台 | `/jst/wx/teacher` | 8 | jst-user/WxTeacherController |
| 公开查询 | `/jst/public/score` `/jst/public/cert` | 2 | jst-event/PublicController |
| 文件 | `/jst/wx/upload` | 1 | jst-common/WxUploadController |

---

## 3. 完整接口清单

### 3.1 认证 / 用户资料 (jst-user)

| Method | Path | 入参 | 出参 | 权限 | 备注 |
|---|---|---|---|---|---|
| POST | /jst/wx/auth/login | `{code}` | `{token, userInfo, isNewUser}` | 公开 | wx.login code 换 JWT |
| POST | /jst/wx/auth/bind-phone | `{encryptedData, iv}` | `{mobile}` | 登录 | getPhoneNumber |
| POST | /jst/wx/auth/logout | — | — | 登录 | |
| GET  | /jst/wx/user/profile | — | UserProfileVO | 登录 | 当前用户资料 |
| PUT  | /jst/wx/user/profile | UserProfileForm | — | 登录 | 含真实姓名/生日/头像 |
| GET  | /jst/wx/user/binding | — | List<BindingVO> | 登录 | 已绑定渠道方 |
| POST | /jst/wx/user/binding/switch | `{channelId}` | — | 登录 | 切换/新建绑定 |
| POST | /jst/wx/user/binding/unbind | `{bindingId}` | — | 登录 | 解绑 |

### 3.2 赛事 / 报名 (jst-event)

| Method | Path | 入参 | 出参 | 权限 |
|---|---|---|---|---|
| GET  | /jst/wx/index/banner | — | List<BannerVO> | 公开 |
| GET  | /jst/wx/contest/hot | `{limit?}` | List<ContestCardVO> | 公开 |
| GET  | /jst/wx/contest/list | ContestQueryDTO | Page<ContestCardVO> | 公开 |
| GET  | /jst/wx/contest/{id} | — | ContestDetailVO | 公开 |
| POST | /jst/wx/contest/{id}/favorite | — | — | 登录 |
| GET  | /jst/wx/enroll/template | `{contestId, participantId}` | FormTemplateVO | 登录 |
| POST | /jst/wx/enroll/draft | EnrollDraftDTO | `{enrollId}` | 登录 |
| POST | /jst/wx/enroll/submit | EnrollSubmitDTO | `{enrollId, orderId}` | 登录 |
| GET  | /jst/wx/enroll/{id} | — | EnrollDetailVO | 登录 |
| POST | /jst/wx/enroll/{id}/supplement | EnrollSupplementDTO | — | 登录 |

### 3.3 课程 / 学习 (jst-event)

| Method | Path | 入参 | 出参 | 权限 |
|---|---|---|---|---|
| GET  | /jst/wx/course/list | CourseQueryDTO | Page<CourseCardVO> | 公开 |
| GET  | /jst/wx/course/{id} | — | CourseDetailVO | 公开 |
| GET  | /jst/wx/course/play-auth | `{courseId}` | `{playAuth, vodId}` | 登录 | 阿里云 VOD 播放凭证 |
| POST | /jst/wx/course/learn-record | LearnRecordDTO | — | 登录 | 上报学习进度 |
| GET  | /jst/wx/course/my | — | List<MyCourseVO> | 登录 | 含 AI 课堂 Tab |
| GET  | /jst/wx/course/maic-list | — | List<MaicCourseVO> | 老师 |

### 3.4 订单 / 支付 / 退款 (jst-order)

| Method | Path | 入参 | 出参 | 权限 |
|---|---|---|---|---|
| POST | /jst/wx/order/create | CreateOrderReqDTO | CreateOrderResVO | 登录 | 含混合支付分摊 |
| GET  | /jst/wx/order/{id} | — | OrderDetailVO | 登录 |
| GET  | /jst/wx/order/my | OrderQueryDTO | Page<OrderCardVO> | 登录 |
| POST | /jst/wx/order/{id}/cancel | — | — | 登录 | 待支付状态可取消 |
| POST | /jst/wx/pay/wechat | `{orderId}` | WechatPrepayVO | 登录 | 调起微信支付 |
| POST | /jst/wx/pay/voucher | `{orderId, voucherUrl}` | — | 登录 | 对公转账凭证上传 |
| POST | /jst/wx/pay/notify/wechat | 微信回调 | — | 公开 | 仅微信调用,验签 |
| POST | /jst/wx/order/{id}/refund | RefundApplyDTO | `{refundId}` | 登录 | 用户发起退款 |
| GET  | /jst/wx/refund/{id} | — | RefundDetailVO | 登录 |

### 3.5 预约 / 核销 (jst-order)

| Method | Path | 入参 | 出参 | 权限 |
|---|---|---|---|---|
| POST | /jst/wx/appointment/create | CreateAppointmentDTO | `{appointmentId, qrCode}` | 登录 |
| GET  | /jst/wx/appointment/my | — | List<AppointmentVO> | 登录 |
| POST | /jst/wx/appointment/team/create | CreateTeamApptDTO | `{teamAppointmentId}` | 老师 |
| **POST** | **/jst/wx/appointment/team/writeoff** ⭐ | TeamWriteoffDTO | WriteoffResVO | 老师 | 扫码核销,Redisson 锁 |
| GET  | /jst/wx/appointment/team/{id} | — | TeamApptDetailVO | 登录 |
| GET  | /jst/wx/appointment/writeoff/list | — | List<WriteoffRecordVO> | 老师 |

### 3.6 积分 / 商城 (jst-points)

| Method | Path | 入参 | 出参 | 权限 |
|---|---|---|---|---|
| GET  | /jst/wx/points/account | — | PointsAccountVO | 登录 |
| GET  | /jst/wx/points/ledger | LedgerQueryDTO | Page<PointsLedgerVO> | 登录 |
| GET  | /jst/wx/points/level | — | LevelInfoVO | 登录 | 等级 + 进度 |
| GET  | /jst/wx/mall/goods/list | GoodsQueryDTO | Page<GoodsCardVO> | 登录 |
| GET  | /jst/wx/mall/goods/{id} | — | GoodsDetailVO | 登录 |
| POST | /jst/wx/mall/exchange | ExchangeReqDTO | `{exchangeId, orderId}` | 登录 | 锁库存+扣积分 |
| GET  | /jst/wx/mall/exchange/my | — | List<ExchangeOrderVO> | 登录 |

### 3.7 优惠券 / 权益 (jst-marketing)

| Method | Path | 入参 | 出参 | 权限 |
|---|---|---|---|---|
| GET  | /jst/wx/coupon/my | `{status?}` | List<UserCouponVO> | 登录 |
| POST | /jst/wx/coupon/receive | `{templateId}` | — | 登录 | 主动领券 |
| GET  | /jst/wx/coupon/applicable | `{orderAmount, contestId}` | List<UserCouponVO> | 登录 | 下单页选券 |
| GET  | /jst/wx/rights/my | — | List<UserRightsVO> | 登录 |
| POST | /jst/wx/rights/use | UseRightsReqDTO | — | 登录 |
| GET  | /jst/wx/rights/{id} | — | RightsDetailVO | 登录 |

### 3.8 成绩 / 证书 (jst-event)

| Method | Path | 入参 | 出参 | 权限 |
|---|---|---|---|---|
| GET  | /jst/wx/score/my | — | List<ScoreVO> | 登录 |
| GET  | /jst/wx/score/{id} | — | ScoreDetailVO | 登录 |
| GET  | /jst/wx/cert/my | — | List<CertVO> | 登录 |
| GET  | /jst/wx/cert/{id} | — | CertDetailVO | 登录 |
| POST | /jst/wx/cert/{id}/share | — | `{shareUrl}` | 登录 |

### 3.9 公告 / 消息 (jst-message)

| Method | Path | 入参 | 出参 | 权限 |
|---|---|---|---|---|
| GET  | /jst/wx/notice/list | NoticeQueryDTO | Page<NoticeVO> | 公开 |
| GET  | /jst/wx/notice/{id} | — | NoticeDetailVO | 公开 |
| GET  | /jst/wx/message/my | — | Page<MessageVO> | 登录 |
| POST | /jst/wx/message/{id}/read | — | — | 登录 |
| POST | /jst/wx/message/read-all | — | — | 登录 |

### 3.10 老师工作台 (jst-user)

> 强约束：所有接口必须 `hasRole('jst_channel')` + `@ChannelScope` 数据权限

| Method | Path | 入参 | 出参 |
|---|---|---|---|
| GET  | /jst/wx/teacher/home | — | TeacherHomeVO |
| GET  | /jst/wx/teacher/students | StudentQueryDTO | Page<StudentCardVO> |
| POST | /jst/wx/teacher/student/create | CreateParticipantDTO | `{participantId}` | 创建临时档案 |
| GET  | /jst/wx/teacher/orders | OrderQueryDTO | Page<OrderCardVO> |
| GET  | /jst/wx/teacher/data | StatsQueryDTO | StatsVO |
| GET  | /jst/wx/teacher/rebate | — | RebateVO | 返点台账 |
| POST | /jst/wx/teacher/withdraw | WithdrawDTO | `{settlementId}` | 申请提现 |
| GET  | /jst/wx/teacher/identity | — | IdentityVO | 我的认证状态 |

### 3.11 公开查询 (jst-event/jst-public)

| Method | Path | 入参 | 出参 | 权限 |
|---|---|---|---|---|
| GET | /jst/public/score/query | `{name, contestId, idCard4}` | List<ScorePublicVO> | 完全公开 |
| GET | /jst/public/cert/verify | `{certNo}` 或 `{verifyCode}` | CertVerifyVO | 完全公开 |

### 3.12 文件上传 (jst-common)

| Method | Path | 入参 | 出参 |
|---|---|---|---|
| POST | /jst/wx/upload/image | multipart file | `{url}` |
| POST | /jst/wx/upload/document | multipart file | `{url}` | 凭证/合同 |

---

## 4. 通用响应格式（与若依一致）

```json
// 成功
{ "code": 200, "msg": "操作成功", "data": { ... } }

// 业务失败
{ "code": 30020, "msg": "当前订单不允许退款", "data": null }

// 未登录
{ "code": 401, "msg": "请先登录", "data": null }

// 分页
{ "code": 200, "msg": "操作成功",
  "rows": [ ... ],
  "total": 138 }
```

---

## 5. 用户端 vs admin 端的关键差异

| 维度 | admin 端 (/jst/{module}/**) | 小程序端 (/jst/wx/**) |
|---|---|---|
| 认证 | 账号密码 + JWT | wx.login code + JWT |
| 权限粒度 | 细粒度 perms (`jst:order:refund`) | 角色级 (`hasRole('jst_student')`) |
| 数据范围 | @DataScope / @PartnerScope / @ChannelScope | 强制 user_id = 当前用户(SecurityCheck.assertSameUser) |
| 入参 | XxxQueryDTO 含管理员级筛选 | XxxQueryDTO 仅含学生可选筛选 |
| 出参 | 含所有字段(脱敏) | 仅含展示字段(无审计字段) |
| 删除 | 软删 + 角色限制 | **完全不暴露** |
| 审计 | @OperateLog 全量 | 仅敏感操作(下单/退款/绑定) |

---

## 6. Controller 命名与落点规范

```java
// 错误:复用 admin Controller
@RestController @RequestMapping("/jst/order/main")
public class JstOrderMainController { /* admin 用 */ }

// 正确:独立 Wx Controller
@RestController @RequestMapping("/jst/wx/order")
public class WxOrderController extends BaseController {
    @Autowired private OrderService orderService;  // 复用 Service

    @PreAuthorize("hasRole('jst_student')")
    @PostMapping("/create")
    public AjaxResult create(@Valid @RequestBody CreateOrderReqDTO req) {
        // 1. SecurityCheck.assertSameUser 不需要,因为 user_id 直接从 token 取
        Long userId = SecurityUtils.getUserId();
        return AjaxResult.success(orderService.createMixedPay(userId, req));
    }
}
```

**强约束**：
- 小程序 Controller 类名前缀 `Wx`，包路径 `com.ruoyi.jst.{module}.controller.wx`
- admin Controller 不带前缀，包路径 `com.ruoyi.jst.{module}.controller`
- **共用 Service 层**，不要重复实现业务逻辑
- 小程序 Controller 内禁止接受任何 user_id 入参，必须从 token 取

---

## 7. 接口文档生成

沿用若依 springdoc，新增 group：
```yaml
springdoc:
  group-configs:
    - group: 'admin'
      display-name: '管理后台 API'
      paths-to-match: '/jst/**'
      paths-to-exclude: '/jst/wx/**,/jst/public/**'
    - group: 'wx'
      display-name: '小程序 API'
      paths-to-match: '/jst/wx/**'
    - group: 'public'
      display-name: '公开 API'
      paths-to-match: '/jst/public/**'
```
访问 `/swagger-ui.html` 可分组查看。

---

## 8. 实施顺序建议

按 25 文档 §1 阶段流水线，每个 admin Controller 实现完成后**立即**配套实现对应 WxController。具体顺序：

1. WxAuthController + WxUserController（最优先，所有后续接口的前置）
2. WxContestController + WxEnrollController（业务入口）
3. WxOrderController + WxPayController（资金主线）
4. WxAppointmentController（团队核销）
5. WxPointsController + WxMallController
6. WxCouponController + WxRightsController
7. WxScoreController + WxCertController
8. WxNoticeController + WxMessageController
9. WxTeacherController（老师工作台，最后做）
10. PublicController（公开查询，独立）
