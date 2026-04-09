package com.ruoyi.jst.order.api;

import com.ruoyi.jst.order.dto.AppointmentOrderCreateReqDTO;
import com.ruoyi.jst.order.vo.AppointmentOrderCreateResVO;

/**
 * 预约子订单创建门面。
 * <p>
 * 供 jst-event 等外部模块通过 Spring 注入调用，避免直接依赖 jst-order 的 service 实现。
 *
 * @author jst
 * @since 1.0.0
 */
public interface OrderCreationFacade {

    /**
     * 创建团队/个人预约产生的子订单。
     *
     * @param req 下单快照
     * @return 创建结果
     */
    AppointmentOrderCreateResVO createAppointmentOrder(AppointmentOrderCreateReqDTO req);
}
