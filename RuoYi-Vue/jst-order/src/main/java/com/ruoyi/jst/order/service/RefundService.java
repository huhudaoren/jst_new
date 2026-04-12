package com.ruoyi.jst.order.service;

import com.ruoyi.jst.order.dto.RefundApplyDTO;
import com.ruoyi.jst.order.dto.RefundAuditDTO;
import com.ruoyi.jst.order.dto.RefundQueryReqDTO;
import com.ruoyi.jst.order.vo.RefundDetailVO;
import com.ruoyi.jst.order.vo.RefundListVO;

import java.util.List;
import java.util.Map;

/**
 * 退款全流程领域服务。
 *
 * @author jst
 * @since 1.0.0
 */
public interface RefundService {

    /**
     * 学生发起退款申请。
     *
     * @param orderId 订单ID
     * @param req     申请入参
     * @return refundId / refundNo
     */
    Map<String, Object> apply(Long orderId, RefundApplyDTO req);

    /**
     * 平台发起特批退款申请。
     *
     * @param orderId 订单ID
     * @param req     特批备注
     * @return refundId / refundNo
     */
    Map<String, Object> specialRefund(Long orderId, RefundAuditDTO req);

    /**
     * 查询学生侧退款详情。
     *
     * @param refundId 退款单ID
     * @return 详情
     */
    RefundDetailVO getWxDetail(Long refundId);

    /**
     * 查询学生侧退款列表。
     *
     * @param query 查询条件
     * @return 列表
     */
    List<RefundListVO> selectMyList(RefundQueryReqDTO query);

    /**
     * 学生撤销退款申请。
     *
     * @param refundId 退款单ID
     */
    void cancel(Long refundId);

    /**
     * 查询后台退款列表。
     *
     * @param query 查询条件
     * @return 列表
     */
    List<RefundListVO> selectAdminList(RefundQueryReqDTO query);

    /**
     * 审核通过退款申请。
     *
     * @param refundId 退款单ID
     * @param req      审核备注
     */
    void approve(Long refundId, RefundAuditDTO req);

    /**
     * 驳回退款申请。
     *
     * @param refundId 退款单ID
     * @param req      审核备注
     */
    void reject(Long refundId, RefundAuditDTO req);

    /**
     * 系统任务关闭超时未审核退款申请。
     *
     * @param refundId 退款单ID
     * @param remark   关闭备注
     * @return true=成功关闭；false=退款单状态已变化
     */
    boolean closeTimeoutPending(Long refundId, String remark);

    /**
     * 执行退款资金回退。
     *
     * @param refundId 退款单ID
     */
    void execute(Long refundId);

    /**
     * 处理微信退款回调。
     *
     * @param body    回调报文
     * @param headers 请求头
     * @return 微信响应
     */
    String handleWechatRefundNotify(String body, Map<String, String> headers);
}
