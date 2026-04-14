# REFACTOR-6-BIZ-NO — 统一编号规则引擎

> 优先级：P2 | 预估：M | Agent：Backend Agent
> 依赖：无（可独立开发）

---

## 一、背景

系统多个业务需要唯一编号（证书编号、渠道授权编号等），当前无统一规则。需要建立可配置的编号生成规则字典库。

## 二、DDL

```sql
-- 编号规则定义
CREATE TABLE IF NOT EXISTS jst_biz_no_rule (
  rule_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  rule_code     VARCHAR(50) NOT NULL UNIQUE COMMENT '规则编码: cert_no/channel_auth_no',
  rule_name     VARCHAR(100) NOT NULL COMMENT '规则名称',
  prefix        VARCHAR(20) DEFAULT '' COMMENT '前缀如 JST-CERT-',
  date_format   VARCHAR(20) DEFAULT 'yyyyMMdd' COMMENT '日期部分格式',
  seq_length    INT DEFAULT 4 COMMENT '序号位数',
  seq_start     BIGINT DEFAULT 1 COMMENT '起始序号',
  description   VARCHAR(500),
  status        TINYINT DEFAULT 1,
  create_by     VARCHAR(64) DEFAULT '',
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by     VARCHAR(64) DEFAULT '',
  update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务编号规则';

-- 编号序列（按日分配）
CREATE TABLE IF NOT EXISTS jst_biz_no_seq (
  rule_code     VARCHAR(50) NOT NULL,
  date_key      VARCHAR(20) NOT NULL COMMENT '日期键如 20260413',
  current_seq   BIGINT DEFAULT 0 COMMENT '当前序号',
  PRIMARY KEY (rule_code, date_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务编号序列';

-- 预置规则
INSERT INTO jst_biz_no_rule (rule_code, rule_name, prefix, date_format, seq_length, description)
VALUES
('cert_no', '证书编号', 'JST-CERT-', 'yyyyMMdd', 4, '证书唯一编号，如 JST-CERT-20260413-0001'),
('channel_auth_no', '渠道授权编号', 'JST-AUTH-', 'yyyyMMdd', 4, '渠道授权证书编号'),
('enroll_cert_no', '报名证书编号', 'JST-EC-', 'yyyyMMdd', 5, '报名通过后的参赛证编号');
```

## 三、后端实现

### 3.1 BizNoGenerateService

放在 `jst-common` 模块：

```java
@Service
public class BizNoGenerateService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private JstBizNoRuleMapper ruleMapper;
    @Autowired
    private JstBizNoSeqMapper seqMapper;

    /**
     * 生成下一个编号
     * 如：JST-CERT-20260413-0001
     */
    public String nextNo(String ruleCode) {
        JstBizNoRule rule = ruleMapper.selectByCode(ruleCode);
        if (rule == null || rule.getStatus() != 1) {
            throw new ServiceException("编号规则未配置: " + ruleCode);
        }
        
        String dateKey = DateUtils.dateTimeNow(rule.getDateFormat());
        String redisKey = "biz_no:" + ruleCode + ":" + dateKey;
        
        // Redis INCR 保证并发安全
        Long seq = redisTemplate.opsForValue().increment(redisKey);
        if (seq == 1) {
            // 首次，设置过期时间（48小时，防止日期切换后残留）
            redisTemplate.expire(redisKey, 48, TimeUnit.HOURS);
            // 同步写 DB（异步也可以）
        }
        
        // 格式化序号
        String seqStr = String.format("%0" + rule.getSeqLength() + "d", seq);
        return rule.getPrefix() + dateKey + "-" + seqStr;
    }
}
```

### 3.2 Domain/Mapper/Service

标准若依 CRUD 生成：
- JstBizNoRule / JstBizNoRuleMapper / JstBizNoRuleService
- JstBizNoSeq / JstBizNoSeqMapper

### 3.3 管理端接口

`GET/POST/PUT/DELETE /jst/admin/biz-no-rule` — 规则管理 CRUD

## 四、DoD

- [ ] 2 张表在测试库建表
- [ ] 预置 3 条规则
- [ ] BizNoGenerateService 实现
- [ ] Redis INCR 并发安全
- [ ] 管理端 CRUD 接口
- [ ] `mvn compile` 通过
- [ ] 报告交付
