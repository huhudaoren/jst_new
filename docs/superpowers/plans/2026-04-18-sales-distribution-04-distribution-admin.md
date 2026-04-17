# 销售管理 + 渠道分销 - 计划 #4: 渠道分销 + admin 后台 + 反带客户加固

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 落地渠道分销链路（邀请码生成、ChannelInviteService 含 M1 互斥、ChannelDistributionService 分销提成计算 + Quartz 入账）+ 小程序前端 3 页（邀请码 / 邀请列表 / 分销收益） + 渠道方注册回填 invite_code + admin 后台 5 页（销售管理 / 业绩看板 / 月结审核 / 冲突队列 / 团队管理） + 反带客户加固落地（AC1 手机号脱敏端到端 + AC3 Excel 水印实际插入）。

**Architecture:** Backend 复刻 plan-02 service+listener 模式；分销提成走独立 ledger 表 + 复用 jst_channel_withdraw 提现流程；前端 admin 页复用 ruoyi-ui 风格；小程序复用现有 jst-uniapp uView 2.0 + Design Token。

**Tech Stack:** 同 plan-02/03 + UniApp（jst-uniapp 小程序）+ EasyExcel/POI（导出水印）。

**前置:** plan-01/02/03 全部合并到当前分支基。

**关联 spec:** §1.3 / §2.2 / §3.5 渠道分销结算 / §4.3 / §4.6 / §5.1 admin 部分 / §5.4 / §5.5 渠道分销 + admin API

---

## 文件结构

### Java
**service/**
- `InviteCodeService.java` + impl（生成唯一邀请码 + 校验）
- `ChannelInviteService.java` + impl（M1 互斥 + 反环路 + admin 后门）
- `ChannelDistributionService.java` + impl（分销提成计算）
- `AdminSalesDashboardService.java` + impl（业绩看板聚合）
- `SalesAuditService.java` + impl（脱敏审计 + 导出审计查询）

**controller/**
- `controller/wx/WxChannelInviteController.java`（小程序：邀请码 + 邀请列表 + 分销收益）
- `controller/admin/AdminChannelInviteController.java`（admin 后门改邀请关系）
- `controller/admin/AdminSalesDashboardController.java`（业绩看板）
- `controller/admin/AdminSettlementController.java`（月结审核）
- `controller/admin/AdminConflictController.java`（冲突队列）
- `controller/admin/AdminBindingController.java`（手动绑定渠道-销售）
- `controller/admin/AdminAuditController.java`（脱敏审计 + 导出审计）
- `controller/sales/SalesManagerController.java`（团队管理）

**listener/**
- `ChannelInviteBindingListener.java`（订阅 ChannelRegisteredEvent，建立邀请关系）
- 修改 `SalesCommissionListener.java`（在 OrderPaidEvent 处理时同时调 ChannelDistributionService）

**job/**
- `ChannelDistributionAccrueJob.java`

**util/**
- `SalesExportUtils.java`（实际插入 Excel/PDF/CSV 水印的工具方法 — 使用 plan-01 的水印文本生成）

### Mapper XML 增量
- `JstChannelInviteMapper.xml` 加 `selectByInviter` / `unbindActive` / `selectActiveByInviteCode`
- `JstChannelDistributionLedgerMapper.xml` 加 `batchInsert` / `selectPendingForAccrue` / `selectByInviter` / `aggregateByMonth`
- `JstChannelMapper.xml` 加 `updateInviteCode` / `selectByInviteCode` / `updateParentInviteAttempted`
- 修改 `JstSalesCommissionLedgerMapper.xml` 加 admin 端聚合查询

### SQL
- `架构设计/ddl/99-migration-sales-distribution-distribution-quartz.sql`（1 个 Quartz）

### Frontend (ruoyi-ui)
**api/admin/sales/**
- `index.js`（admin 销售 CRUD 全套）
- `dashboard.js`（业绩看板）
- `settlement.js`（月结审核）
- `conflict.js`（冲突队列）
- `binding.js`（手动绑定）
- `audit.js`（审计查询）

**api/sales/manager/**
- `team.js`（团队管理）

**views/jst/sales/**
- `index.vue`（销售档案管理列表 + 创建 + 编辑费率/主管 + 离职申请/执行）
- `dashboard.vue`（业绩看板，echarts 图表）
- `conflict.vue`（冲突队列）
- `settlement/index.vue`（月结单审核列表）
- `settlement/detail.vue`（单张月结单明细）
- `audit.vue`（脱敏审计 + 导出审计两个 Tab）

**views/sales/manager/**
- `team/index.vue`（团队管理 — 下属销售列表 + 切换查看）
- `settlement/index.vue`（团队成员月结单 — 含金额）

### Frontend (jst-uniapp)
**api/**
- `invite.js`（小程序：getMyInviteCode / listInvited / listDistributionLedger / monthlyDistributionSummary）

**pages-sub/channel/**
- `invite-code.vue`（我的邀请码 + 二维码）
- `invite-list.vue`（我邀请的渠道列表）
- `distribution.vue`（分销收益明细）

**修改：**
- `pages-sub/public/channel-register.vue`（或类似的渠道注册页面）— 加 invite_code 隐藏字段（从 launch options 拿）+ business_no 选填字段

### CLAUDE.md
- 修改顶部 + §六 SALES-DISTRIBUTION 行（标记 plan-04 完成 → 整个 SALES-DISTRIBUTION 子系统完成）

---

## Task 1: 准备分支 + 检查前置

- [ ] **Step 1: 拉 main 开新分支**

```bash
cd D:/coding/jst_v1
git checkout main
git pull
git checkout -b feature/sales-distribution-04-distribution-admin
```

- [ ] **Step 2: 验证前置**

```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
SHOW TABLES LIKE 'jst_channel_invite';
SHOW TABLES LIKE 'jst_channel_distribution_ledger';
SELECT COUNT(*) FROM sys_job WHERE job_id BETWEEN 2001 AND 2007;
" jst
```
Expected: 表存在 + 7 个 Quartz 任务（plan-01/02/03 注册过）。

---

## Task 2: InviteCodeService（邀请码生成 + 校验）+ 单测

**Files:**
- Create: `service/InviteCodeService.java` + impl
- Create: 单测

- [ ] **Step 1: 接口**

```java
public interface InviteCodeService {
    /** 给渠道生成唯一邀请码（注册时调，存到 jst_channel.invite_code） */
    String generateForChannel(Long channelId);
    /** 校验邀请码是否有效（active 渠道） */
    JstChannel resolveInviter(String inviteCode);
}
```

- [ ] **Step 2: 测试 4 case**

- generateForChannel 返回 8-12 位字母数字混合 + 唯一性（mock channelMapper 检查不重复）
- generateForChannel 重试机制（如生成的 code 已被占用）
- resolveInviter 命中 active 渠道
- resolveInviter 命中 disabled 渠道 → 返 null
- resolveInviter 不存在 → 返 null

- [ ] **Step 3: 实现**

```java
@Service
public class InviteCodeServiceImpl implements InviteCodeService {

    @Autowired private JstChannelMapper channelMapper;
    private static final String CHARS = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";  // 排除易混淆 0/O/1/I/L
    private static final int LEN = 8;
    private static final int MAX_RETRY = 5;

    @Override
    public String generateForChannel(Long channelId) {
        String code = null;
        for (int i = 0; i < MAX_RETRY; i++) {
            String candidate = randomCode(LEN);
            JstChannel exists = channelMapper.selectByInviteCode(candidate);
            if (exists == null) { code = candidate; break; }
        }
        if (code == null) throw new ServiceException("邀请码生成失败（重试 5 次）");
        channelMapper.updateInviteCode(channelId, code);
        return code;
    }

    @Override
    public JstChannel resolveInviter(String inviteCode) {
        if (inviteCode == null || inviteCode.isEmpty()) return null;
        JstChannel c = channelMapper.selectByInviteCode(inviteCode);
        if (c == null) return null;
        if (!"active".equals(c.getStatus()) && !"normal".equals(c.getStatus())) return null;
        return c;
    }

    private String randomCode(int len) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append(CHARS.charAt(r.nextInt(CHARS.length())));
        return sb.toString();
    }
}
```

加 Mapper 接口方法 `selectByInviteCode` / `updateInviteCode`，对应 XML：
```xml
    <select id="selectByInviteCode" resultMap="JstChannelResult">
        SELECT * FROM jst_channel WHERE invite_code = #{inviteCode} LIMIT 1
    </select>

    <update id="updateInviteCode">
        UPDATE jst_channel SET invite_code = #{inviteCode} WHERE channel_id = #{channelId}
    </update>
```

- [ ] **Step 4: 跑测试 + 提交**

```bash
git commit -m "feat(channel): InviteCodeService 邀请码生成 (8 位字母数字+排除易混) + 4 单测"
```

---

## Task 3: ChannelInviteService（M1 互斥 + 反环路 + admin 后门）+ 单测

**Files:**
- Create: `service/ChannelInviteService.java` + impl
- Modify: `JstChannelInviteMapper.xml` 加查询
- Create: 单测

- [ ] **Step 1: Mapper XML 追加**

```xml
    <select id="selectActiveByInvitee" parameterType="Long" resultMap="JstChannelInviteResult">
        SELECT <include refid="cols"/> FROM jst_channel_invite
         WHERE invitee_channel_id = #{inviteeChannelId} AND status = 'active' LIMIT 1
    </select>

    <select id="selectByInviter" parameterType="Long" resultMap="JstChannelInviteResult">
        SELECT <include refid="cols"/> FROM jst_channel_invite
         WHERE inviter_channel_id = #{inviterChannelId} AND status = 'active'
        ORDER BY invited_at DESC
    </select>

    <update id="unbindActive">
        UPDATE jst_channel_invite SET status='unbound', update_time=NOW()
         WHERE invite_id = #{inviteId} AND status='active'
    </update>
```

- [ ] **Step 2: ChannelInviteService 接口**

```java
public interface ChannelInviteService {
    /**
     * 渠道注册时建立邀请关系（M1 互斥 + 反环路 + 自邀防御）。
     * @return 已建立的 invite_id；未建立返 null（静默忽略 / M1 互斥）
     */
    Long establishOnRegister(Long newChannelId, String filledInviteCode, boolean hasSalesBinding);

    /** admin 后门：解绑当前邀请关系 */
    void unbindByAdmin(Long inviteId, Long adminUserId, String reason);

    /** admin 后门：强制建立邀请关系（绕过 M1 互斥） */
    Long forceEstablishByAdmin(Long inviterChannelId, Long inviteeChannelId, BigDecimal customRate, Long adminUserId);

    /** admin 后门：修改单独费率 */
    void updateRateByAdmin(Long inviteId, BigDecimal rate, Long adminUserId);

    /** 查渠道下级列表 */
    List<JstChannelInvite> listByInviter(Long inviterChannelId);

    /** 查渠道上级 */
    JstChannelInvite getActiveByInvitee(Long inviteeChannelId);
}
```

- [ ] **Step 3: 测试 8 case**

- establishOnRegister 无 invite_code → 返 null
- establishOnRegister 命中 → 写入 jst_channel_invite + 更新 jst_channel.parent_invite_id
- establishOnRegister 命中但 inviter 是 official → 返 null（is_official 跳过）
- establishOnRegister 命中但 inviter 已封禁 → 返 null
- establishOnRegister 自邀（inviteCode 是 newChannel 自己的）→ 返 null
- establishOnRegister M1 互斥 (hasSalesBinding=true) → 不写 invite，UPDATE jst_channel.parent_invite_attempted 字段
- unbindByAdmin → status='unbound'
- forceEstablishByAdmin 跳过 M1 互斥 → 写入

- [ ] **Step 4: 实现**

```java
@Service
public class ChannelInviteServiceImpl implements ChannelInviteService {

    @Autowired private JstChannelInviteMapper inviteMapper;
    @Autowired private JstChannelMapper channelMapper;
    @Autowired private InviteCodeService inviteCodeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long establishOnRegister(Long newChannelId, String filledInviteCode, boolean hasSalesBinding) {
        if (filledInviteCode == null || filledInviteCode.isEmpty()) return null;
        JstChannel inviter = inviteCodeService.resolveInviter(filledInviteCode);
        if (inviter == null) return null;
        if (inviter.getIsOfficial() != null && inviter.getIsOfficial() == 1) return null;
        if (Objects.equals(inviter.getChannelId(), newChannelId)) return null;  // 自邀

        // M1 互斥：已绑定销售则不建邀请，记录 attempted
        if (hasSalesBinding) {
            channelMapper.updateParentInviteAttempted(newChannelId, filledInviteCode);
            return null;
        }

        JstChannelInvite row = new JstChannelInvite();
        row.setInviteId(snowId());
        row.setInviterChannelId(inviter.getChannelId());
        row.setInviteeChannelId(newChannelId);
        row.setInviteCode(filledInviteCode);
        row.setShareScene("mp_share");
        row.setInvitedAt(new Date());
        row.setStatus("active");
        inviteMapper.insertJstChannelInvite(row);

        // 更新 channel.parent_invite_id
        JstChannel update = new JstChannel();
        update.setChannelId(newChannelId);
        update.setParentInviteId(row.getInviteId());
        channelMapper.updateJstChannel(update);

        return row.getInviteId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindByAdmin(Long inviteId, Long adminUserId, String reason) {
        inviteMapper.unbindActive(inviteId);
        // 写 sys_oper_log（@Log 注解通常处理，但本方法直接由 service 调用时手工写）
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long forceEstablishByAdmin(Long inviterChannelId, Long inviteeChannelId, BigDecimal customRate, Long adminUserId) {
        // 先解绑 invitee 当前生效的邀请（如有）
        JstChannelInvite current = inviteMapper.selectActiveByInvitee(inviteeChannelId);
        if (current != null) inviteMapper.unbindActive(current.getInviteId());

        JstChannelInvite row = new JstChannelInvite();
        row.setInviteId(snowId());
        row.setInviterChannelId(inviterChannelId);
        row.setInviteeChannelId(inviteeChannelId);
        // 取上级渠道的 invite_code（admin 强建无小程序分享，记录上级 code 便于追溯）
        JstChannel inviter = channelMapper.selectJstChannelByChannelId(inviterChannelId);
        row.setInviteCode(inviter == null ? "" : inviter.getInviteCode());
        row.setShareScene("manual_admin");
        row.setCommissionRate(customRate);
        row.setInvitedAt(new Date());
        row.setStatus("active");
        inviteMapper.insertJstChannelInvite(row);

        JstChannel update = new JstChannel();
        update.setChannelId(inviteeChannelId);
        update.setParentInviteId(row.getInviteId());
        channelMapper.updateJstChannel(update);
        return row.getInviteId();
    }

    @Override
    public void updateRateByAdmin(Long inviteId, BigDecimal rate, Long adminUserId) {
        JstChannelInvite update = new JstChannelInvite();
        update.setInviteId(inviteId);
        update.setCommissionRate(rate);
        inviteMapper.updateJstChannelInvite(update);
    }

    @Override public List<JstChannelInvite> listByInviter(Long inviterChannelId) {
        return inviteMapper.selectByInviter(inviterChannelId);
    }

    @Override public JstChannelInvite getActiveByInvitee(Long inviteeChannelId) {
        return inviteMapper.selectActiveByInvitee(inviteeChannelId);
    }

    private Long snowId() { /* 复用 SalesServiceImpl */ return 0L; }
}
```

加 `JstChannelMapper.xml`：
```xml
    <update id="updateParentInviteAttempted">
        UPDATE jst_channel SET parent_invite_attempted = #{inviteCode}
         WHERE channel_id = #{channelId}
    </update>
```

- [ ] **Step 5: 提交**

```bash
git commit -m "feat(channel): ChannelInviteService (M1 互斥 + 反环路 + admin 后门 unbind/forceBind/updateRate) + 8 单测"
```

---

## Task 4: ChannelDistributionService（分销提成计算）+ 单测

**Files:**
- Create: `service/ChannelDistributionService.java` + impl
- Modify: `JstChannelDistributionLedgerMapper.xml`
- Create: 单测

- [ ] **Step 1: Mapper XML 追加**

```xml
    <insert id="batchInsert" parameterType="java.util.List">
        INSERT INTO jst_channel_distribution_ledger (
            ledger_id, inviter_channel_id, invitee_channel_id, order_id, order_no,
            base_amount, applied_rate, raw_amount, compress_ratio, amount,
            status, accrue_at, create_time
        ) VALUES
        <foreach collection="list" item="r" separator=",">
            (#{r.ledgerId},#{r.inviterChannelId},#{r.inviteeChannelId},#{r.orderId},#{r.orderNo},
             #{r.baseAmount},#{r.appliedRate},#{r.rawAmount},#{r.compressRatio},#{r.amount},
             #{r.status},#{r.accrueAt},NOW())
        </foreach>
    </insert>

    <select id="selectPendingForAccrue" resultType="Long">
        SELECT ledger_id FROM jst_channel_distribution_ledger
         WHERE status = 'pending' AND accrue_at &lt;= NOW()
         LIMIT 1000
    </select>

    <update id="markAccruedBatch">
        UPDATE jst_channel_distribution_ledger SET status='accrued', accrued_at=NOW(), update_time=NOW()
         WHERE ledger_id IN <foreach collection="ids" item="id" open="(" separator="," close=")">#{id}</foreach>
           AND status='pending'
    </update>

    <update id="cancelPendingByOrder">
        UPDATE jst_channel_distribution_ledger SET status='cancelled', update_time=NOW()
         WHERE order_id = #{orderId} AND status='pending'
    </update>

    <select id="selectByInviter" resultMap="JstChannelDistributionLedgerResult">
        SELECT <include refid="cols"/> FROM jst_channel_distribution_ledger
         WHERE inviter_channel_id = #{inviterChannelId}
        <if test="status != null and status != ''">AND status = #{status}</if>
        ORDER BY create_time DESC
    </select>

    <select id="aggregateByMonth" resultType="java.util.Map">
        SELECT DATE_FORMAT(create_time, '%Y-%m') AS month,
               COUNT(*) AS cnt, SUM(amount) AS total
          FROM jst_channel_distribution_ledger
         WHERE inviter_channel_id = #{inviterChannelId} AND status IN ('accrued','settled','paid')
         GROUP BY DATE_FORMAT(create_time, '%Y-%m')
         ORDER BY month DESC
    </select>
```

- [ ] **Step 2: 接口**

```java
public interface ChannelDistributionService {
    /** 订单付款时计算分销提成（与 SalesCommissionService 同事务，由 Listener 协调） */
    JstChannelDistributionLedger calculateOnOrderPaid(OrderPaidContext ctx, BigDecimal compressRatio);

    /** 退款撤销 pending */
    int cancelOnOrderRefunded(Long orderId);

    /** Quartz 用：批量 pending → accrued */
    int accruePendingByCron();

    List<JstChannelDistributionLedger> listByInviter(Long channelId, String status);
    Map<String, Object> aggregateByMonth(Long channelId);
}
```

- [ ] **Step 3: 测试 6 case**

- calculateOnOrderPaid 渠道有 active 上级 → 写 1 行
- calculateOnOrderPaid 渠道无上级 → 0 行
- calculateOnOrderPaid 上级是 official → 0 行
- calculateOnOrderPaid 上级被封禁 → 0 行
- calculateOnOrderPaid 自定义 commission_rate → 用自定义而非默认 1.5%
- calculateOnOrderPaid 应用统一 compress_ratio（与销售提成上限对齐）

- [ ] **Step 4: 实现**

```java
@Service
public class ChannelDistributionServiceImpl implements ChannelDistributionService {

    @Autowired private JstChannelDistributionLedgerMapper ledgerMapper;
    @Autowired private ChannelInviteService inviteService;
    @Autowired private JstChannelMapper channelMapper;

    @Value("${jst.sales.distribution.default_rate:0.015}")
    private BigDecimal defaultRate;

    @Value("${jst.sales.aftersales_days:7}")
    private int aftersalesDays;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JstChannelDistributionLedger calculateOnOrderPaid(OrderPaidContext ctx, BigDecimal compressRatio) {
        if (ctx.paidAmount == null || ctx.paidAmount.compareTo(BigDecimal.ZERO) <= 0) return null;
        JstChannelInvite invite = inviteService.getActiveByInvitee(ctx.channelId);
        if (invite == null) return null;
        JstChannel inviter = channelMapper.selectJstChannelByChannelId(invite.getInviterChannelId());
        if (inviter == null) return null;
        if (inviter.getIsOfficial() != null && inviter.getIsOfficial() == 1) return null;
        if (!"active".equals(inviter.getStatus()) && !"normal".equals(inviter.getStatus())) return null;

        BigDecimal rate = invite.getCommissionRate() != null ? invite.getCommissionRate() : defaultRate;
        BigDecimal raw = ctx.paidAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal ratio = compressRatio != null ? compressRatio : BigDecimal.ONE;
        BigDecimal amount = raw.multiply(ratio).setScale(2, RoundingMode.HALF_UP);

        JstChannelDistributionLedger row = new JstChannelDistributionLedger();
        row.setLedgerId(snowId());
        row.setInviterChannelId(inviter.getChannelId());
        row.setInviteeChannelId(ctx.channelId);
        row.setOrderId(ctx.orderId);
        row.setOrderNo(ctx.orderNo);
        row.setBaseAmount(ctx.paidAmount);
        row.setAppliedRate(rate);
        row.setRawAmount(raw);
        row.setCompressRatio(ratio);
        row.setAmount(amount);
        row.setStatus("pending");
        row.setAccrueAt(new Date(ctx.payTime.getTime() + 86400_000L * aftersalesDays));

        ledgerMapper.insertJstChannelDistributionLedger(row);
        return row;
    }

    // 其他方法实现...
}
```

- [ ] **Step 5: 提交**

```bash
git commit -m "feat(channel): ChannelDistributionService 分销提成计算 (默认费率/自定义/官方豁免/封禁跳过) + 6 单测"
```

---

## Task 5: 协调 SalesCommissionListener + ChannelDistribution 同事务计算

> 关键：销售提成和分销提成必须**同事务计算**，且共享同一个 `compress_ratio`（统一应用单笔上限）。

**Files:**
- Modify: `service/SalesCommissionService.java` 接口（拆分 calculate 阶段）
- Modify: `service/impl/SalesCommissionServiceImpl.java` + `ChannelDistributionServiceImpl`
- Modify: `listener/SalesCommissionListener.java`

- [ ] **Step 1: 重构 SalesCommissionService**

把单一的 `calculateOnOrderPaid` 拆为两步：

```java
public interface SalesCommissionService {
    /** Step 1: 仅计算 raw amounts，不入库 */
    SalesCommissionDraft prepareDraft(OrderPaidContext ctx);

    /** Step 2: 应用 compress ratio + 入库 */
    List<JstSalesCommissionLedger> persist(SalesCommissionDraft draft, BigDecimal compressRatio);

    /** Step 3: 旧 API 兼容（内部调 prepareDraft + computeRatio + persist） */
    @Deprecated
    List<JstSalesCommissionLedger> calculateOnOrderPaid(OrderPaidContext ctx);
}

public class SalesCommissionDraft {
    public List<JstSalesCommissionLedger> rawLedgers;  // raw_amount 已填，amount/compress_ratio 待填
    public BigDecimal sumRaw;
}
```

- [ ] **Step 2: 修改 ChannelDistributionService**

类似拆分（prepareDraft / persist）。

- [ ] **Step 3: 修改 SalesCommissionListener**

```java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void on(OrderPaidEvent event) {
    OrderPaidContext ctx = buildContext(event);

    // Step 1: prepare drafts（销售 + 分销）
    SalesCommissionDraft salesDraft = commissionService.prepareDraft(ctx);
    JstChannelDistributionLedger distDraft = distributionService.prepareDraft(ctx);

    // Step 2: 计算统一 compress ratio
    BigDecimal sumRaw = salesDraft.sumRaw;
    if (distDraft != null) sumRaw = sumRaw.add(distDraft.getRawAmount());
    sumRaw = sumRaw.add(ctx.channelRebateAmount == null ? BigDecimal.ZERO : ctx.channelRebateAmount);

    BigDecimal compressRatio = commissionService.computeCompressRatio(sumRaw, ctx.paidAmount, maxRate);

    // Step 3: 应用 + 入库
    commissionService.persist(salesDraft, compressRatio);
    if (distDraft != null) distributionService.persist(distDraft, compressRatio);
    // 渠道返点的压缩交给现有渠道返点服务处理（如果需要的话）
}
```

- [ ] **Step 4: 改测试 + 提交**

修改 SalesCommissionServiceImplTest 和 ChannelDistributionServiceImplTest 适配新 API；新增协调测试。

```bash
git commit -m "feat(channel): SalesCommissionListener 协调 sales+distribution 同事务+统一 compress_ratio"
```

---

## Task 6: ChannelInviteBindingListener（订阅 ChannelRegisteredEvent）

**Files:**
- Create: `listener/ChannelInviteBindingListener.java`

- [ ] **Step 1: Listener**

```java
@Component
public class ChannelInviteBindingListener {
    private static final Logger log = LoggerFactory.getLogger(ChannelInviteBindingListener.class);

    @Autowired private ChannelInviteService inviteService;
    @Autowired private SalesChannelBindingService bindingService;
    @Autowired private InviteCodeService inviteCodeService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Order(20)  // 在 SalesAutoBindingListener (默认 0) 之后跑，便于 M1 互斥判断
    public void on(ChannelRegisteredEvent event) {
        try {
            // 1. 给新渠道生成 invite_code（自己以后可分享）
            inviteCodeService.generateForChannel(event.getChannelId());

            // 2. 检查 M1 互斥：是否已建立销售归属
            JstSalesChannelBinding binding = bindingService.getCurrentByChannelId(event.getChannelId());
            boolean hasSales = binding != null;

            // 3. 处理 invite_code
            Long inviteId = inviteService.establishOnRegister(
                event.getChannelId(), event.getFilledInviteCode(), hasSales);

            log.info("[ChannelInviteBinding] channel={} inviteId={} (null=未建/M1 互斥)",
                     event.getChannelId(), inviteId);
        } catch (Exception ex) {
            log.error("[ChannelInviteBinding] channel={} 失败", event.getChannelId(), ex);
        }
    }
}
```

- [ ] **Step 2: 提交**

```bash
git commit -m "feat(channel): ChannelInviteBindingListener (生成邀请码+M1 互斥判定+建立邀请关系)"
```

---

## Task 7: ChannelDistributionAccrueJob

**Files:**
- Create: `RuoYi-Vue/ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/ChannelDistributionAccrueJob.java`
- Create: `架构设计/ddl/99-migration-sales-distribution-distribution-quartz.sql`

- [ ] **Step 1: Job**

```java
@Component("channelDistributionAccrueJob")
public class ChannelDistributionAccrueJob {
    @Autowired private ChannelDistributionService distributionService;
    public void execute() {
        int n = distributionService.accruePendingByCron();
        log.info("[ChannelDistributionAccrueJob] 推进 {} 行 pending → accrued", n);
    }
}
```

- [ ] **Step 2: SQL 注册**

```sql
INSERT INTO sys_job (job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark) VALUES
(2008, '渠道-分销提成入账', 'SALES', 'channelDistributionAccrueJob.execute()', '0 30 2 * * ?', '1', '1', '1', 'admin', NOW(), '售后期满分销提成 → accrued');
```

- [ ] **Step 3: 应用 + 提交**

```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 jst < "架构设计/ddl/99-migration-sales-distribution-distribution-quartz.sql"
git commit -m "feat(quartz): ChannelDistributionAccrueJob (02:30) + sys_job 注册"
```

---

## Task 8: 渠道注册流程修改（接 invite_code/business_no 字段 + publishEvent）

**Files:**
- Modify: 渠道注册请求 DTO（新增 inviteCode/businessNo 字段）
- Modify: 渠道注册 Service 实现（生成 invite_code + publish 完整事件）

- [ ] **Step 1: 找到渠道注册入口**

```bash
grep -rn "channel.*register\|JstChannelService.*create\|渠道注册" RuoYi-Vue --include="*.java" -l | head -5
```

定位到 `JstChannelServiceImpl.register()` 或类似方法，以及对应的 ReqDTO 类。

- [ ] **Step 2: ReqDTO 加字段**

在渠道注册的 ReqDTO 加：
```java
/** 销售业务编号（选填，渠道注册时填的销售给的"信物"） */
private String businessNo;

/** 邀请码（选填，由小程序分享链接自动回填） */
private String inviteCode;
// getter/setter
```

- [ ] **Step 3: Service 注册成功后调 publishEvent**

```java
// 在渠道注册事务提交前
publisher.publishEvent(new ChannelRegisteredEvent(this,
    newChannel.getChannelId(),
    newChannel.getPhone(),
    req.getBusinessNo(),
    req.getInviteCode()
));
```

- [ ] **Step 4: 校验编译**

```bash
mvn -pl RuoYi-Vue/jst-user,RuoYi-Vue/jst-organizer,RuoYi-Vue/jst-channel,RuoYi-Vue/ruoyi-admin -am clean compile -DskipTests -q
```

- [ ] **Step 5: 提交**

```bash
git commit -m "feat(user): 渠道注册 DTO 加 businessNo/inviteCode + publish ChannelRegisteredEvent"
```

---

## Task 9: 小程序前端 — 邀请码 + 邀请列表 + 分销收益

**Files:**
- Create: `jst-uniapp/api/invite.js`
- Create: `jst-uniapp/pages-sub/channel/invite-code.vue`
- Create: `jst-uniapp/pages-sub/channel/invite-list.vue`
- Create: `jst-uniapp/pages-sub/channel/distribution.vue`
- Modify: `jst-uniapp/pages.json`（注册新页面）

- [ ] **Step 1: API**

```javascript
// jst-uniapp/api/invite.js
import request from './request'

export const getMyInviteCode = () => request.get('/jst/wx/channel/invite/code')
export const listInvited = (params) => request.get('/jst/wx/channel/invite/list', params)
export const listDistributionLedger = (params) => request.get('/jst/wx/channel/distribution/ledger', params)
export const getDistributionSummary = () => request.get('/jst/wx/channel/distribution/summary')
```

- [ ] **Step 2: invite-code.vue**

```vue
<template>
  <view class="invite-code-page">
    <view class="card">
      <text class="title">我的邀请码</text>
      <view class="code-block">{{ inviteCode }}</view>
      
      <image v-if="qrcodeUrl" :src="qrcodeUrl" class="qrcode" mode="widthFix" />
      
      <button class="action-btn primary" @click="onShareWeChat">微信分享</button>
      <button class="action-btn" @click="onCopyLink">复制邀请链接</button>
    </view>
    
    <view class="rules-card">
      <text class="rules-title">分销规则</text>
      <text class="rules-text">
        1. 邀请新渠道方注册成功后，您将获得其每笔订单的分销提成
        2. 默认分销费率 1.5%（具体以您与平台约定为准）
        3. 售后期满 7 天后提成自动入账，可在收益页提现
      </text>
    </view>
  </view>
</template>

<script>
import { getMyInviteCode } from '@/api/invite'

export default {
  data() {
    return { inviteCode: '', qrcodeUrl: '' }
  },
  onLoad() { this.load() },
  methods: {
    async load() {
      const res = await getMyInviteCode()
      this.inviteCode = res.data.inviteCode
      this.qrcodeUrl = res.data.qrcodeUrl  // 后端返回小程序码 OSS URL
    },
    onShareWeChat() {
      uni.share({
        provider: 'weixin',
        scene: 'WXSceneSession',
        type: 0,
        title: '邀请您加入竞赛通渠道',
        summary: `邀请码：${this.inviteCode}`,
        imageUrl: this.qrcodeUrl
      })
    },
    onCopyLink() {
      const link = `https://jstapp.esget.cn/wx/channel/register?invite_code=${this.inviteCode}`
      uni.setClipboardData({ data: link, success: () => uni.showToast({ title: '已复制' }) })
    }
  },
  // 微信原生分享
  onShareAppMessage() {
    return {
      title: '邀请您加入竞赛通渠道',
      path: `/pages-sub/public/channel-register?invite_code=${this.inviteCode}`,
      imageUrl: this.qrcodeUrl
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/tokens.scss';
.invite-code-page { padding: $jst-space-4; }
.card { background: $jst-bg-card; border-radius: $jst-radius-lg; padding: $jst-space-6; text-align: center; }
.title { font-size: $jst-font-lg; color: $jst-text-primary; }
.code-block { font-size: 32rpx; font-weight: bold; letter-spacing: 4rpx; margin: $jst-space-4 0;
              padding: $jst-space-3; background: $jst-bg-emphasis; border-radius: $jst-radius-md;
              color: $jst-brand; }
.qrcode { width: 400rpx; margin: $jst-space-4 auto; display: block; }
.action-btn { margin-top: $jst-space-3; }
.action-btn.primary { background: $jst-brand; color: #fff; }
.rules-card { margin-top: $jst-space-4; padding: $jst-space-4; background: $jst-bg-card;
              border-radius: $jst-radius-lg; }
.rules-title { font-size: $jst-font-md; color: $jst-text-primary; font-weight: 500; }
.rules-text { font-size: $jst-font-sm; color: $jst-text-secondary; line-height: 1.7; margin-top: $jst-space-2; }
</style>
```

- [ ] **Step 3: invite-list.vue**

```vue
<template>
  <view class="invite-list-page">
    <view class="stats-bar">
      <view class="stat-item">
        <text class="stat-num">{{ total }}</text>
        <text class="stat-label">已邀请</text>
      </view>
      <view class="stat-item">
        <text class="stat-num">{{ activeCount }}</text>
        <text class="stat-label">活跃</text>
      </view>
    </view>

    <scroll-view scroll-y class="scroll-area">
      <view v-for="item in list" :key="item.inviteId" class="invite-item">
        <view class="left">
          <text class="channel-name">{{ item.inviteeChannelName }}</text>
          <text class="invite-time">{{ formatTime(item.invitedAt) }}</text>
        </view>
        <view class="right">
          <text class="rate">{{ formatRate(item.commissionRate) }}</text>
          <text class="status" :class="item.status">{{ statusLabel(item.status) }}</text>
        </view>
      </view>
      <jst-empty v-if="!list.length" text="还没有邀请的渠道" />
    </scroll-view>
  </view>
</template>

<script>
import { listInvited } from '@/api/invite'
export default {
  data() { return { list: [], total: 0, activeCount: 0 } },
  onLoad() { this.load() },
  methods: {
    async load() {
      const res = await listInvited()
      this.list = res.rows || []
      this.total = this.list.length
      this.activeCount = this.list.filter(x => x.status === 'active').length
    },
    formatTime(t) { return t ? new Date(t).toLocaleDateString() : '' },
    formatRate(r) { return r ? (r * 100).toFixed(1) + '%' : '默认' },
    statusLabel(s) { return s === 'active' ? '生效中' : '已解绑' }
  }
}
</script>
```

- [ ] **Step 4: distribution.vue**

```vue
<template>
  <view class="distribution-page">
    <view class="summary-card">
      <view class="summary-item">
        <text class="summary-num">¥{{ total.totalAmount || '0.00' }}</text>
        <text class="summary-label">累计分销收益</text>
      </view>
      <view class="summary-item">
        <text class="summary-num">¥{{ total.pendingAmount || '0.00' }}</text>
        <text class="summary-label">待入账</text>
      </view>
      <view class="summary-item">
        <text class="summary-num">¥{{ total.accruedAmount || '0.00' }}</text>
        <text class="summary-label">可提现</text>
      </view>
    </view>

    <view class="action-bar">
      <button class="btn-withdraw" @click="onWithdraw">提现</button>
    </view>

    <view class="ledger-list">
      <view class="ledger-item" v-for="item in list" :key="item.ledgerId">
        <view class="ledger-top">
          <text class="ledger-from">来源：{{ item.inviteeChannelName }}</text>
          <text class="ledger-amount">+¥{{ item.amount }}</text>
        </view>
        <view class="ledger-bottom">
          <text class="ledger-order">订单 #{{ item.orderNo }}</text>
          <text class="ledger-status" :class="item.status">{{ statusLabel(item.status) }}</text>
        </view>
      </view>
      <jst-empty v-if="!list.length" text="暂无分销收益" />
    </view>
  </view>
</template>

<script>
import { listDistributionLedger, getDistributionSummary } from '@/api/invite'
export default {
  data() { return { total: {}, list: [] } },
  onLoad() { this.load() },
  methods: {
    async load() {
      const sum = await getDistributionSummary()
      this.total = sum.data || {}
      const ledger = await listDistributionLedger({ pageNum: 1, pageSize: 50 })
      this.list = ledger.rows || []
    },
    onWithdraw() {
      uni.navigateTo({ url: '/pages-sub/channel/withdraw' })  // 复用现有提现页（C5b）
    },
    statusLabel(s) {
      return { pending: '待入账', accrued: '可提现', settled: '已结算', paid: '已打款', cancelled: '已撤销' }[s] || s
    }
  }
}
</script>
```

- [ ] **Step 5: pages.json 注册**

```json
{
  "subPackages": [{
    "root": "pages-sub",
    "pages": [
      // 既有 ...
      { "path": "channel/invite-code", "style": { "navigationBarTitleText": "我的邀请码" } },
      { "path": "channel/invite-list", "style": { "navigationBarTitleText": "我邀请的渠道" } },
      { "path": "channel/distribution", "style": { "navigationBarTitleText": "分销收益" } }
    ]
  }]
}
```

并在小程序"我的"页面（渠道方视角）加 3 个入口卡片或菜单项跳转到这些页面。

- [ ] **Step 6: 后端 WxChannelInviteController**

```java
@RestController
@RequestMapping("/jst/wx/channel")
@PreAuthorize("@ss.hasRole('jst_channel')")
public class WxChannelInviteController extends BaseController {

    @Autowired private ChannelInviteService inviteService;
    @Autowired private ChannelDistributionService distributionService;
    @Autowired private InviteCodeService inviteCodeService;
    @Autowired private JstChannelMapper channelMapper;

    @GetMapping("/invite/code")
    public AjaxResult myCode() {
        Long channelId = JstLoginContext.currentChannelId();
        JstChannel c = channelMapper.selectJstChannelByChannelId(channelId);
        Map<String, Object> data = new HashMap<>();
        data.put("inviteCode", c.getInviteCode());
        // qrcodeUrl 走小程序码生成（复用既有 cert / qrcode 服务）
        data.put("qrcodeUrl", null);  // 留接口
        return AjaxResult.success(data);
    }

    @GetMapping("/invite/list")
    public TableDataInfo invitedList() {
        Long channelId = JstLoginContext.currentChannelId();
        startPage();
        return getDataTable(inviteService.listByInviter(channelId));
    }

    @GetMapping("/distribution/ledger")
    public TableDataInfo distributionLedger(@RequestParam(required = false) String status) {
        Long channelId = JstLoginContext.currentChannelId();
        startPage();
        return getDataTable(distributionService.listByInviter(channelId, status));
    }

    @GetMapping("/distribution/summary")
    public AjaxResult distributionSummary() {
        Long channelId = JstLoginContext.currentChannelId();
        return AjaxResult.success(distributionService.aggregateByMonth(channelId));
    }
}
```

- [ ] **Step 7: 提交**

```bash
git add jst-uniapp/api/invite.js jst-uniapp/pages-sub/channel/invite*.vue \
        jst-uniapp/pages-sub/channel/distribution.vue jst-uniapp/pages.json \
        RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/controller/wx/WxChannelInviteController.java
git commit -m "feat(mp+channel): 小程序渠道分销 3 页 + WxChannelInviteController API"
```

---

## Task 10: 小程序渠道注册回填 invite_code

**Files:**
- Modify: `jst-uniapp/pages-sub/public/partner-apply.vue`（或对应渠道注册页）

> 渠道方注册入口可能是 `partner-apply.vue` 或专门的 `channel-register.vue`。本计划假设是后者；如果前者承担渠道注册职责，则修改前者。

- [ ] **Step 1: 在 onLoad 时拿 invite_code（来自分享链接 query）**

```javascript
onLoad(options) {
  if (options.invite_code) {
    this.form.inviteCode = options.invite_code
    uni.showToast({ title: '已自动填写邀请码', icon: 'none' })
  }
}
```

- [ ] **Step 2: 表单加 inviteCode（可见但只读 if 已自动填）+ businessNo（选填，文案"如有销售对接，请填写销售给的业务编号"）**

```vue
<view class="form-section">
  <view class="form-item">
    <text class="label">邀请码</text>
    <input v-model="form.inviteCode" placeholder="选填" :disabled="hasInviteFromShare" />
  </view>
  <view class="form-item">
    <text class="label">销售业务编号</text>
    <input v-model="form.businessNo" placeholder="如有销售对接，请填写销售给的编号" />
    <text class="hint">如未对接销售，可留空</text>
  </view>
</view>
```

- [ ] **Step 3: 提交**

```bash
git commit -m "feat(mp): 渠道注册页接入 invite_code（分享自动回填）+ business_no 字段"
```

---

## Task 11: AdminSalesDashboardService + Controller + 前端业绩看板页

**Files:**
- Create: `service/AdminSalesDashboardService.java` + impl
- Create: `controller/admin/AdminSalesDashboardController.java`
- Create: `RuoYi-Vue/ruoyi-ui/src/api/admin/sales/dashboard.js`
- Create: `RuoYi-Vue/ruoyi-ui/src/views/jst/sales/dashboard.vue`

- [ ] **Step 1: Service**

```java
public interface AdminSalesDashboardService {
    /** 全员业绩 + 排行榜 + 提成成本 + 跟进活跃度 */
    DashboardOverviewVO getOverview(String month);
    /** 单个销售明细 */
    SalesDetailedPerformanceVO getSalesDetail(Long salesId, String month);
    /** 跟进活跃度 ranking（销售视角） */
    List<FollowupActivityVO> rankFollowupActivity(String month);
}
```

聚合 service 调用 SalesService、SalesCommissionService、SalesFollowupService 等。

- [ ] **Step 2: Controller**

```java
@RestController
@RequestMapping("/admin/sales/dashboard")
@PreAuthorize("@ss.hasAnyRoles('admin,jst_operator,jst_finance,jst_analyst')")
public class AdminSalesDashboardController extends BaseController {
    @Autowired private AdminSalesDashboardService dashboardService;

    @GetMapping("/overview")
    public AjaxResult overview(@RequestParam(required = false) String month) {
        return AjaxResult.success(dashboardService.getOverview(month));
    }

    @GetMapping("/sales/{salesId}")
    public AjaxResult salesDetail(@PathVariable Long salesId, @RequestParam(required = false) String month) {
        return AjaxResult.success(dashboardService.getSalesDetail(salesId, month));
    }

    @GetMapping("/followup-activity")
    public AjaxResult followupRanking(@RequestParam(required = false) String month) {
        return AjaxResult.success(dashboardService.rankFollowupActivity(month));
    }
}
```

- [ ] **Step 3: 前端 dashboard.vue（echarts 图表 + 排行榜）**

```vue
<template>
  <div class="sales-dashboard">
    <el-form :inline="true">
      <el-form-item label="月份">
        <el-date-picker v-model="month" type="month" value-format="yyyy-MM" @change="load" />
      </el-form-item>
    </el-form>

    <el-row :gutter="20">
      <el-col :span="6"><el-card><el-statistic title="销售在职" :value="overview.activeSalesCount" /></el-card></el-col>
      <el-col :span="6"><el-card><el-statistic title="本月成交订单" :value="overview.monthOrderCount" /></el-card></el-col>
      <el-col :span="6"><el-card><el-statistic title="本月 GMV" prefix="¥" :value="overview.monthGmv" /></el-card></el-col>
      <el-col :span="6"><el-card><el-statistic title="本月提成成本" prefix="¥" :value="overview.monthCommissionCost" /></el-card></el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <div slot="header">销售业绩排行</div>
          <el-table :data="overview.salesRanking" max-height="400">
            <el-table-column prop="salesName" label="销售" />
            <el-table-column prop="orderCount" label="笔数" align="right" />
            <el-table-column prop="gmv" label="GMV" align="right">
              <template slot-scope="s">¥{{ s.row.gmv }}</template>
            </el-table-column>
            <el-table-column prop="commission" label="提成" align="right">
              <template slot-scope="s">¥{{ s.row.commission }}</template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="s">
                <el-button size="mini" @click="viewDetail(s.row.salesId)">明细</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header">跟进活跃度</div>
          <div ref="followupChartRef" style="height: 400px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getOverview, getFollowupActivity } from '@/api/admin/sales/dashboard'
export default {
  data() {
    return {
      month: new Date().toISOString().slice(0, 7),
      overview: {},
      activity: []
    }
  },
  created() { this.load() },
  methods: {
    async load() {
      const ov = await getOverview(this.month)
      this.overview = ov.data
      const act = await getFollowupActivity(this.month)
      this.activity = act.data
      this.$nextTick(() => this.renderChart())
    },
    renderChart() {
      const c = echarts.init(this.$refs.followupChartRef)
      c.setOption({
        xAxis: { type: 'category', data: this.activity.map(x => x.salesName) },
        yAxis: { type: 'value' },
        series: [{ type: 'bar', data: this.activity.map(x => x.followupCount), itemStyle: { color: '#409eff' } }]
      })
    },
    viewDetail(salesId) { this.$router.push(`/jst/sales/${salesId}/detail`) }
  }
}
</script>
```

- [ ] **Step 4: 提交**

```bash
git commit -m "feat(channel+ui): admin 销售业绩看板 (overview+排行+跟进活跃度 echarts)"
```

---

## Task 12: AdminSettlementController + 前端月结审核 2 页

**Files:**
- Create: `controller/admin/AdminSettlementController.java`
- Create: `RuoYi-Vue/ruoyi-ui/src/api/admin/sales/settlement.js`
- Create: `RuoYi-Vue/ruoyi-ui/src/views/jst/sales/settlement/index.vue`
- Create: `RuoYi-Vue/ruoyi-ui/src/views/jst/sales/settlement/detail.vue`

- [ ] **Step 1: Controller**

```java
@RestController
@RequestMapping("/admin/sales/settlement")
@PreAuthorize("@ss.hasAnyRoles('admin,jst_operator,jst_finance')")
public class AdminSettlementController extends BaseController {
    @Autowired private SalesCommissionSettlementService settlementService;

    @GetMapping("/list")
    public TableDataInfo list(SettlementQueryReqDTO query) {
        startPage();
        return getDataTable(settlementService.listForAdmin(query));
    }

    @GetMapping("/{settlementId}")
    public AjaxResult detail(@PathVariable Long settlementId) {
        return AjaxResult.success(settlementService.getDetailWithLedgers(settlementId));
    }

    @PostMapping("/{settlementId}/approve")
    @Log(title = "月结单审核", businessType = BusinessType.UPDATE)
    public AjaxResult approve(@PathVariable Long settlementId) {
        settlementService.approve(settlementId, SecurityUtils.getUserId());
        return AjaxResult.success();
    }

    @PostMapping("/{settlementId}/reject")
    @Log(title = "月结单驳回", businessType = BusinessType.UPDATE)
    public AjaxResult reject(@PathVariable Long settlementId, @RequestBody Map<String, String> body) {
        settlementService.reject(settlementId, body.getOrDefault("reason", ""));
        return AjaxResult.success();
    }

    @PostMapping("/{settlementId}/pay-record")
    @Log(title = "录入打款流水", businessType = BusinessType.UPDATE)
    public AjaxResult recordPayment(@PathVariable Long settlementId, @RequestBody Map<String, String> body) {
        settlementService.recordPayment(settlementId, body.getOrDefault("voucher", ""));
        return AjaxResult.success();
    }
}
```

- [ ] **Step 2: 前端 settlement/index.vue**（列表页）

```vue
<template>
  <div>
    <el-form :inline="true">
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable @change="load">
          <el-option label="待审核" value="pending_review" />
          <el-option label="已审核" value="approved" />
          <el-option label="已打款" value="paid" />
          <el-option label="驳回" value="rejected" />
        </el-select>
      </el-form-item>
      <el-form-item label="月份">
        <el-date-picker v-model="query.period" type="month" value-format="yyyy-MM" @change="load" />
      </el-form-item>
    </el-form>

    <el-table :data="list">
      <el-table-column prop="salesName" label="销售" />
      <el-table-column prop="period" label="月份" />
      <el-table-column prop="totalCount" label="笔数" align="right" />
      <el-table-column prop="totalAmount" label="金额" align="right">
        <template slot-scope="s">¥{{ s.row.totalAmount }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template slot-scope="s">
          <el-tag :type="statusType(s.row.status)">{{ statusLabel(s.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="s">
          <el-button size="mini" @click="viewDetail(s.row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination ... />
  </div>
</template>
```

- [ ] **Step 3: settlement/detail.vue**（详情 + 审核操作）

```vue
<template>
  <div>
    <el-page-header @back="$router.back()" content="月结单详情" />
    
    <el-descriptions :column="3" border style="margin-top: 20px">
      <el-descriptions-item label="销售">{{ data.salesName }}</el-descriptions-item>
      <el-descriptions-item label="月份">{{ data.period }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="statusType(data.status)">{{ statusLabel(data.status) }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="笔数">{{ data.totalCount }}</el-descriptions-item>
      <el-descriptions-item label="金额">¥{{ data.totalAmount }}</el-descriptions-item>
      <el-descriptions-item v-if="data.payVoucher" label="打款流水">{{ data.payVoucher }}</el-descriptions-item>
    </el-descriptions>

    <el-card style="margin-top: 20px">
      <div slot="header">提成明细 ({{ data.ledgers?.length || 0 }} 笔)</div>
      <el-table :data="data.ledgers">
        <el-table-column prop="orderNo" label="订单" />
        <el-table-column prop="channelName" label="渠道" />
        <el-table-column prop="businessType" label="业务类型" />
        <el-table-column label="基数 / 费率 / 金额" align="right">
          <template slot-scope="s">¥{{ s.row.baseAmount }} × {{ formatRate(s.row.appliedRate) }} = ¥{{ s.row.amount }}</template>
        </el-table-column>
      </el-table>
    </el-card>

    <div style="margin-top: 20px; text-align: right" v-if="data.status === 'pending_review'">
      <el-button @click="onReject">驳回</el-button>
      <el-button type="primary" @click="onApprove">审核通过</el-button>
    </div>
    <div style="margin-top: 20px; text-align: right" v-if="data.status === 'approved'">
      <el-button type="primary" @click="onPayRecord">录入打款流水</el-button>
    </div>
  </div>
</template>

<script>
import { getDetail, approve, reject, recordPayment } from '@/api/admin/sales/settlement'
export default {
  data() { return { data: {} } },
  async created() {
    const res = await getDetail(this.$route.params.id)
    this.data = res.data
  },
  methods: {
    async onApprove() {
      await this.$confirm('确认审核通过？', '提示', { type: 'success' })
      await approve(this.data.settlementId)
      this.$message.success('已通过')
      this.$router.back()
    },
    async onReject() {
      const { value } = await this.$prompt('请输入驳回原因', '驳回', { type: 'warning' })
      await reject(this.data.settlementId, value)
      this.$router.back()
    },
    async onPayRecord() {
      const { value } = await this.$prompt('请输入打款流水号', '录入打款', {})
      await recordPayment(this.data.settlementId, value)
      this.$router.back()
    },
    formatRate(r) { return r ? (r * 100).toFixed(2) + '%' : '' },
    statusType(s) { return { pending_review: 'warning', approved: 'primary', paid: 'success', rejected: 'danger' }[s] },
    statusLabel(s) { return { pending_review: '待审核', approved: '已审核', paid: '已打款', rejected: '驳回' }[s] }
  }
}
</script>
```

- [ ] **Step 4: 提交**

```bash
git commit -m "feat(channel+ui): admin 月结审核 列表+详情 2 页 + 审核/驳回/录入打款 端点"
```

---

## Task 13: AdminConflictController + AdminBindingController + admin 销售管理 + 团队管理页

**Files:**
- Create: 2 个 admin Controller
- Create: 4 个前端页面（admin 销售管理 / 冲突队列 / 团队管理 / 业绩明细）

- [ ] **Step 1: AdminConflictController + AdminBindingController（精简版）**

```java
@RestController
@RequestMapping("/admin/sales/binding")
@PreAuthorize("@ss.hasAnyRoles('admin,jst_operator')")
public class AdminBindingController extends BaseController {
    @Autowired private SalesChannelBindingService bindingService;
    @Autowired private SalesService salesService;

    @PostMapping("/manual")
    @Log(title = "手动绑定渠道-销售", businessType = BusinessType.UPDATE)
    public AjaxResult manualBind(@RequestBody Map<String, Object> body) {
        Long channelId = Long.valueOf(body.get("channelId").toString());
        Long salesId = Long.valueOf(body.get("salesId").toString());
        String remark = (String) body.get("remark");
        bindingService.bindOrTransfer(channelId, salesId, "manual", remark, SecurityUtils.getUserId());
        return AjaxResult.success();
    }
}

@RestController
@RequestMapping("/admin/sales/conflict")
@PreAuthorize("@ss.hasAnyRoles('admin,jst_operator')")
public class AdminConflictController extends BaseController {
    @Autowired private JstSalesAttributionConflictMapper conflictMapper;

    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String status) {
        startPage();
        // mapper 加 selectByStatus
        return getDataTable(conflictMapper.selectByStatus(status == null ? "pending" : status));
    }

    @PostMapping("/{conflictId}/resolve")
    public AjaxResult resolve(@PathVariable Long conflictId, @RequestBody Map<String, Object> body) {
        // 设 status='resolved' + resolved_by + resolution_remark
        // 可选：直接执行 bindingService.bindOrTransfer 把渠道绑给某销售
        return AjaxResult.success();
    }
}
```

- [ ] **Step 2: 前端 admin 销售管理 index.vue**

页面包含：
- 顶部筛选（状态 / 主管 / 关键字）
- 列表（销售号 / 姓名 / 主管 / 状态 / 默认费率 / 名下渠道数 / 操作）
- 操作按钮：编辑费率 / 设置主管 / 申请离职 / 立即停权 / 终结过渡
- 创建按钮

```vue
<template>
  <div>
    <el-form :inline="true">
      <el-form-item label="状态"><el-select v-model="query.status" clearable @change="load">
        <el-option label="在职" value="active" />
        <el-option label="申请离职" value="resign_apply" />
        <el-option label="过渡期" value="resigned_pending_settle" />
        <el-option label="已离职" value="resigned_final" />
      </el-select></el-form-item>
      <el-form-item><el-input v-model="query.salesName" placeholder="姓名" /></el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" @click="load">查询</el-button>
        <el-button type="primary" icon="el-icon-plus" @click="onCreate">新建</el-button></el-form-item>
    </el-form>

    <el-table :data="list">
      <el-table-column prop="salesNo" label="编号" width="120" />
      <el-table-column prop="salesName" label="姓名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="managerName" label="主管" />
      <el-table-column label="状态">
        <template slot-scope="s"><el-tag :type="statusType(s.row.status)">{{ statusLabel(s.row.status) }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="commissionRateDefault" label="费率">
        <template slot-scope="s">{{ formatRate(s.row.commissionRateDefault) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template slot-scope="s">
          <el-button size="mini" @click="onEditRate(s.row)">改费率</el-button>
          <el-button size="mini" @click="onSetManager(s.row)">主管</el-button>
          <el-dropdown @command="cmd => onAction(s.row, cmd)">
            <el-button size="mini" type="text">更多<i class="el-icon-arrow-down"></i></el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item v-if="s.row.status === 'active'" command="resign-apply">申请离职</el-dropdown-item>
              <el-dropdown-item v-if="s.row.status === 'resign_apply'" command="resign-execute">立即执行</el-dropdown-item>
              <el-dropdown-item v-if="s.row.status === 'resigned_pending_settle'" command="end-transition">终结过渡</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>
    <!-- 创建/编辑/状态切换的弹层组件略 -->
  </div>
</template>
```

- [ ] **Step 3: 前端 admin 冲突队列 conflict.vue**（按需精简）
- [ ] **Step 4: 前端 销售主管 团队管理 team/index.vue**

```vue
<template>
  <div>
    <el-form :inline="true">
      <el-form-item label="切换查看">
        <el-select v-model="selectedSalesId" @change="load">
          <el-option label="全部下属合并" value="" />
          <el-option v-for="s in subordinates" :key="s.salesId" :label="s.salesName" :value="s.salesId" />
        </el-select>
      </el-form-item>
    </el-form>

    <el-table :data="performance">
      <el-table-column prop="salesName" label="销售" />
      <el-table-column prop="orderCount" label="笔数" />
      <el-table-column prop="gmv" label="GMV"><template slot-scope="s">¥{{ s.row.gmv }}</template></el-table-column>
      <el-table-column prop="commission" label="提成"><template slot-scope="s">¥{{ s.row.commission }}</template></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="s">
          <el-button size="mini" @click="viewDetail(s.row.salesId)">查看明细</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
```

- [ ] **Step 5: 提交**

```bash
git commit -m "feat(channel+ui): admin 销售管理+冲突队列+主管团队管理 页面 + 后端端点"
```

---

## Task 14: AC1 手机号脱敏端到端验证 + AC3 Excel 水印实际插入

**Files:**
- Modify: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/controller/sales/SalesChannelController.java`（销售看渠道详情接口，VO 加 @Sensitive(phone=true) 字段）
- Create: `service/SalesChannelService.java`（封装查渠道 + 写审计日志）
- Create: `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/util/SalesExportUtils.java`（实际生成水印 Excel）

- [ ] **Step 1: 销售看渠道详情接口（VO + Controller）**

```java
public class SalesChannelDetailVO {
    private Long channelId;
    private String channelName;
    @Sensitive(phone = true)  // MaskSalaryAspect 自动脱敏
    private String phone;
    private String status;
    @Sensitive(idcard = true)
    private String legalIdcard;
    // ... 其他字段
}

@RestController
@RequestMapping("/sales/me/channels")
public class SalesChannelController extends BaseSalesController {
    @Autowired private SalesChannelService channelService;

    @GetMapping
    public TableDataInfo list(SalesChannelQueryReqDTO query) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        startPage();
        return getDataTable(channelService.listMine(salesId, query));
    }

    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable Long id) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        return AjaxResult.success(channelService.getDetailMine(id, salesId));
    }

    @GetMapping("/{id}/phone-full")
    public AjaxResult phoneFull(@PathVariable Long id) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        // 写审计日志（business_type=SALES_VIEW_PHONE）
        operLogService.recordSalesViewPhone(salesId, id);
        String phone = channelService.getFullPhone(id, salesId);
        return AjaxResult.success(phone);
    }
}
```

> ⚠️ AC1 要求"所有销售"看渠道详情都常态脱敏。这通过 `@Sensitive(phone=true)` 字段 + plan-01 已实现的 `MaskSalaryAspect` 完成。`MaskSalaryAspect.mask()` 方法已经判断"如果是 jst_sales 角色就脱敏 phone"。所以本步骤主要是"在 VO 上正确加注解"+"提供 phone-full 端点 + 审计日志"。

- [ ] **Step 2: SalesExportUtils（Excel 水印插入）**

```java
package com.ruoyi.jst.common.util;

import com.ruoyi.jst.common.interceptor.SalesExportWatermarkInterceptor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SalesExportUtils {

    /**
     * 在 Excel 第一行插入水印行（销售姓名 + 工号 + 时间），下面才是真实数据。
     */
    public static void writeWatermarkedExcel(
        OutputStream os,
        String salesName, String userNo, LocalDateTime now,
        List<String> headers, List<Map<String, Object>> rows
    ) throws Exception {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("data");

            // 第 0 行：水印
            Row wmRow = sheet.createRow(0);
            Cell wmCell = wmRow.createCell(0);
            wmCell.setCellValue(SalesExportWatermarkInterceptor.buildWatermarkText(salesName, userNo, now));
            CellStyle wmStyle = wb.createCellStyle();
            Font wmFont = wb.createFont();
            wmFont.setItalic(true);
            wmFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            wmStyle.setFont(wmFont);
            wmCell.setCellStyle(wmStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, headers.size() - 1));

            // 第 1 行：表头
            Row headerRow = sheet.createRow(1);
            for (int i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }

            // 数据
            for (int r = 0; r < rows.size(); r++) {
                Row dataRow = sheet.createRow(r + 2);
                for (int c = 0; c < headers.size(); c++) {
                    Object v = rows.get(r).get(headers.get(c));
                    dataRow.createCell(c).setCellValue(v == null ? "" : v.toString());
                }
            }
            wb.write(os);
        }
    }
}
```

- [ ] **Step 3: 在销售可触发的导出接口实际调用**

例：销售导出"我的渠道列表"
```java
@GetMapping("/export")
public void exportChannels(HttpServletResponse resp) throws Exception {
    Long salesId = SalesScopeUtils.currentSalesIdRequired();
    LoginUser u = SalesScopeUtils.getLoginUserQuietly();
    String name = u.getUser().getNickName();
    String userNo = String.valueOf(u.getUser().getUserId());

    List<SalesChannelDetailVO> rows = channelService.listMine(salesId, null);
    List<String> headers = List.of("渠道名", "联系电话", "状态", "注册时间");
    List<Map<String, Object>> data = rows.stream().map(r -> Map.of(
        "渠道名", r.getChannelName(),
        "联系电话", r.getPhone(),    // 此处已被 MaskSalaryAspect 脱敏
        "状态", r.getStatus(),
        "注册时间", r.getCreateTime()
    )).collect(Collectors.toList());

    resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    resp.setHeader("Content-Disposition", "attachment;filename=\"my-channels.xlsx\"");
    SalesExportUtils.writeWatermarkedExcel(resp.getOutputStream(), name, userNo, LocalDateTime.now(),
        headers, data);
}
```

- [ ] **Step 4: 提交**

```bash
git commit -m "feat(common+channel): AC1 渠道详情手机号脱敏端到端 + AC3 Excel 水印实际插入工具"
```

---

## Task 15: AdminAuditController（脱敏审计 + 导出审计）+ 前端审计页

**Files:**
- Create: `controller/admin/AdminAuditController.java`
- Create: `RuoYi-Vue/ruoyi-ui/src/views/jst/sales/audit.vue`

- [ ] **Step 1: Controller**

```java
@RestController
@RequestMapping("/admin/sales/audit")
@PreAuthorize("@ss.hasAnyRoles('admin,jst_operator,jst_risk')")
public class AdminAuditController extends BaseController {
    @Autowired private com.ruoyi.system.service.ISysOperLogService operLogService;

    @GetMapping("/phone-views")
    public TableDataInfo phoneViews(@RequestParam(required = false) Long salesId, @RequestParam(required = false) String dateFrom) {
        // 查 sys_oper_log WHERE business_type='SALES_VIEW_PHONE' 排序
        startPage();
        // operLogService.selectByBusinessType("SALES_VIEW_PHONE", filters)
        return getDataTable(/* result */);
    }

    @GetMapping("/exports")
    public TableDataInfo exports(@RequestParam(required = false) Long salesId) {
        startPage();
        // 查 sys_oper_log WHERE business_type='SALES_EXPORT' or oper_url LIKE '/sales/%/export'
        return getDataTable(/* result */);
    }
}
```

- [ ] **Step 2: audit.vue（双 Tab）**

```vue
<template>
  <div>
    <el-tabs v-model="activeTab" @tab-click="load">
      <el-tab-pane label="手机号查看记录" name="phone" />
      <el-tab-pane label="导出操作记录" name="export" />
    </el-tabs>

    <el-table :data="list">
      <el-table-column prop="operName" label="操作人" />
      <el-table-column prop="operTime" label="时间" width="180" />
      <el-table-column prop="operParam" label="参数" />
      <el-table-column prop="operIp" label="IP" />
    </el-table>
    <pagination ... />
  </div>
</template>
```

- [ ] **Step 3: 提交**

```bash
git commit -m "feat(channel+ui): AdminAuditController 脱敏/导出审计查询 + audit.vue 双 Tab 页"
```

---

## Task 16: 全量 build + 启动验证 + 端到端 smoke

- [ ] **Step 1: Backend 编译**

```bash
cd D:/coding/jst_v1/RuoYi-Vue
mvn clean compile -DskipTests -q
mvn -pl jst-channel test -q
```
Expected: 全绿。

- [ ] **Step 2: Frontend 构建**

```bash
cd D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui
npm run build:prod
```

- [ ] **Step 3: jst-uniapp 编译验证**

用 HBuilderX 打开 `jst-uniapp/`，预览到微信开发者工具，验证 invite-code / invite-list / distribution 3 页面无 JS 错误。

- [ ] **Step 4: 端到端 smoke 流程**

启动 ruoyi-admin + ruoyi-ui + 微信开发者工具：

1. **流程 A — 销售开拓 + 自动绑定**
   - admin 创建销售 S1（jst_sales 角色）
   - S1 登录 Web 端 → 我的预录入 → 新建预录入 phone=13900001234, target=北京海淀新东方
   - 用一个新手机号 13900001234 在小程序注册渠道方 → 自动绑定到 S1
   - S1 看"我的渠道"出现新渠道
   
2. **流程 B — 学生报名 → 提成入账**
   - 该渠道的某个学生在小程序报名一个 100 元赛事
   - 触发 OrderPaidEvent → SalesCommissionListener → 写 ledger（pending）
   - 等待 7 天（或手动调小 sysParam 售后期为 0 立即触发）
   - Quartz Accrue Job 跑后 → ledger status='accrued'
   - 月初 → SalesMonthlySettlementJob → settlement 'pending_review'
   - admin 在 Web "月结审核"通过 → 录流水 → 完成
   
3. **流程 C — 渠道分销链路**
   - 渠道 A 在小程序看自己的邀请码 → 分享给渠道 B
   - 渠道 B 通过分享链接注册 → 自动建立邀请关系（M1 互斥：A 没绑销售则建立；A 绑销售则跳过 + 写 attempted）
   - 渠道 B 学生报名 → 触发 ChannelDistributionService → 写分销 ledger
   - 渠道 A 在小程序看分销收益
   
4. **流程 D — 销售离职 3 阶段**
   - admin 给 S1 提交离职申请，预期日期=今天+1
   - S1 登录 → 我的渠道仍可看 + 跟进可写，但导出按钮被拦截 + 新建预录入返 403
   - 第二天 Quartz ResignExecuteJob 跑 → S1 账号停权 + 渠道转给主管
   - 3 个月后 Quartz TransitionEndJob 跑 → status='resigned_final'
   
5. **流程 E — 反带客户加固**
   - S1 登录看渠道详情 → phone 显示为 139****1234
   - 点"查看完整" → 弹出完整号码 + sys_oper_log 写一条 SALES_VIEW_PHONE
   - admin 在"脱敏审计"页能查到该记录
   - S1 导出渠道列表 → Excel 第一行有水印 "本表由销售[S1/工号]于[时间]导出..."

---

## Task 17: 更新 CLAUDE.md（最终版本）

- [ ] **Step 1: 修改 SALES-DISTRIBUTION 行**

将 SALES-DISTRIBUTION 行替换为：

`**P0 ✅** | SALES-DISTRIBUTION | ✅ 全部 4 计划完成 (2026-04-XX) | spec：docs/superpowers/specs/2026-04-18-sales-channel-distribution-design.md，4 计划：plan-01 (DDL+基础架构 9 表+12 Domain+SalesScope/Mask/Watermark)、plan-02 (销售核心 Service+提成管线+6 Quartz+离职 3 阶段)、plan-03 (CRM 跟进/标签/任务+销售工作台 7 页)、plan-04 (渠道分销+admin 后台 5 页+小程序 3 页+反带客户 AC1/AC3 加固)。详见 4 个 plan 文档的 commit。`

- [ ] **Step 2: Commit + merge**

```bash
git add CLAUDE.md
git commit -m "docs: CLAUDE.md SALES-DISTRIBUTION 4 计划全部完成"
git checkout main
git merge --no-ff feature/sales-distribution-04-distribution-admin
```

---

## 自检清单

- [ ] InviteCodeService 4 单测（生成 + 唯一性 + resolveInviter）
- [ ] ChannelInviteService 8 单测（M1 互斥 + 反环路 + admin 后门）
- [ ] ChannelDistributionService 6 单测（默认费率/自定义/官方豁免/封禁）
- [ ] SalesCommissionListener 重构为 prepare+persist + 与 distribution 共享 compress_ratio
- [ ] ChannelInviteBindingListener 订阅 ChannelRegisteredEvent (@Order 在 SalesAutoBindingListener 之后)
- [ ] ChannelDistributionAccrueJob (job_id=2008) 注册
- [ ] 渠道注册 ReqDTO 加 inviteCode/businessNo + publish event 完整
- [ ] 小程序 3 页 (invite-code/invite-list/distribution) + WxChannelInviteController API
- [ ] 渠道注册回填 invite_code 自动从分享链接取
- [ ] AdminSalesDashboardService + Controller + dashboard.vue (echarts)
- [ ] AdminSettlementController + 月结审核 2 页
- [ ] AdminConflictController + 冲突队列页
- [ ] AdminBindingController + admin 销售管理页（含离职 3 阶段操作）
- [ ] SalesManager 团队管理页
- [ ] AC1 渠道详情手机号脱敏 + phone-full 端点 + 审计日志
- [ ] AC3 SalesExportUtils Excel 水印 + 至少 1 个销售可触发的导出接口实际调用
- [ ] AdminAuditController + audit.vue 双 Tab
- [ ] mvn compile + npm build:prod + uniapp 编译三绿
- [ ] 端到端 5 个 smoke 流程全部通过
- [ ] CLAUDE.md 已更新为"全部 4 计划完成"

---

**End of Plan #4 (FINAL)**
