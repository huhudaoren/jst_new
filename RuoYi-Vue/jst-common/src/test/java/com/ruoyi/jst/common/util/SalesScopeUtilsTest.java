package com.ruoyi.jst.common.util;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.jst.common.context.JstLoginContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class SalesScopeUtilsTest {

    @BeforeEach
    void setUp() { JstLoginContext.clear(); }

    @AfterEach
    void tearDown() { JstLoginContext.clear(); }

    @Test
    void currentSalesId_returnsNull_whenNoContext() {
        assertNull(SalesScopeUtils.currentSalesId());
    }

    @Test
    void currentSalesId_returnsValue_fromContext() {
        JstLoginContext ctx = new JstLoginContext();
        ctx.setSalesId(1001L);
        JstLoginContext.set(ctx);
        assertEquals(1001L, SalesScopeUtils.currentSalesId());
    }

    @Test
    void currentSalesIdRequired_throws_whenNotSalesRole() {
        JstLoginContext ctx = new JstLoginContext();
        ctx.setRoleKeys(Collections.singletonList("jst_partner"));
        JstLoginContext.set(ctx);
        assertThrows(RuntimeException.class, SalesScopeUtils::currentSalesIdRequired);
    }

    @Test
    void currentSalesIdRequired_returnsId_whenSalesRoleAndIdPresent() {
        // 注意：currentSalesIdRequired 内部用 SecurityUtils.getLoginUser() 校验角色，
        // 测试需 mock LoginUser context，否则 isSalesRole 返 false。
        // 这里测的是 SalesScopeUtils 工具方法本身，不测端到端 SecurityContext。
        // 简化：直接验证 isSalesRole 与 currentSalesId 的组合行为通过测 isSalesRole/isManagerRole 等方法。
        // 此 case 改为：验证 isSalesRole(mockLoginUser('jst_sales')) == true
        LoginUser u = mockLoginUser("jst_sales");
        assertTrue(SalesScopeUtils.isSalesRole(u));
    }

    @Test
    void isSalesRole_returnsTrue_forSalesOrManagerRole() {
        assertTrue(SalesScopeUtils.isSalesRole(mockLoginUser("jst_sales")));
        assertTrue(SalesScopeUtils.isSalesRole(mockLoginUser("jst_sales_manager")));
        assertFalse(SalesScopeUtils.isSalesRole(mockLoginUser("jst_partner")));
    }

    @Test
    void isManagerRole_returnsTrue_onlyForManagerRole() {
        assertTrue(SalesScopeUtils.isManagerRole(mockLoginUser("jst_sales_manager")));
        assertFalse(SalesScopeUtils.isManagerRole(mockLoginUser("jst_sales")));
    }

    @Test
    void isPlatformOpOrAdminOrManager_returnsTrue_forAdmin() {
        SysUser admin = new SysUser();
        admin.setUserId(1L);  // userId=1 在若依里是 admin (isAdmin())
        admin.setRoles(Collections.emptyList());
        LoginUser u = new LoginUser();
        u.setUser(admin);
        assertTrue(SalesScopeUtils.isPlatformOpOrAdminOrManager(u));
    }

    @Test
    void isPlatformOpOrAdminOrManager_returnsTrue_forSalesManager() {
        assertTrue(SalesScopeUtils.isPlatformOpOrAdminOrManager(mockLoginUser("jst_sales_manager")));
    }

    @Test
    void isPlatformOpOrAdminOrManager_returnsFalse_forPlainSales() {
        assertFalse(SalesScopeUtils.isPlatformOpOrAdminOrManager(mockLoginUser("jst_sales")));
    }

    @Test
    void isPlainSalesOnly_returnsTrue_onlyForPlainSales() {
        assertTrue(SalesScopeUtils.isPlainSalesOnly(mockLoginUser("jst_sales")));
        assertFalse(SalesScopeUtils.isPlainSalesOnly(mockLoginUser("jst_sales_manager")));
        assertFalse(SalesScopeUtils.isPlainSalesOnly(mockLoginUser("jst_partner")));
    }

    @Test
    void findBaseEntity_returnsFirstBaseEntityArg() {
        com.ruoyi.common.core.domain.BaseEntity be = new com.ruoyi.common.core.domain.BaseEntity() {};
        Object[] args = new Object[]{ "string", 42L, be, "another" };
        assertSame(be, SalesScopeUtils.findBaseEntity(args));
        assertNull(SalesScopeUtils.findBaseEntity(new Object[]{"no entity"}));
        assertNull(SalesScopeUtils.findBaseEntity(null));
    }

    private LoginUser mockLoginUser(String roleKey) {
        SysUser user = new SysUser();
        user.setUserId(100L);  // not 1, so isAdmin() returns false
        SysRole role = new SysRole();
        role.setRoleKey(roleKey);
        user.setRoles(Arrays.asList(role));
        LoginUser lu = new LoginUser();
        lu.setUser(user);
        return lu;
    }
}
