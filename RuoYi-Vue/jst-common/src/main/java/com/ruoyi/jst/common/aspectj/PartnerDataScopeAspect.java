package com.ruoyi.jst.common.aspectj;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.annotation.PartnerDataScope;
import com.ruoyi.jst.common.util.PartnerScopeUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Injects partner-level SQL conditions into {@code BaseEntity.params.dataScope}.
 */
@Aspect
@Component
public class PartnerDataScopeAspect {

    public static final String DATA_SCOPE = "dataScope";
    private static final Logger log = LoggerFactory.getLogger(PartnerDataScopeAspect.class);

    @Before("@annotation(partnerDataScope)")
    public void doBefore(JoinPoint point, PartnerDataScope partnerDataScope) {
        clearDataScope(point);
        handleDataScope(point, partnerDataScope);
    }

    protected void handleDataScope(JoinPoint joinPoint, PartnerDataScope partnerDataScope) {
        LoginUser loginUser = PartnerScopeUtils.getLoginUserQuietly();
        if (loginUser == null || PartnerScopeUtils.isPlatformOp(loginUser)) {
            return;
        }
        if (!PartnerScopeUtils.isPartnerRole(loginUser)) {
            return;
        }

        BaseEntity baseEntity = PartnerScopeUtils.findBaseEntity(joinPoint.getArgs());
        if (baseEntity == null) {
            log.warn("[PartnerDataScope] No BaseEntity argument found on {}", joinPoint.getSignature().toShortString());
            return;
        }

        Long partnerId = PartnerScopeUtils.requireCurrentPartnerId();
        String partnerIdColumn = resolvePartnerIdColumn(partnerDataScope);
        String sqlCondition = PartnerScopeUtils.buildDataScopeSql(partnerDataScope.deptAlias(),
                partnerIdColumn, partnerId);
        baseEntity.getParams().put(DATA_SCOPE, sqlCondition);
    }

    private void clearDataScope(JoinPoint joinPoint) {
        BaseEntity baseEntity = PartnerScopeUtils.findBaseEntity(joinPoint.getArgs());
        if (baseEntity != null) {
            baseEntity.getParams().put(DATA_SCOPE, "");
        }
    }

    private String resolvePartnerIdColumn(PartnerDataScope partnerDataScope) {
        if (StringUtils.isNotBlank(partnerDataScope.organizerIdColumn())) {
            return partnerDataScope.organizerIdColumn();
        }
        return partnerDataScope.partnerIdColumn();
    }
}
