package com.ruoyi.jst.event.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 成绩导入响应。
 *
 * @author jst
 * @since 1.0.0
 */
public class ScoreImportResVO {

    private String importBatchNo;
    private Integer totalCount;
    private Integer successCount;
    private Integer failedCount;
    private List<String> errors = new ArrayList<>();

    public String getImportBatchNo() { return importBatchNo; }
    public void setImportBatchNo(String importBatchNo) { this.importBatchNo = importBatchNo; }
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Integer getSuccessCount() { return successCount; }
    public void setSuccessCount(Integer successCount) { this.successCount = successCount; }
    public Integer getFailedCount() { return failedCount; }
    public void setFailedCount(Integer failedCount) { this.failedCount = failedCount; }
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
