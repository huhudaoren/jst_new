package com.ruoyi.jst.common.context;

/**
 * 登录上下文充实器接口
 * <p>
 * 由 jst-user 模块 ({@code JstLoginEnricherImpl}) 实现,
 * 通过 userId 查找该用户的赛事方ID/渠道方ID/业务类型,填充到 {@link JstLoginContext}。
 * <p>
 * 接口下沉到 jst-common 是为了避免 jst-common 反向依赖 jst-user,
 * 由 Spring 自动按类型注入实现。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstLoginEnricher {

    /**
     * 根据 userId 充实登录上下文
     * @param userId 当前登录用户ID
     * @param ctx    待填充的上下文(已含 userId/roleKeys)
     */
    void enrich(Long userId, JstLoginContext ctx);
}
