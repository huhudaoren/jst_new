package com.ruoyi.jst.common.statemachine;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.Map;
import java.util.Set;

/**
 * 简易状态机骨架。每个领域的状态枚举继承本接口实现 allowed() 方法。
 * <p>
 * 用法示例（领域代码）：
 * <pre>
 * public enum OrderStatus implements StateMachine&lt;OrderStatus&gt; {
 *     CREATED, PENDING_PAY, PAID, REVIEWING, IN_SERVICE, AFTERSALE, COMPLETED, CANCELLED, CLOSED;
 *
 *     private static final Map&lt;OrderStatus, Set&lt;OrderStatus&gt;&gt; ALLOWED = Map.of(
 *         CREATED,     Set.of(PENDING_PAY, CANCELLED),
 *         PENDING_PAY, Set.of(PAID, CANCELLED),
 *         PAID,        Set.of(REVIEWING),
 *         REVIEWING,   Set.of(IN_SERVICE, CANCELLED),
 *         IN_SERVICE,  Set.of(AFTERSALE, COMPLETED),
 *         AFTERSALE,   Set.of(IN_SERVICE),
 *         COMPLETED,   Set.of(CLOSED)
 *     );
 *
 *     &#64;Override public Map&lt;OrderStatus, Set&lt;OrderStatus&gt;&gt; allowed() { return ALLOWED; }
 * }
 *
 * // 业务调用
 * OrderStatus.PAID.assertCanTransitTo(OrderStatus.REVIEWING);  // 通过
 * OrderStatus.PAID.assertCanTransitTo(OrderStatus.COMPLETED);  // 抛 ServiceException
 * </pre>
 *
 * @param <E> 状态枚举类型
 * @author jst
 * @since 1.0.0
 */
public interface StateMachine<E extends Enum<E>> {

    /**
     * 跃迁矩阵
     */
    Map<E, Set<E>> allowed();

    /**
     * 校验当前状态可跃迁到 target，否则抛 ServiceException
     */
    @SuppressWarnings("unchecked")
    default void assertCanTransitTo(E target) {
        E self = (E) this;
        Set<E> allowedSet = allowed().getOrDefault(self, Set.of());
        if (!allowedSet.contains(target)) {
            throw new ServiceException(
                    String.format("状态非法跃迁: %s → %s (allowed=%s)", self, target, allowedSet),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
    }
}
