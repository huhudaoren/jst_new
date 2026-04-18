package com.ruoyi.web.controller.jst;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 统一实体选择器后端（配合 RelationPicker / EntityLink 前端组件）。
 * <p>
 * 端点：
 * <ul>
 *   <li>GET /admin/entity/search?type=channel&amp;kw=xxx  → 搜索候选项（Picker 用）</li>
 *   <li>GET /admin/entity/brief?type=channel&amp;id=123   → 单个实体摘要（EntityLink 用）</li>
 * </ul>
 * <p>
 * 返回格式 {id, name, subtitle, statusTag}。
 * <p>
 * 字段名核对（已 DESCRIBE 验证，2026-04-17）：
 * <ul>
 *   <li>jst_channel: channel_id, channel_name, contact_mobile, status (tinyint)</li>
 *   <li>jst_sales: sales_id, sales_name, sales_no, status (varchar)</li>
 *   <li>sys_user: user_id, nick_name, user_name, status (char)</li>
 *   <li>jst_user: user_id, nickname, mobile, status (tinyint) — 注意 nickname 非 nick_name</li>
 *   <li>jst_event_partner: partner_id, partner_name, contact_mobile, audit_status</li>
 *   <li>jst_contest: contest_id, contest_name, category, audit_status</li>
 *   <li>jst_participant: participant_id, name (非 participant_name), guardian_mobile — 无 phone 字段</li>
 *   <li>jst_order_main: order_id, order_no, net_pay_amount, order_status</li>
 *   <li>jst_enroll_form_template: template_id, template_name, template_version, owner_type, owner_id, audit_status</li>
 *   <li>jst_cert_template: template_id, template_name, owner_type, owner_id, bg_image, audit_status</li>
 *   <li>jst_coupon_template: coupon_template_id, coupon_name, coupon_type, threshold_amount, status</li>
 *   <li>jst_rights_template: rights_template_id, rights_name, rights_type, quota_mode, quota_value, status</li>
 *   <li>jst_course: course_id, course_name, course_type (非 category), status (on/off), del_flag</li>
 * </ul>
 *
 * @author jst
 * @since 1.0.0
 */
@RestController
@RequestMapping("/admin/entity")
@PreAuthorize("isAuthenticated()")
public class EntityBriefController extends BaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 搜索候选项（RelationPicker 用）。
     *
     * @param type  实体类型：channel / sales / user / jstUser / partner / contest /
     *              participant / participantStudent / order
     * @param kw    关键字（可为空，为空时返回最新 limit 条）
     * @param limit 最多返回条数，默认 20，上限 100
     */
    @GetMapping("/search")
    public AjaxResult search(@RequestParam String type,
                             @RequestParam(required = false) String kw,
                             @RequestParam(defaultValue = "20") int limit) {
        if (limit > 100) limit = 100;
        if (limit < 1) limit = 20;
        List<Map<String, Object>> rows = searchByType(type, kw, limit);
        return AjaxResult.success(rows);
    }

    /**
     * 取单个实体摘要（EntityLink hydrate 用）。
     *
     * @param type 实体类型（同 search 端点）
     * @param id   实体主键
     */
    @GetMapping("/brief")
    public AjaxResult brief(@RequestParam String type, @RequestParam Long id) {
        Map<String, Object> row = briefByType(type, id);
        return AjaxResult.success(row);
    }

    // ============================================================
    // Private: search 派发
    // ============================================================

    private List<Map<String, Object>> searchByType(String type, String kw, int limit) {
        switch (type) {
            case "channel":             return searchChannel(kw, limit);
            case "sales":               return searchSales(kw, limit);
            case "user":                return searchSysUser(kw, limit);
            case "jstUser":             return searchJstUser(kw, limit);
            case "partner":             return searchPartner(kw, limit);
            case "contest":             return searchContest(kw, limit);
            case "participant":
            case "participantStudent":  return searchParticipant(kw, limit);
            case "order":               return searchOrder(kw, limit);
            case "formTemplate":        return searchFormTemplate(kw, limit);
            case "certTemplate":        return searchCertTemplate(kw, limit);
            case "couponTemplate":      return searchCouponTemplate(kw, limit);
            case "rightsTemplate":      return searchRightsTemplate(kw, limit);
            case "course":              return searchCourse(kw, limit);
            default:                    return Collections.emptyList();
        }
    }

    /**
     * jst_channel: channel_id, channel_name, contact_mobile, status(tinyint)
     */
    private List<Map<String, Object>> searchChannel(String kw, int limit) {
        String base = "SELECT channel_id AS id, channel_name AS name, contact_mobile AS subtitle, status AS statusTag"
                + " FROM jst_channel WHERE del_flag = '0' ";
        if (StringUtils.isNotBlank(kw)) {
            String like = "%" + kw + "%";
            return jdbcTemplate.queryForList(
                    base + "AND (channel_name LIKE ? OR contact_mobile LIKE ?) ORDER BY channel_id DESC LIMIT ?",
                    like, like, limit);
        }
        return jdbcTemplate.queryForList(base + "ORDER BY channel_id DESC LIMIT ?", limit);
    }

    /**
     * jst_sales: sales_id, sales_name, sales_no, phone, status(varchar)
     */
    private List<Map<String, Object>> searchSales(String kw, int limit) {
        String base = "SELECT sales_id AS id, sales_name AS name, sales_no AS subtitle, status AS statusTag"
                + " FROM jst_sales ";
        if (StringUtils.isNotBlank(kw)) {
            String like = "%" + kw + "%";
            return jdbcTemplate.queryForList(
                    base + "WHERE (sales_name LIKE ? OR sales_no LIKE ? OR phone LIKE ?) ORDER BY sales_id DESC LIMIT ?",
                    like, like, like, limit);
        }
        return jdbcTemplate.queryForList(
                base + "WHERE status IN ('active','resign_apply') ORDER BY sales_id DESC LIMIT ?", limit);
    }

    /**
     * sys_user: user_id, nick_name, user_name, status(char: '0'=正常 '1'=停用)
     */
    private List<Map<String, Object>> searchSysUser(String kw, int limit) {
        String base = "SELECT user_id AS id, nick_name AS name, user_name AS subtitle, status AS statusTag"
                + " FROM sys_user WHERE del_flag = '0' ";
        if (StringUtils.isNotBlank(kw)) {
            String like = "%" + kw + "%";
            return jdbcTemplate.queryForList(
                    base + "AND (nick_name LIKE ? OR user_name LIKE ?) ORDER BY user_id DESC LIMIT ?",
                    like, like, limit);
        }
        return jdbcTemplate.queryForList(base + "ORDER BY user_id DESC LIMIT ?", limit);
    }

    /**
     * jst_user: user_id, nickname (非 nick_name), mobile (非 phone), status(tinyint)
     * del_flag='0' 表示未删除
     */
    private List<Map<String, Object>> searchJstUser(String kw, int limit) {
        String base = "SELECT user_id AS id, COALESCE(nickname, mobile) AS name, mobile AS subtitle"
                + " FROM jst_user WHERE del_flag = '0' ";
        if (StringUtils.isNotBlank(kw)) {
            String like = "%" + kw + "%";
            return jdbcTemplate.queryForList(
                    base + "AND (nickname LIKE ? OR mobile LIKE ?) ORDER BY user_id DESC LIMIT ?",
                    like, like, limit);
        }
        return jdbcTemplate.queryForList(base + "ORDER BY user_id DESC LIMIT ?", limit);
    }

    /**
     * jst_event_partner: partner_id, partner_name, contact_mobile (非 contact_phone), audit_status
     */
    private List<Map<String, Object>> searchPartner(String kw, int limit) {
        String base = "SELECT partner_id AS id, partner_name AS name, contact_mobile AS subtitle, audit_status AS statusTag"
                + " FROM jst_event_partner WHERE del_flag = '0' ";
        if (StringUtils.isNotBlank(kw)) {
            String like = "%" + kw + "%";
            return jdbcTemplate.queryForList(
                    base + "AND (partner_name LIKE ? OR contact_mobile LIKE ?) ORDER BY partner_id DESC LIMIT ?",
                    like, like, limit);
        }
        return jdbcTemplate.queryForList(base + "ORDER BY partner_id DESC LIMIT ?", limit);
    }

    /**
     * jst_contest: contest_id, contest_name, category, audit_status
     */
    private List<Map<String, Object>> searchContest(String kw, int limit) {
        String base = "SELECT contest_id AS id, contest_name AS name, category AS subtitle, audit_status AS statusTag"
                + " FROM jst_contest WHERE del_flag = '0' ";
        if (StringUtils.isNotBlank(kw)) {
            String like = "%" + kw + "%";
            return jdbcTemplate.queryForList(
                    base + "AND contest_name LIKE ? ORDER BY contest_id DESC LIMIT ?",
                    like, limit);
        }
        return jdbcTemplate.queryForList(base + "ORDER BY contest_id DESC LIMIT ?", limit);
    }

    /**
     * jst_participant: participant_id, name (非 participant_name), guardian_mobile (无 phone 字段)
     */
    private List<Map<String, Object>> searchParticipant(String kw, int limit) {
        String base = "SELECT participant_id AS id, name AS name, guardian_mobile AS subtitle"
                + " FROM jst_participant WHERE del_flag = '0' ";
        if (StringUtils.isNotBlank(kw)) {
            String like = "%" + kw + "%";
            return jdbcTemplate.queryForList(
                    base + "AND (name LIKE ? OR guardian_mobile LIKE ?) ORDER BY participant_id DESC LIMIT ?",
                    like, like, limit);
        }
        return jdbcTemplate.queryForList(base + "ORDER BY participant_id DESC LIMIT ?", limit);
    }

    /**
     * jst_order_main: order_id, order_no, net_pay_amount, order_status
     */
    private List<Map<String, Object>> searchOrder(String kw, int limit) {
        String base = "SELECT order_id AS id, order_no AS name, CAST(net_pay_amount AS CHAR) AS subtitle, order_status AS statusTag"
                + " FROM jst_order_main WHERE del_flag = '0' ";
        if (StringUtils.isNotBlank(kw)) {
            String like = "%" + kw + "%";
            return jdbcTemplate.queryForList(
                    base + "AND order_no LIKE ? ORDER BY order_id DESC LIMIT ?",
                    like, limit);
        }
        return jdbcTemplate.queryForList(base + "ORDER BY order_id DESC LIMIT ?", limit);
    }

    /**
     * jst_enroll_form_template: template_id, template_name, template_version, owner_type, owner_id, audit_status
     * owner_type=platform 表示平台模板；owner_type=partner 时 owner_id 对应 jst_event_partner.partner_id
     */
    private List<Map<String, Object>> searchFormTemplate(String kw, int limit) {
        String base = "SELECT t.template_id AS id, t.template_name AS name, "
                + "CONCAT("
                + "CASE "
                + "WHEN t.owner_type = 'platform' THEN '平台模板' "
                + "ELSE IFNULL(p.partner_name, CONCAT('赛事方#', CAST(t.owner_id AS CHAR))) "
                + "END, "
                + "' · V', CAST(IFNULL(t.template_version, 1) AS CHAR)"
                + ") AS subtitle, "
                + "t.audit_status AS statusTag "
                + "FROM jst_enroll_form_template t "
                + "LEFT JOIN jst_event_partner p ON t.owner_type = 'partner' AND t.owner_id = p.partner_id AND p.del_flag = '0' "
                + "WHERE t.del_flag = '0' ";
        if (StringUtils.isNotBlank(kw)) {
            String like = "%" + kw + "%";
            return jdbcTemplate.queryForList(
                    base + "AND (t.template_name LIKE ? OR p.partner_name LIKE ?) ORDER BY t.template_id DESC LIMIT ?",
                    like, like, limit);
        }
        return jdbcTemplate.queryForList(base + "ORDER BY t.template_id DESC LIMIT ?", limit);
    }

    /**
     * jst_cert_template: template_id, template_name, owner_type, owner_id, bg_image, audit_status
     * owner_type=platform 表示平台模板；owner_type=partner 时 owner_id 对应 jst_event_partner.partner_id
     */
    private List<Map<String, Object>> searchCertTemplate(String kw, int limit) {
        String base = "SELECT t.template_id AS id, t.template_name AS name, "
                + "CONCAT("
                + "CASE "
                + "WHEN t.owner_type = 'platform' THEN '平台模板' "
                + "ELSE IFNULL(p.partner_name, CONCAT('赛事方#', CAST(t.owner_id AS CHAR))) "
                + "END, "
                + "' · ', "
                + "CASE WHEN t.bg_image IS NULL OR t.bg_image = '' THEN '空白模板' ELSE '已配底图' END"
                + ") AS subtitle, "
                + "t.audit_status AS statusTag "
                + "FROM jst_cert_template t "
                + "LEFT JOIN jst_event_partner p ON t.owner_type = 'partner' AND t.owner_id = p.partner_id AND p.del_flag = '0' "
                + "WHERE t.del_flag = '0' ";
        if (StringUtils.isNotBlank(kw)) {
            String like = "%" + kw + "%";
            return jdbcTemplate.queryForList(
                    base + "AND (t.template_name LIKE ? OR p.partner_name LIKE ?) ORDER BY t.template_id DESC LIMIT ?",
                    like, like, limit);
        }
        return jdbcTemplate.queryForList(base + "ORDER BY t.template_id DESC LIMIT ?", limit);
    }

    /**
     * jst_coupon_template: coupon_template_id, coupon_name, coupon_type, threshold_amount, status
     */
    private List<Map<String, Object>> searchCouponTemplate(String kw, int limit) {
        String base = "SELECT coupon_template_id AS id, coupon_name AS name, "
                + "CONCAT(coupon_type, ' · 门槛¥', CAST(IFNULL(threshold_amount, 0) AS CHAR)) AS subtitle, "
                + "status AS statusTag "
                + "FROM jst_coupon_template WHERE del_flag = '0' ";
        if (StringUtils.isNotBlank(kw)) {
            String like = "%" + kw + "%";
            return jdbcTemplate.queryForList(
                    base + "AND coupon_name LIKE ? ORDER BY coupon_template_id DESC LIMIT ?",
                    like, limit);
        }
        return jdbcTemplate.queryForList(base + "ORDER BY coupon_template_id DESC LIMIT ?", limit);
    }

    /**
     * jst_rights_template: rights_template_id, rights_name, rights_type, quota_mode, quota_value, status
     */
    private List<Map<String, Object>> searchRightsTemplate(String kw, int limit) {
        String base = "SELECT rights_template_id AS id, rights_name AS name, "
                + "CONCAT(rights_type, ' · ', quota_mode, ':', CAST(IFNULL(quota_value, 0) AS CHAR)) AS subtitle, "
                + "status AS statusTag "
                + "FROM jst_rights_template WHERE del_flag = '0' ";
        if (StringUtils.isNotBlank(kw)) {
            String like = "%" + kw + "%";
            return jdbcTemplate.queryForList(
                    base + "AND rights_name LIKE ? ORDER BY rights_template_id DESC LIMIT ?",
                    like, limit);
        }
        return jdbcTemplate.queryForList(base + "ORDER BY rights_template_id DESC LIMIT ?", limit);
    }

    /**
     * jst_course: course_id, course_name, course_type (非 category), status (on/off)
     */
    private List<Map<String, Object>> searchCourse(String kw, int limit) {
        String base = "SELECT course_id AS id, course_name AS name, course_type AS subtitle, status AS statusTag"
                + " FROM jst_course WHERE del_flag = '0' ";
        if (StringUtils.isNotBlank(kw)) {
            String like = "%" + kw + "%";
            return jdbcTemplate.queryForList(
                    base + "AND course_name LIKE ? ORDER BY course_id DESC LIMIT ?",
                    like, limit);
        }
        return jdbcTemplate.queryForList(base + "ORDER BY course_id DESC LIMIT ?", limit);
    }

    // ============================================================
    // Private: brief 派发
    // ============================================================

    private Map<String, Object> briefByType(String type, Long id) {
        String sql = buildBriefSql(type);
        if (sql == null) return null;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id);
        return rows.isEmpty() ? null : rows.get(0);
    }

    private String buildBriefSql(String type) {
        switch (type) {
            case "channel":
                return "SELECT channel_id AS id, channel_name AS name, contact_mobile AS subtitle, status AS statusTag"
                        + " FROM jst_channel WHERE del_flag = '0' AND channel_id = ?";
            case "sales":
                return "SELECT sales_id AS id, sales_name AS name, sales_no AS subtitle, status AS statusTag"
                        + " FROM jst_sales WHERE sales_id = ?";
            case "user":
                return "SELECT user_id AS id, nick_name AS name, user_name AS subtitle, status AS statusTag"
                        + " FROM sys_user WHERE del_flag = '0' AND user_id = ?";
            case "jstUser":
                return "SELECT user_id AS id, COALESCE(nickname, mobile) AS name, mobile AS subtitle"
                        + " FROM jst_user WHERE del_flag = '0' AND user_id = ?";
            case "partner":
                return "SELECT partner_id AS id, partner_name AS name, contact_mobile AS subtitle, audit_status AS statusTag"
                        + " FROM jst_event_partner WHERE del_flag = '0' AND partner_id = ?";
            case "contest":
                return "SELECT contest_id AS id, contest_name AS name, category AS subtitle, audit_status AS statusTag"
                        + " FROM jst_contest WHERE del_flag = '0' AND contest_id = ?";
            case "participant":
            case "participantStudent":
                return "SELECT participant_id AS id, name AS name, guardian_mobile AS subtitle"
                        + " FROM jst_participant WHERE del_flag = '0' AND participant_id = ?";
            case "order":
                return "SELECT order_id AS id, order_no AS name, CAST(net_pay_amount AS CHAR) AS subtitle, order_status AS statusTag"
                        + " FROM jst_order_main WHERE del_flag = '0' AND order_id = ?";
            case "formTemplate":
                return "SELECT t.template_id AS id, t.template_name AS name, "
                        + "CONCAT("
                        + "CASE "
                        + "WHEN t.owner_type = 'platform' THEN '平台模板' "
                        + "ELSE IFNULL(p.partner_name, CONCAT('赛事方#', CAST(t.owner_id AS CHAR))) "
                        + "END, "
                        + "' · V', CAST(IFNULL(t.template_version, 1) AS CHAR)"
                        + ") AS subtitle, "
                        + "t.audit_status AS statusTag "
                        + "FROM jst_enroll_form_template t "
                        + "LEFT JOIN jst_event_partner p ON t.owner_type = 'partner' AND t.owner_id = p.partner_id AND p.del_flag = '0' "
                        + "WHERE t.del_flag = '0' AND t.template_id = ?";
            case "certTemplate":
                return "SELECT t.template_id AS id, t.template_name AS name, "
                        + "CONCAT("
                        + "CASE "
                        + "WHEN t.owner_type = 'platform' THEN '平台模板' "
                        + "ELSE IFNULL(p.partner_name, CONCAT('赛事方#', CAST(t.owner_id AS CHAR))) "
                        + "END, "
                        + "' · ', "
                        + "CASE WHEN t.bg_image IS NULL OR t.bg_image = '' THEN '空白模板' ELSE '已配底图' END"
                        + ") AS subtitle, "
                        + "t.audit_status AS statusTag "
                        + "FROM jst_cert_template t "
                        + "LEFT JOIN jst_event_partner p ON t.owner_type = 'partner' AND t.owner_id = p.partner_id AND p.del_flag = '0' "
                        + "WHERE t.del_flag = '0' AND t.template_id = ?";
            case "couponTemplate":
                return "SELECT coupon_template_id AS id, coupon_name AS name, "
                        + "CONCAT(coupon_type, ' · 门槛¥', CAST(IFNULL(threshold_amount, 0) AS CHAR)) AS subtitle, "
                        + "status AS statusTag "
                        + "FROM jst_coupon_template WHERE del_flag = '0' AND coupon_template_id = ?";
            case "rightsTemplate":
                return "SELECT rights_template_id AS id, rights_name AS name, "
                        + "CONCAT(rights_type, ' · ', quota_mode, ':', CAST(IFNULL(quota_value, 0) AS CHAR)) AS subtitle, "
                        + "status AS statusTag "
                        + "FROM jst_rights_template WHERE del_flag = '0' AND rights_template_id = ?";
            case "course":
                return "SELECT course_id AS id, course_name AS name, course_type AS subtitle, status AS statusTag"
                        + " FROM jst_course WHERE del_flag = '0' AND course_id = ?";
            default:
                return null;
        }
    }
}
