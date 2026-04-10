package com.ruoyi.jst.common.aspectj;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.annotation.PartnerDataScope;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.controller.BasePartnerController;
import com.ruoyi.jst.common.exception.BizErrorCode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PartnerDataScopeAspectTest {

    private static AnnotationConfigApplicationContext context;

    @BeforeAll
    static void initContext() {
        context = new AnnotationConfigApplicationContext(TestConfig.class);
    }

    @AfterAll
    static void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
        JstLoginContext.clear();
    }

    @Test
    void partnerRoleInjectsCurrentPartnerFilter() {
        loginAs(101L, 2001L, "jst_partner");
        QueryReqDTO query = new QueryReqDTO();

        context.getBean(TestPartnerService.class).list(query);

        assertEquals(" AND c.partner_id = 2001 ", query.getParams().get(PartnerDataScopeAspect.DATA_SCOPE));
    }

    @Test
    void partnerRoleReflectsLatestPartnerBinding() {
        loginAs(102L, 2002L, "jst_partner");
        QueryReqDTO query = new QueryReqDTO();

        context.getBean(TestPartnerService.class).list(query);

        assertEquals(" AND c.partner_id = 2002 ", query.getParams().get(PartnerDataScopeAspect.DATA_SCOPE));
    }

    @Test
    void organizerColumnCompatibilityAliasStillWorks() {
        loginAs(103L, 2003L, "jst_partner");
        QueryReqDTO query = new QueryReqDTO();

        context.getBean(TestPartnerService.class).listByOrganizerColumn(query);

        assertEquals(" AND o.organizer_id = 2003 ", query.getParams().get(PartnerDataScopeAspect.DATA_SCOPE));
    }

    @Test
    void platformOperatorSkipsPartnerScopeInjection() {
        loginAs(1L, null, "jst_platform_op");
        QueryReqDTO query = new QueryReqDTO();

        context.getBean(TestPartnerService.class).list(query);

        assertEquals("", query.getParams().get(PartnerDataScopeAspect.DATA_SCOPE));
    }

    @Test
    void nonPartnerAccountCannotReadCurrentPartnerId() {
        loginAs(201L, null, "jst_student");

        ServiceException exception = assertThrows(ServiceException.class,
                () -> context.getBean(TestPartnerController.class).exposeCurrentPartnerId());

        assertEquals(BizErrorCode.JST_COMMON_AUTH_DENIED.code(), exception.getCode());
    }

    @Test
    void partnerAccountRequiresPartnerBinding() {
        loginAs(202L, null, "jst_partner");

        ServiceException exception = assertThrows(ServiceException.class,
                () -> context.getBean(TestPartnerController.class).exposeCurrentPartnerId());

        assertEquals(BizErrorCode.JST_COMMON_AUTH_DENIED.code(), exception.getCode());
        assertEquals("当前赛事方账号未绑定 partnerId", exception.getMessage());
    }

    private void loginAs(Long userId, Long partnerId, String... roleKeys) {
        LoginUser loginUser = buildLoginUser(userId, roleKeys);
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList()));

        JstLoginContext loginContext = new JstLoginContext();
        loginContext.setUserId(userId);
        loginContext.setPartnerId(partnerId);
        loginContext.setRoleKeys(Arrays.asList(roleKeys));
        JstLoginContext.set(loginContext);
    }

    private LoginUser buildLoginUser(Long userId, String... roleKeys) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setUserName("tester-" + userId);
        sysUser.setRoles(new ArrayList<>());
        for (String roleKey : roleKeys) {
            SysRole role = new SysRole();
            role.setRoleKey(roleKey);
            sysUser.getRoles().add(role);
        }

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(userId);
        loginUser.setUser(sysUser);
        return loginUser;
    }

    static class QueryReqDTO extends BaseEntity {
        private static final long serialVersionUID = 1L;
    }

    static class TestPartnerService {

        @PartnerDataScope(deptAlias = "c")
        public QueryReqDTO list(QueryReqDTO query) {
            return query;
        }

        @PartnerDataScope(deptAlias = "o", organizerIdColumn = "organizer_id")
        public QueryReqDTO listByOrganizerColumn(QueryReqDTO query) {
            return query;
        }
    }

    static class TestPartnerController extends BasePartnerController {
        public Long exposeCurrentPartnerId() {
            return currentPartnerId();
        }
    }

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class TestConfig {

        @Bean
        PartnerDataScopeAspect partnerDataScopeAspect() {
            return new PartnerDataScopeAspect();
        }

        @Bean
        TestPartnerService testPartnerService() {
            return new TestPartnerService();
        }

        @Bean
        TestPartnerController testPartnerController() {
            return new TestPartnerController();
        }
    }
}
