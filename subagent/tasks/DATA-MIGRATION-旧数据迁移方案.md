# DATA-MIGRATION — 旧数据迁移方案与脚本

> 优先级：P1 | 预估：L | Agent：Backend Agent

---

## 一、背景

平台有历史运营数据（旧系统），需迁移到新系统。年参赛量约 100 万人，积累了大量用户、赛事、报名、成绩、证书等数据。需要编写安全的迁移方案和脚本。

## 二、迁移范围

### 2.1 需迁移的数据（按优先级）

| 优先级 | 表 | 预估数据量 | 说明 |
|---|---|---|---|
| **P0** | jst_user | 50~100 万 | 用户基础信息，手机号去重 |
| **P0** | jst_participant | 100~200 万 | 参赛人档案 |
| **P0** | jst_contest | 1,000~5,000 | 历史赛事 |
| **P0** | jst_enroll_record | 100~200 万 | 报名记录 |
| **P1** | jst_score_record | 50~100 万 | 成绩记录 |
| **P1** | jst_cert_record | 30~50 万 | 证书记录 |
| **P1** | jst_order_main + jst_order_item | 50~100 万 | 历史订单（只读归档） |
| **P2** | jst_channel + jst_student_channel_binding | 1~5 万 | 渠道和绑定关系 |
| **P2** | jst_points_account + jst_points_ledger | 50~100 万 | 积分数据 |

### 2.2 不迁移的数据

| 表 | 原因 |
|---|---|
| jst_refund_record | 历史退款已完成，无需迁移 |
| jst_rebate_ledger / jst_rebate_settlement | 返点重新计算 |
| jst_message | 历史消息无价值 |
| sys_* | 若依系统表，全新配置 |

## 三、迁移方案

### 3.1 总体策略

```
旧库 → ETL 脚本（Python/Java） → 中间 CSV/JSON → 批量 INSERT → 新库
                                    ↓
                               数据校验 → 对账报告
```

**不做直接库到库迁移**（新旧表结构不同，需要字段映射和清洗）。

### 3.2 迁移脚本框架

新建 `scripts/migration/` 目录：

```
scripts/migration/
├── config.py               # 新旧库连接配置
├── 01_migrate_users.py      # 用户迁移
├── 02_migrate_participants.py  # 参赛人迁移
├── 03_migrate_contests.py   # 赛事迁移
├── 04_migrate_enrolls.py    # 报名记录迁移
├── 05_migrate_scores.py     # 成绩迁移
├── 06_migrate_certs.py      # 证书迁移
├── 07_migrate_orders.py     # 订单迁移（只读归档）
├── 08_migrate_channels.py   # 渠道迁移
├── 09_migrate_points.py     # 积分迁移
├── verify.py                # 数据校验与对账
└── README.md                # 迁移执行手册
```

### 3.3 单表迁移脚本模板

```python
"""
迁移模板 — 分批读取旧库 → 字段映射 → 批量写入新库
"""
import pymysql
from config import OLD_DB, NEW_DB

BATCH_SIZE = 1000
SLEEP_MS = 100  # 每批间隔，避免锁表

def migrate():
    old_conn = pymysql.connect(**OLD_DB)
    new_conn = pymysql.connect(**NEW_DB)
    
    old_cursor = old_conn.cursor(pymysql.cursors.DictCursor)
    new_cursor = new_conn.cursor()
    
    # 1. 读取旧库总量
    old_cursor.execute("SELECT COUNT(*) as cnt FROM old_user_table")
    total = old_cursor.fetchone()['cnt']
    print(f"待迁移: {total} 条")
    
    # 2. 分批读取+写入
    offset = 0
    migrated = 0
    while offset < total:
        old_cursor.execute(f"SELECT * FROM old_user_table ORDER BY id LIMIT {BATCH_SIZE} OFFSET {offset}")
        rows = old_cursor.fetchall()
        if not rows:
            break
        
        values = []
        for row in rows:
            # 3. 字段映射与清洗
            mapped = {
                'user_id': row['id'],
                'openid': row.get('wechat_openid', ''),
                'mobile': clean_mobile(row.get('phone', '')),
                'nickname': row.get('name', ''),
                'user_type': 'student',
                'status': '0',  # 正常
                'create_time': row.get('created_at'),
                'del_flag': '0'
            }
            values.append(mapped)
        
        # 4. 批量 INSERT IGNORE（幂等，重复执行不报错）
        sql = """INSERT IGNORE INTO jst_user 
                 (user_id, openid, mobile, nickname, user_type, status, create_time, del_flag)
                 VALUES (%(user_id)s, %(openid)s, %(mobile)s, %(nickname)s, 
                         %(user_type)s, %(status)s, %(create_time)s, %(del_flag)s)"""
        new_cursor.executemany(sql, values)
        new_conn.commit()
        
        migrated += len(rows)
        offset += BATCH_SIZE
        print(f"进度: {migrated}/{total} ({migrated*100//total}%)")
        
        time.sleep(SLEEP_MS / 1000)
    
    print(f"迁移完成: {migrated} 条")
    
    # 5. 校验
    new_cursor.execute("SELECT COUNT(*) as cnt FROM jst_user WHERE del_flag = '0'")
    new_count = new_cursor.fetchone()[0]
    print(f"新库记录数: {new_count}")

def clean_mobile(phone):
    """手机号清洗：去空格、去国际区号、校验格式"""
    if not phone:
        return ''
    phone = phone.strip().replace(' ', '').replace('-', '')
    if phone.startswith('+86'):
        phone = phone[3:]
    if phone.startswith('86') and len(phone) == 13:
        phone = phone[2:]
    return phone if len(phone) == 11 and phone.startswith('1') else ''
```

### 3.4 数据清洗规则

| 字段 | 清洗规则 |
|---|---|
| 手机号 | 去空格/区号，11 位校验，无效置空 |
| 身份证 | 15 位转 18 位，校验位验证 |
| 姓名 | trim + 去除特殊字符 |
| 时间 | 统一为 `yyyy-MM-dd HH:mm:ss` |
| 金额 | 统一为分（Long），旧系统如果是元则 ×100 |
| 状态 | 映射表转换（旧系统状态码 → 新系统枚举值） |
| 重复数据 | 手机号重复用户合并（保留最新一条） |

### 3.5 ID 映射策略

新系统使用雪花 ID，旧系统可能是自增 ID。迁移时：

1. **用户**：如果旧 ID 不冲突（<雪花 ID 起始值），直接保留旧 ID
2. **如果冲突**：新建 `migration_id_map` 临时表记录 old_id → new_id 映射
3. **关联数据**：报名/订单/成绩等表的 user_id/contest_id 按映射表替换

### 3.6 历史订单处理

历史订单以**只读归档**方式迁入：
- `order_status` = `completed`
- `pay_time` = 原支付时间
- 不触发返点计算、积分发放等业务逻辑
- 订单号保留原编号（加前缀 `OLD_` 避免冲突）

## 四、执行流程

```
1. 备份新库（mysqldump）
2. 执行 01_migrate_users.py → 验证用户数
3. 执行 02_migrate_participants.py → 验证档案数
4. 执行 03_migrate_contests.py → 验证赛事数
5. 执行 04_migrate_enrolls.py → 验证报名数 + 关联正确性
6. 执行 05~09 → 逐表验证
7. 执行 verify.py → 生成对账报告
8. 人工抽样校验（随机 100 条对比新旧）
```

**每步执行后检查**：
- 新库记录数 ≥ 旧库（INSERT IGNORE 可能因重复少几条）
- 关键字段抽样：手机号、身份证、金额、状态一致
- 无 NULL 不该 NULL 的字段

## 五、回滚方案

每步迁移前记录 `新库当前最大 ID`，如需回滚：
```sql
DELETE FROM jst_user WHERE user_id > {迁移前最大ID};
```

## 六、产出物

1. `scripts/migration/` 目录（含所有迁移脚本 + config + README）
2. `scripts/migration/README.md`（迁移执行手册）
3. `架构设计/ddl/99-migration-id-map.sql`（临时 ID 映射表）
4. 报告交付（含迁移量、耗时、校验结果）

## 七、注意事项

- **必须在低峰期执行**（凌晨 2-6 点）
- **分批写入**：BATCH_SIZE=1000 + sleep 100ms
- **INSERT IGNORE**：幂等，可重复执行
- **不要在迁移期间跑定时任务**（暂停 Quartz）
- **迁移完成后重建索引统计**：`ANALYZE TABLE jst_user;`

## 八、DoD

- [ ] 迁移脚本框架搭建（config + 模板 + README）
- [ ] 至少完成 users/participants/contests 三表脚本（其余可后续补充）
- [ ] verify.py 对账脚本
- [ ] ID 映射表 DDL
- [ ] README 迁移执行手册
- [ ] 报告交付

**注意**：本卡只做脚本框架和核心表脚本。实际执行需要等旧库连接信息和字段映射确认后才能跑。
