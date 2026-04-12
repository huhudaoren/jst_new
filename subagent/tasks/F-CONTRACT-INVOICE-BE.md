# F-CONTRACT-INVOICE-BE — 合同/发票小程序端接口

> 优先级：P2 | 预估：S | Agent：Backend Agent

---

## 一、背景

合同 `jst_contract_record` 和发票 `jst_invoice_record` 表已建，Admin CRUD Controller 已有（`jst-finance` 模块），但小程序端（渠道方/赛事方）**没有任何接口**。需要补齐 wx 端读取 + 发票申请能力。

## 二、新增 Controller

### 1. `WxContractController.java`

模块：`jst-finance`
路径：`controller/wx/WxContractController.java`
Base URL：`/jst/wx/contract`

| ��法 | URL | 说明 | 权限 |
|---|---|---|---|
| GET | `/list` | 我的合同列表（按当前用户 target_id + target_type 筛选��� | `@LoginRequired` |
| GET | `/{contractId}` | 合同详情 | `@LoginRequired` + 数据归属校验 |

**数据隔离**：
- 根据当前登录用户身份判断 `target_type`（channel/partner）
- `target_id` = 当前用户的 channelId 或 partnerId
- 不允许跨用户查询

**返回 VO**：
```java
public class ContractRecordVO {
    private Long contractId;
    private String contractNo;
    private String contractType;    // partner_coop/channel_settle/supplement
    private String status;          // draft/pending_sign/signed/expired/archived
    private String fileUrl;
    private Date effectiveTime;
    private Date signTime;
    private String remark;
    private Date createTime;
}
```

### 2. `WxInvoiceController.java`

模块：`jst-finance`
路径：`controller/wx/WxInvoiceController.java`
Base URL：`/jst/wx/invoice`

| 方法 | URL | 说明 | 权限 |
|---|---|---|---|
| GET | `/list` | 我的发票列表 | `@LoginRequired` |
| GET | `/{invoiceId}` | 发票详情 | `@LoginRequired` + 数据归属校验 |
| POST | `/apply` | 申请开票 | `@LoginRequired` |

**申请开票 ReqDTO**：
```java
public class InvoiceApplyForm {
    @NotBlank private String refSettlementNo;  // 关联结算/提现单号
    @NotBlank private String invoiceTitle;      // 发票抬头
    @NotBlank private String taxNo;             // 税号
    @NotNull  private BigDecimal amount;        // 金额
    private String remark;
}
```

**申请逻辑**：
1. 校验 refSettlementNo 归属当前用户
2. 校验金额 ≤ 结算单实际金额
3. 创建 `jst_invoice_record`，status = `pending_apply`
4. 返回新建记录 ID

**返回 VO**：
```java
public class InvoiceRecordVO {
    private Long invoiceId;
    private String invoiceNo;
    private String refSettlementNo;
    private String invoiceTitle;
    private String taxNo;
    private BigDecimal amount;
    private String status;          // pending_apply/reviewing/issuing/issued/voided/red_offset
    private String fileUrl;
    private String expressStatus;
    private Date issueTime;
    private Date createTime;
}
```

## 三、Service 层

在 `jst-finance` 中新建或扩展：

### `WxFinanceService.java`

```java
public interface WxFinanceService {
    // 合同
    List<ContractRecordVO> listMyContracts(String targetType, Long targetId);
    ContractRecordVO getContractDetail(Long contractId, String targetType, Long targetId);
    
    // 发票
    List<InvoiceRecordVO> listMyInvoices(String targetType, Long targetId);
    InvoiceRecordVO getInvoiceDetail(Long invoiceId, String targetType, Long targetId);
    Long applyInvoice(InvoiceApplyForm form, String targetType, Long targetId);
}
```

**复用**：直接用已有的 `JstContractRecordMapper` 和 `JstInvoiceRecordMapper`，只需追加查询条件（target_type + target_id）。

## 四、Mapper 扩展

在已有 Mapper XML 中追加按 target 筛选的查询：

```xml
<!-- JstContractRecordMapper.xml 追加 -->
<select id="selectByTarget" resultMap="JstContractRecordResult">
    SELECT * FROM jst_contract_record 
    WHERE target_type = #{targetType} AND target_id = #{targetId} AND del_flag = '0'
    ORDER BY create_time DESC
</select>

<!-- JstInvoiceRecordMapper.xml 追加 -->
<select id="selectByTarget" resultMap="JstInvoiceRecordResult">
    SELECT * FROM jst_invoice_record 
    WHERE target_type = #{targetType} AND target_id = #{targetId} AND del_flag = '0'
    ORDER BY create_time DESC
</select>
```

## 五、测试用例

追加到 `test/wx-tests.http`：

```http
### 合同列表
GET {{baseUrl}}/jst/wx/contract/list
Authorization: Bearer {{channelToken}}

### 合同详情
GET {{baseUrl}}/jst/wx/contract/1
Authorization: Bearer {{channelToken}}

### 发票列表
GET {{baseUrl}}/jst/wx/invoice/list
Authorization: Bearer {{channelToken}}

### 申请开票
POST {{baseUrl}}/jst/wx/invoice/apply
Authorization: Bearer {{channelToken}}
Content-Type: application/json

{
  "refSettlementNo": "WD20260412001",
  "invoiceTitle": "测试公司",
  "taxNo": "91110000MA0123456X",
  "amount": 1000.00
}
```

## 六、DoD
- [ ] 2 个 Controller + 5 个端点
- [ ] VO 不暴露内部字段（del_flag/update_by 等）
- [ ] 数据归属校验（不可跨用户）
- [ ] 编译通���
- [ ] .http 测试用例通过
- [ ] 报告交付
