package com.ruoyi.jst.points.service;

import com.ruoyi.jst.points.dto.AftersaleApplyDTO;
import com.ruoyi.jst.points.dto.AftersaleAuditDTO;
import com.ruoyi.jst.points.dto.AftersaleQueryReqDTO;
import com.ruoyi.jst.points.vo.AftersaleApplyResVO;
import com.ruoyi.jst.points.vo.AftersaleDetailVO;
import com.ruoyi.jst.points.vo.AftersaleListVO;

import java.util.List;

/**
 * 商城售后服务。
 */
public interface MallAftersaleService {

    AftersaleApplyResVO apply(AftersaleApplyDTO req);

    List<AftersaleListVO> selectMyList(String status);

    AftersaleDetailVO getMyDetail(Long refundId);

    List<AftersaleListVO> selectAdminList(AftersaleQueryReqDTO query);

    AftersaleDetailVO getAdminDetail(Long refundId);

    void approve(Long refundId, AftersaleAuditDTO req);

    void reject(Long refundId, AftersaleAuditDTO req);
}
