package com.ruoyi.jst.finance.mapper;

import com.ruoyi.jst.finance.domain.JstInvoiceTitle;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 发票抬头 Mapper。
 * <p>
 * 对应表：jst_invoice_title（DDL：架构设计/ddl/99-migration-invoice-title.sql）
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstInvoiceTitleMapper {

    /** 按抬头 ID 查询（不带 del_flag 过滤，Service 层统一处理） */
    JstInvoiceTitle selectByTitleId(@Param("titleId") Long titleId);

    /** 按用户 ID 查询全部未删除抬头（默认排在前 + create_time 倒序） */
    List<JstInvoiceTitle> selectByUserId(@Param("userId") Long userId);

    /** 新增（会把 titleId 回填） */
    int insert(JstInvoiceTitle entity);

    /** 更新（仅更新抬头字段，不改 is_default） */
    int update(JstInvoiceTitle entity);

    /** 软删 */
    int softDelete(@Param("titleId") Long titleId,
                   @Param("updateBy") String updateBy,
                   @Param("updateTime") Date updateTime);

    /** 清除某用户全部默认标记（用于切换默认前的前置 UPDATE） */
    int clearDefaultByUserId(@Param("userId") Long userId,
                             @Param("updateBy") String updateBy,
                             @Param("updateTime") Date updateTime);

    /** 设置某抬头为默认 */
    int markDefault(@Param("titleId") Long titleId,
                    @Param("updateBy") String updateBy,
                    @Param("updateTime") Date updateTime);
}
