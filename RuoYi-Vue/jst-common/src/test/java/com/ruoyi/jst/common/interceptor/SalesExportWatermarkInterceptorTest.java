package com.ruoyi.jst.common.interceptor;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesExportWatermarkInterceptorTest {

    @Test
    void buildWatermarkText_containsNameAndTime() {
        LocalDateTime now = LocalDateTime.of(2026, 4, 18, 14, 30, 0);
        String text = SalesExportWatermarkInterceptor.buildWatermarkText("张三", "S202604001", now);
        assertTrue(text.contains("张三"));
        assertTrue(text.contains("S202604001"));
        assertTrue(text.contains("2026-04-18 14:30:00"));
        assertTrue(text.contains("仅供内部使用") || text.contains("外泄追责"));
    }

    @Test
    void buildCsvWatermark_startsWithHash() {
        String csv = SalesExportWatermarkInterceptor.buildCsvWatermark("alice", "S001",
                LocalDateTime.of(2026, 4, 18, 9, 0, 0));
        assertTrue(csv.startsWith("# Exported"));
        assertTrue(csv.contains("alice"));
    }
}
