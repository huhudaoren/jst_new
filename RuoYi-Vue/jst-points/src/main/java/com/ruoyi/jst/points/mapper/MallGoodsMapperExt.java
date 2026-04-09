package com.ruoyi.jst.points.mapper;

import com.ruoyi.jst.points.domain.JstMallGoods;
import com.ruoyi.jst.points.dto.GoodsQueryReqDTO;
import com.ruoyi.jst.points.vo.GoodsDetailVO;
import com.ruoyi.jst.points.vo.GoodsListVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 商城商品扩展 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface MallGoodsMapperExt {

    List<GoodsListVO> selectAdminList(GoodsQueryReqDTO query);

    List<GoodsListVO> selectWxList(GoodsQueryReqDTO query);

    GoodsDetailVO selectDetail(@Param("goodsId") Long goodsId);

    JstMallGoods selectByIdForUpdate(@Param("goodsId") Long goodsId);

    int updateStatus(@Param("goodsId") Long goodsId,
                     @Param("expectedStatus") String expectedStatus,
                     @Param("targetStatus") String targetStatus,
                     @Param("updateBy") String updateBy,
                     @Param("updateTime") Date updateTime);

    int softDelete(@Param("goodsId") Long goodsId,
                   @Param("updateBy") String updateBy,
                   @Param("updateTime") Date updateTime);

    int decreaseStock(@Param("goodsId") Long goodsId,
                      @Param("quantity") Integer quantity,
                      @Param("updateBy") String updateBy,
                      @Param("updateTime") Date updateTime);

    int restoreStock(@Param("goodsId") Long goodsId,
                     @Param("quantity") Integer quantity,
                     @Param("updateBy") String updateBy,
                     @Param("updateTime") Date updateTime);
}
