package com.ruoyi.jst.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 跨域查询轻量 Mapper
 * <p>
 * 用途：jst-user 的 LoginEnricher 需要按 userId 查 jst_event_partner.partner_id,
 * 但 jst-event 的 Entity 在另一个模块。为避免引入 jst-event 模块依赖,
 * 本类直接通过 SQL 查询字段而不映射 Entity。
 * <p>
 * 强约束:
 * - 仅允许 SELECT,禁止 INSERT/UPDATE/DELETE 跨域操作
 * - 禁止扩展为复杂 join 查询(应在原模块定义)
 *
 * @author jst
 * @since 1.0.0
 */
@Mapper
public interface PartnerLookupMapper {

    /**
     * 按 user_id 查关联的赛事方ID(可能为 null)
     */
    @Select("SELECT partner_id FROM jst_event_partner WHERE user_id = #{userId} AND del_flag = '0' LIMIT 1")
    Long selectPartnerIdByUserId(@Param("userId") Long userId);
}
