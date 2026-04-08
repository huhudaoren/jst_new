package com.ruoyi.jst.common.id;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 雪花算法 ID 生成器
 * <p>
 * 64bit 结构：1bit 符号 + 41bit 毫秒时间戳 + 5bit datacenterId + 5bit workerId + 12bit 序列
 * 单机每毫秒最多生成 4096 个 ID。
 * <p>
 * 配置项：
 *   jst.biz.snowflake-worker-id      (0~31)
 *   jst.biz.snowflake-datacenter-id  (0~31)
 * <p>
 * 用于业务可读单号场景的雪花 ID（如 order_no = "JST" + nextId() 截位）。
 * 业务表的自增主键由数据库 auto_increment 处理，本类不参与。
 *
 * @author jst
 * @since 1.0.0
 */
@Component
public class SnowflakeIdWorker {

    /** 起始时间戳 (2026-01-01 00:00:00 UTC+8) */
    private static final long START_TIMESTAMP = 1767196800000L;

    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdWorker(@Value("${jst.biz.snowflake-worker-id:1}") long workerId,
                             @Value("${jst.biz.snowflake-datacenter-id:1}") long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("workerId 必须在 0~" + MAX_WORKER_ID + " 之间");
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId 必须在 0~" + MAX_DATACENTER_ID + " 之间");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 生成下一个 64 位雪花 ID
     */
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("时钟回拨，拒绝生成 ID。差值=" + (lastTimestamp - timestamp) + "ms");
        }
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 生成业务可读的字符串 ID（前缀 + 时间 + 序列）
     * @param prefix 业务前缀，例如 "JST" "OD" "RF"
     */
    public String nextBizNo(String prefix) {
        return prefix + nextId();
    }

    private long waitNextMillis(long lastTs) {
        long ts = System.currentTimeMillis();
        while (ts <= lastTs) {
            ts = System.currentTimeMillis();
        }
        return ts;
    }
}
