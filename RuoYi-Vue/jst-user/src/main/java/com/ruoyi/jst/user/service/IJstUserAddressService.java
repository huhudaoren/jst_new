package com.ruoyi.jst.user.service;

import com.ruoyi.jst.user.dto.UserAddressSaveDTO;
import com.ruoyi.jst.user.vo.UserAddressVO;

import java.util.List;

/**
 * 用户收货地址服务
 */
public interface IJstUserAddressService {

    List<UserAddressVO> selectMyList(Long userId);

    UserAddressVO getMyDetail(Long userId, Long addressId);

    UserAddressVO create(Long userId, UserAddressSaveDTO req);

    UserAddressVO update(Long userId, Long addressId, UserAddressSaveDTO req);

    void delete(Long userId, Long addressId);

    UserAddressVO setDefault(Long userId, Long addressId);
}
