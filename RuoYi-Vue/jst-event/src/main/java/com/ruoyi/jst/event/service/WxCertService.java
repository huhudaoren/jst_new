package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.dto.PublicCertVerifyQueryDTO;
import com.ruoyi.jst.event.vo.PublicCertVerifyVO;
import com.ruoyi.jst.event.vo.WxCertVO;

import java.util.List;

/**
 * Service for mini-program certificate pages.
 */
public interface WxCertService {

    List<WxCertVO> selectMyCerts(Long userId);

    PublicCertVerifyVO verifyPublicCert(PublicCertVerifyQueryDTO query);
}
