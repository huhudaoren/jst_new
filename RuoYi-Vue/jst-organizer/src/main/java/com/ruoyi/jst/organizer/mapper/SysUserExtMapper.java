package com.ruoyi.jst.organizer.mapper;

import com.ruoyi.common.core.domain.entity.SysUser;
import org.apache.ibatis.annotations.Param;

/**
 * 赛事方入驻审核用系统用户扩展 Mapper
 * <p>
 * 仅允许：
 * <ul>
 *   <li>按 role_key 查询 role_id</li>
 *   <li>按 user_name 查询是否已存在用户</li>
 *   <li>新增 sys_user</li>
 *   <li>查询用户角色关联是否存在</li>
 *   <li>新增 sys_user_role 关联</li>
 * </ul>
 *
 * @author jst
 * @since 1.0.0
 */
public interface SysUserExtMapper {

    /**
     * 按角色标识查询角色ID
     *
     * @param roleKey 角色标识
     * @return 角色ID
     */
    Long selectRoleIdByRoleKey(@Param("roleKey") String roleKey);

    /**
     * 按用户名查询用户ID
     *
     * @param userName 用户名
     * @return 用户ID
     */
    Long selectUserIdByUserName(@Param("userName") String userName);

    /**
     * 新增系统用户
     *
     * @param user 系统用户
     * @return 影响行数
     */
    int insertSysUser(@Param("user") SysUser user);

    /**
     * 查询用户角色关联数量
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 关联数量
     */
    int countUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 新增用户角色关联
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
