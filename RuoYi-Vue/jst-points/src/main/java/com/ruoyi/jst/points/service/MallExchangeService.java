package com.ruoyi.jst.points.service;

import com.ruoyi.jst.points.dto.ExchangeApplyDTO;
import com.ruoyi.jst.points.dto.ExchangeQueryReqDTO;
import com.ruoyi.jst.points.dto.ExchangeShipDTO;
import com.ruoyi.jst.points.vo.ExchangeApplyResVO;
import com.ruoyi.jst.points.vo.ExchangeDetailVO;
import com.ruoyi.jst.points.vo.ExchangeListVO;

import java.util.List;

/**
 * 商城兑换领域服务。
 *
 * @author jst
 * @since 1.0.0
 */
public interface MallExchangeService {

    List<ExchangeListVO> selectAdminList(ExchangeQueryReqDTO query);

    List<ExchangeListVO> selectMyList(String status);

    ExchangeDetailVO getAdminDetail(Long exchangeId);

    ExchangeDetailVO getMyDetail(Long exchangeId);

    ExchangeApplyResVO apply(ExchangeApplyDTO req);

    void mockPaySuccess(Long exchangeId);

    void cancel(Long exchangeId);

    void ship(Long exchangeId, ExchangeShipDTO req);

    void complete(Long exchangeId);
}
