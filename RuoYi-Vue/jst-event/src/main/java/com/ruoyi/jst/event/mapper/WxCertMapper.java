package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.dto.PublicCertVerifyQueryDTO;
import com.ruoyi.jst.event.vo.PublicCertVerifyVO;
import com.ruoyi.jst.event.vo.WxCertVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper for mini-program certificate queries.
 */
public interface WxCertMapper {

    List<WxCertVO> selectMyCertList(@Param("userId") Long userId);

    PublicCertVerifyVO selectPublicCertVerify(PublicCertVerifyQueryDTO query);
}
