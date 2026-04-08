package com.ruoyi.jst.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 微信登录角色查询轻量 Mapper。
 * <p>
 * 仅用于按 userId 读取 sys_user_role / sys_role 中已分配的角色标识，
 * 避免从 jst_user.user_type 推断业务角色。
 *
 * @author jst
 * @since 1.0.0
 */
@Mapper
public interface RoleLookupMapper {

    /**
     * 查询用户已绑定的启用角色标识列表。
     *
     * @param userId 用户ID
     * @return role_key 列表
     */
    @Select("SELECT r.role_key " +
            "FROM sys_user_role ur " +
            "JOIN sys_role r ON ur.role_id = r.role_id " +
            "WHERE ur.user_id = #{userId} " +
            "AND r.del_flag = '0' " +
            "AND r.status = '0'")
    List<String> selectRoleKeysByUserId(@Param("userId") Long userId);
}
