package com.ruoyi.jst.common.util;

/**
 * 敏感字段脱敏工具
 * <p>
 * 强制：所有日志、ResVO、审计 before/after 在序列化前必须使用本工具脱敏。
 * 参见 16-安全与敏感字段.md §2.3
 *
 * @author jst
 * @since 1.0.0
 */
public final class MaskUtil {

    private MaskUtil() {}

    /** 手机号：保留前 3 + 后 4，例如 138****1234 */
    public static String mobile(String m) {
        if (m == null || m.length() < 7) return m;
        return m.substring(0, 3) + "****" + m.substring(m.length() - 4);
    }

    /** 身份证号：保留前 4 + 后 4，例如 1101**********1234 */
    public static String idCard(String id) {
        if (id == null || id.length() < 10) return id;
        return id.substring(0, 4) + "**********" + id.substring(id.length() - 4);
    }

    /** 银行卡号：保留前 4 + 后 4 */
    public static String bankAccount(String acc) {
        if (acc == null || acc.length() < 8) return acc;
        return acc.substring(0, 4) + "********" + acc.substring(acc.length() - 4);
    }

    /** 真实姓名：保留首字符，其余 * */
    public static String realName(String name) {
        if (name == null || name.isEmpty()) return name;
        if (name.length() == 1) return name;
        if (name.length() == 2) return name.charAt(0) + "*";
        return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1);
    }

    /** 邮箱：a***@b.com */
    public static String email(String e) {
        if (e == null || !e.contains("@")) return e;
        int at = e.indexOf('@');
        if (at <= 1) return e;
        return e.charAt(0) + "***" + e.substring(at);
    }

    /** 地址：保留省市，模糊详细 */
    public static String address(String addr) {
        if (addr == null || addr.length() <= 6) return addr;
        return addr.substring(0, 6) + "******";
    }
}
