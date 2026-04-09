package com.ruoyi.jst.points.service;

import com.ruoyi.jst.points.dto.GoodsQueryReqDTO;
import com.ruoyi.jst.points.dto.GoodsSaveReqDTO;
import com.ruoyi.jst.points.vo.GoodsDetailVO;
import com.ruoyi.jst.points.vo.GoodsListVO;

import java.util.List;

/**
 * 商城商品领域服务。
 *
 * @author jst
 * @since 1.0.0
 */
public interface MallGoodsService {

    List<GoodsListVO> selectAdminList(GoodsQueryReqDTO query);

    List<GoodsListVO> selectWxList(GoodsQueryReqDTO query);

    GoodsDetailVO getDetail(Long goodsId);

    Long save(GoodsSaveReqDTO req);

    void publish(Long goodsId);

    void offline(Long goodsId);

    void remove(Long goodsId);
}
