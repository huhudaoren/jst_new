package com.ruoyi.jst.points.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.points.domain.JstMallGoods;
import com.ruoyi.jst.points.dto.GoodsQueryReqDTO;
import com.ruoyi.jst.points.dto.GoodsSaveReqDTO;
import com.ruoyi.jst.points.enums.MallGoodsStatus;
import com.ruoyi.jst.points.enums.MallGoodsType;
import com.ruoyi.jst.points.enums.MallVirtualTargetType;
import com.ruoyi.jst.points.mapper.JstMallGoodsMapper;
import com.ruoyi.jst.points.mapper.MallGoodsMapperExt;
import com.ruoyi.jst.points.mapper.lookup.UserCouponLookupMapper;
import com.ruoyi.jst.points.mapper.lookup.UserRightsLookupMapper;
import com.ruoyi.jst.points.service.MallGoodsService;
import com.ruoyi.jst.points.vo.GoodsDetailVO;
import com.ruoyi.jst.points.vo.GoodsListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商城商品领域服务实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class MallGoodsServiceImpl implements MallGoodsService {

    @Autowired
    private JstMallGoodsMapper jstMallGoodsMapper;

    @Autowired
    private MallGoodsMapperExt mallGoodsMapperExt;

    @Autowired
    private UserCouponLookupMapper userCouponLookupMapper;

    @Autowired
    private UserRightsLookupMapper userRightsLookupMapper;

    /**
     * 查询后台商品列表。
     *
     * @param query 查询条件
     * @return 商品列表
     */
    @Override
    public List<GoodsListVO> selectAdminList(GoodsQueryReqDTO query) {
        return mallGoodsMapperExt.selectAdminList(query == null ? new GoodsQueryReqDTO() : query);
    }

    /**
     * 查询小程序商品列表。
     *
     * @param query 查询条件
     * @return 商品列表
     */
    @Override
    public List<GoodsListVO> selectWxList(GoodsQueryReqDTO query) {
        return mallGoodsMapperExt.selectWxList(query == null ? new GoodsQueryReqDTO() : query);
    }

    /**
     * 查询商品详情。
     *
     * @param goodsId 商品ID
     * @return 商品详情
     */
    @Override
    public GoodsDetailVO getDetail(Long goodsId) {
        GoodsDetailVO detail = mallGoodsMapperExt.selectDetail(goodsId);
        if (detail == null) {
            throw new ServiceException(BizErrorCode.JST_MALL_GOODS_NOT_FOUND.message(),
                    BizErrorCode.JST_MALL_GOODS_NOT_FOUND.code());
        }
        return detail;
    }

    /**
     * 保存商品草稿。
     *
     * @param req 保存请求
     * @return 商品ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "积分商城", action = "GOODS_SAVE", target = "#{req.goodsId}", recordResult = true)
    public Long save(GoodsSaveReqDTO req) {
        validateSaveReq(req);
        MallGoodsType goodsType = MallGoodsType.fromDb(req.getGoodsType());
        String operator = operatorName();
        JstMallGoods entity = req.getGoodsId() == null ? new JstMallGoods() : requireGoods(req.getGoodsId());
        entity.setGoodsName(req.getGoodsName());
        entity.setGoodsType(goodsType.dbValue());
        entity.setCoverImage(req.getCoverImage());
        entity.setDescription(req.getDescription());
        entity.setPointsPrice(defaultPoints(req.getPointsPrice()));
        entity.setCashPrice(defaultAmount(req.getCashPrice()));
        entity.setStock(req.getStock() == null ? null : req.getStock().longValue());
        entity.setStockWarning(req.getStockWarning() == null ? null : req.getStockWarning().longValue());
        entity.setRoleLimit(req.getRoleLimit());
        entity.setRemark(buildVirtualConfigRemark(req, goodsType));
        entity.setUpdateBy(operator);
        entity.setUpdateTime(DateUtils.getNowDate());
        if (req.getGoodsId() == null) {
            entity.setStatus(MallGoodsStatus.DRAFT.dbValue());
            entity.setCreateBy(operator);
            entity.setCreateTime(DateUtils.getNowDate());
            entity.setDelFlag("0");
            jstMallGoodsMapper.insertJstMallGoods(entity);
        } else {
            jstMallGoodsMapper.updateJstMallGoods(entity);
        }
        return entity.getGoodsId();
    }

    /**
     * 发布商品。
     *
     * @param goodsId 商品ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "积分商城", action = "GOODS_PUBLISH", target = "#{goodsId}")
    public void publish(Long goodsId) {
        JstMallGoods goods = requireGoods(goodsId);
        MallGoodsStatus current = MallGoodsStatus.fromDb(goods.getStatus());
        if (current == MallGoodsStatus.ON) {
            return;
        }
        validatePublishGoods(goods);
        if (mallGoodsMapperExt.updateStatus(goodsId, current.dbValue(), MallGoodsStatus.ON.dbValue(),
                operatorName(), DateUtils.getNowDate()) == 0) {
            throwDataTampered();
        }
    }

    /**
     * 下架商品。
     *
     * @param goodsId 商品ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "积分商城", action = "GOODS_OFFLINE", target = "#{goodsId}")
    public void offline(Long goodsId) {
        JstMallGoods goods = requireGoods(goodsId);
        MallGoodsStatus current = MallGoodsStatus.fromDb(goods.getStatus());
        if (current == MallGoodsStatus.OFF) {
            return;
        }
        if (mallGoodsMapperExt.updateStatus(goodsId, current.dbValue(), MallGoodsStatus.OFF.dbValue(),
                operatorName(), DateUtils.getNowDate()) == 0) {
            throwDataTampered();
        }
    }

    /**
     * 删除商品。
     *
     * @param goodsId 商品ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "积分商城", action = "GOODS_REMOVE", target = "#{goodsId}")
    public void remove(Long goodsId) {
        JstMallGoods goods = requireGoods(goodsId);
        if (MallGoodsStatus.fromDb(goods.getStatus()) == MallGoodsStatus.ON) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (mallGoodsMapperExt.softDelete(goodsId, operatorName(), DateUtils.getNowDate()) == 0) {
            throwDataTampered();
        }
    }

    private JstMallGoods requireGoods(Long goodsId) {
        JstMallGoods goods = jstMallGoodsMapper.selectJstMallGoodsByGoodsId(goodsId);
        if (goods == null || !"0".equals(defaultDelFlag(goods.getDelFlag()))) {
            throw new ServiceException(BizErrorCode.JST_MALL_GOODS_NOT_FOUND.message(),
                    BizErrorCode.JST_MALL_GOODS_NOT_FOUND.code());
        }
        return goods;
    }

    private void validateSaveReq(GoodsSaveReqDTO req) {
        MallGoodsType goodsType = MallGoodsType.fromDb(req.getGoodsType());
        if (!"student".equals(req.getRoleLimit()) && !"channel".equals(req.getRoleLimit())
                && !"both".equals(req.getRoleLimit())) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (defaultPoints(req.getPointsPrice()) == 0 && defaultAmount(req.getCashPrice()).compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (goodsType == MallGoodsType.PHYSICAL) {
            if (req.getStock() == null || req.getStock() < 0) {
                throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                        BizErrorCode.JST_COMMON_PARAM_INVALID.code());
            }
            return;
        }
        if (req.getStock() == null || req.getStock() < -1) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        MallVirtualTargetType targetType = MallVirtualTargetType.fromDb(req.getVirtualTargetType());
        if (!targetType.supported()) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (targetType == MallVirtualTargetType.COUPON && userCouponLookupMapper.selectCouponTemplate(req.getCouponTemplateId()) == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (targetType == MallVirtualTargetType.RIGHTS && userRightsLookupMapper.selectRightsTemplate(req.getRightsTemplateId()) == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
    }

    private void validatePublishGoods(JstMallGoods goods) {
        MallGoodsType goodsType = MallGoodsType.fromDb(goods.getGoodsType());
        if (goodsType == MallGoodsType.PHYSICAL) {
            if (goods.getStock() == null || goods.getStock() < 0) {
                throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                        BizErrorCode.JST_COMMON_PARAM_INVALID.code());
            }
            return;
        }
        if (StringUtils.isBlank(goods.getRemark())) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
    }

    private String buildVirtualConfigRemark(GoodsSaveReqDTO req, MallGoodsType goodsType) {
        if (goodsType == MallGoodsType.PHYSICAL) {
            return null;
        }
        MallVirtualTargetType targetType = MallVirtualTargetType.fromDb(req.getVirtualTargetType());
        Map<String, Object> config = new HashMap<>(4);
        config.put("virtualTargetType", targetType.dbValue());
        if (targetType == MallVirtualTargetType.COUPON) {
            config.put("couponTemplateId", req.getCouponTemplateId());
        }
        if (targetType == MallVirtualTargetType.RIGHTS) {
            config.put("rightsTemplateId", req.getRightsTemplateId());
        }
        return JSON.toJSONString(config);
    }

    private Long defaultPoints(Long points) {
        return points == null ? 0L : points;
    }

    private BigDecimal defaultAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private String operatorName() {
        Long userId = SecurityUtils.getUserId();
        return userId == null ? "system" : String.valueOf(userId);
    }

    private String defaultDelFlag(String delFlag) {
        return StringUtils.isBlank(delFlag) ? "0" : delFlag;
    }

    private void throwDataTampered() {
        throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
    }
}
