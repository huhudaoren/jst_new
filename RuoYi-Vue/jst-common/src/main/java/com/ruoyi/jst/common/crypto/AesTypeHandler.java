package com.ruoyi.jst.common.crypto;

import com.ruoyi.jst.common.spring.SpringContextHolder;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis 自定义 TypeHandler：写入时自动加密，读取时自动解密
 * <p>
 * 使用方式（在 Mapper.xml 的 resultMap 字段上声明）：
 * <pre>
 *   &lt;result column="id_card_no" property="idCardNo" typeHandler="com.ruoyi.jst.common.crypto.AesTypeHandler"/&gt;
 * </pre>
 * <p>
 * 实现说明：MyBatis 直接 new TypeHandler 实例,不走 Spring 容器。
 * 通过 SpringContextHolder 拿 JstCipher Bean。
 *
 * @author jst
 * @since 1.0.0
 */
@MappedTypes(String.class)
public class AesTypeHandler extends BaseTypeHandler<String> {

    private static volatile JstCipher cipherCache;

    private static JstCipher cipher() {
        if (cipherCache == null) {
            synchronized (AesTypeHandler.class) {
                if (cipherCache == null) {
                    cipherCache = SpringContextHolder.getBean(JstCipher.class);
                }
            }
        }
        return cipherCache;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, cipher().encrypt(parameter));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return cipher().decrypt(rs.getString(columnName));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return cipher().decrypt(rs.getString(columnIndex));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cipher().decrypt(cs.getString(columnIndex));
    }
}
