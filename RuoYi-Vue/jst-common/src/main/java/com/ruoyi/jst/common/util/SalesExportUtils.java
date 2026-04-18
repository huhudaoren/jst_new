package com.ruoyi.jst.common.util;

import com.ruoyi.jst.common.interceptor.SalesExportWatermarkInterceptor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 销售导出 Excel 工具类（AC3 水印）。
 * <p>
 * 生成带水印首行的 XLSX 文件，使用 Apache POI（via EasyExcel transitive dependency）。
 * <p>
 * 水印行：Row 0，合并全列，灰色斜体文字，内容由
 * {@link SalesExportWatermarkInterceptor#buildWatermarkText} 生成。
 * 数据行从 Row 2 开始（Row 1 是表头）。
 * <p>
 * 典型用法：
 * <pre>{@code
 *   response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
 *   response.setHeader("Content-Disposition", "attachment; filename=export.xlsx");
 *   SalesExportUtils.writeWatermarkedExcel(
 *       response.getOutputStream(),
 *       loginUser.getUser().getNickName(),
 *       String.valueOf(loginUser.getUser().getUserId()),
 *       LocalDateTime.now(),
 *       headers, rows);
 * }</pre>
 *
 * @author jst
 * @since 1.0.0
 */
public final class SalesExportUtils {

    private SalesExportUtils() {}

    /**
     * 写入带水印的 Excel 文件。
     *
     * @param os        输出流（调用方负责关闭）
     * @param salesName 销售姓名（写入水印）
     * @param userNo    销售账号 ID（写入水印）
     * @param now       导出时间（写入水印）
     * @param headers   表头列名列表
     * @param rows      数据行列表（每行为 header→value 的 Map）
     * @throws Exception POI 操作异常
     */
    public static void writeWatermarkedExcel(
            OutputStream os,
            String salesName,
            String userNo,
            LocalDateTime now,
            List<String> headers,
            List<Map<String, Object>> rows) throws Exception {

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("data");

            // ---- Row 0: 水印行 ----
            Row wmRow = sheet.createRow(0);
            Cell wmCell = wmRow.createCell(0);
            wmCell.setCellValue(SalesExportWatermarkInterceptor
                    .buildWatermarkText(salesName, userNo, now));

            CellStyle wmStyle = wb.createCellStyle();
            Font wmFont = wb.createFont();
            wmFont.setItalic(true);
            wmFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            wmStyle.setFont(wmFont);
            wmCell.setCellStyle(wmStyle);

            if (headers != null && headers.size() > 1) {
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.size() - 1));
            }

            // ---- Row 1: 表头 ----
            if (headers != null && !headers.isEmpty()) {
                Row headerRow = sheet.createRow(1);
                CellStyle headStyle = wb.createCellStyle();
                Font headFont = wb.createFont();
                headFont.setBold(true);
                headStyle.setFont(headFont);
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers.get(i));
                    cell.setCellStyle(headStyle);
                }
            }

            // ---- Row 2+: 数据行 ----
            if (rows != null) {
                for (int r = 0; r < rows.size(); r++) {
                    Row dataRow = sheet.createRow(r + 2);
                    if (headers != null) {
                        for (int c = 0; c < headers.size(); c++) {
                            Object v = rows.get(r).get(headers.get(c));
                            dataRow.createCell(c).setCellValue(v == null ? "" : v.toString());
                        }
                    }
                }
            }

            // 自动列宽（最多 32 列）
            int colCount = headers != null ? Math.min(headers.size(), 32) : 0;
            for (int i = 0; i < colCount; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(os);
        }
    }
}
