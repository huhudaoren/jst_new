package com.ruoyi.jst.user.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.crypto.JstCipher;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.common.security.SecurityCheck;
import com.ruoyi.jst.common.util.MaskUtil;
import com.ruoyi.jst.user.domain.JstUser;
import com.ruoyi.jst.user.domain.JstUserAddress;
import com.ruoyi.jst.user.dto.UserAddressSaveDTO;
import com.ruoyi.jst.user.mapper.JstUserAddressMapperExt;
import com.ruoyi.jst.user.mapper.JstUserMapper;
import com.ruoyi.jst.user.service.IJstUserAddressService;
import com.ruoyi.jst.user.vo.UserAddressVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 用户收货地址服务实现
 */
@Service
public class JstUserAddressServiceImpl implements IJstUserAddressService {

    @Autowired
    private JstUserAddressMapperExt userAddressMapperExt;

    @Autowired
    private JstUserMapper jstUserMapper;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    @Autowired
    private JstCipher jstCipher;

    @Override
    public List<UserAddressVO> selectMyList(Long userId) {
        SecurityCheck.assertSameUser(userId);
        assertUserExists(userId);
        return userAddressMapperExt.selectListByUserId(userId).stream()
                .map(this::toVo)
                .toList();
    }

    @Override
    public UserAddressVO getMyDetail(Long userId, Long addressId) {
        SecurityCheck.assertSameUser(userId);
        assertUserExists(userId);
        return toVo(requireAddress(userId, addressId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "用户地址", action = "ADDRESS_CREATE", target = "#{userId}", recordResult = true)
    public UserAddressVO create(Long userId, UserAddressSaveDTO req) {
        SecurityCheck.assertSameUser(userId);
        assertUserExists(userId);
        return jstLockTemplate.execute(lockKey(userId), 3, 5, () -> doCreate(userId, req));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "用户地址", action = "ADDRESS_UPDATE", target = "#{addressId}", recordResult = true)
    public UserAddressVO update(Long userId, Long addressId, UserAddressSaveDTO req) {
        SecurityCheck.assertSameUser(userId);
        assertUserExists(userId);
        return jstLockTemplate.execute(lockKey(userId), 3, 5, () -> doUpdate(userId, addressId, req));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "用户地址", action = "ADDRESS_DELETE", target = "#{addressId}")
    public void delete(Long userId, Long addressId) {
        SecurityCheck.assertSameUser(userId);
        assertUserExists(userId);
        jstLockTemplate.execute(lockKey(userId), 3, 5, () -> {
            requireAddress(userId, addressId);
            if (userAddressMapperExt.softDeleteByIdAndUserId(addressId, userId, operatorName()) == 0) {
                throwDataTampered();
            }
            return null;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "用户地址", action = "ADDRESS_SET_DEFAULT", target = "#{addressId}", recordResult = true)
    public UserAddressVO setDefault(Long userId, Long addressId) {
        SecurityCheck.assertSameUser(userId);
        assertUserExists(userId);
        return jstLockTemplate.execute(lockKey(userId), 3, 5, () -> {
            requireAddress(userId, addressId);
            userAddressMapperExt.clearDefaultByUserId(userId, operatorName());
            if (userAddressMapperExt.setDefaultByIdAndUserId(addressId, userId, operatorName()) == 0) {
                throwDataTampered();
            }
            return toVo(requireAddress(userId, addressId));
        });
    }

    private UserAddressVO doCreate(Long userId, UserAddressSaveDTO req) {
        Date now = DateUtils.getNowDate();
        JstUserAddress address = new JstUserAddress();
        address.setUserId(userId);
        fillAddress(address, req);
        address.setCreateBy(operatorName());
        address.setCreateTime(now);
        address.setUpdateBy(operatorName());
        address.setUpdateTime(now);
        address.setDelFlag("0");
        if (Objects.equals(req.getIsDefault(), 1)) {
            userAddressMapperExt.clearDefaultByUserId(userId, operatorName());
        }
        if (userAddressMapperExt.insertAddress(address) == 0) {
            throwDataTampered();
        }
        return toVo(requireAddress(userId, address.getAddressId()));
    }

    private UserAddressVO doUpdate(Long userId, Long addressId, UserAddressSaveDTO req) {
        JstUserAddress address = requireAddress(userId, addressId);
        fillAddress(address, req);
        address.setUpdateBy(operatorName());
        address.setUpdateTime(DateUtils.getNowDate());
        if (Objects.equals(req.getIsDefault(), 1)) {
            userAddressMapperExt.clearDefaultByUserId(userId, operatorName());
        }
        if (userAddressMapperExt.updateAddress(address) == 0) {
            throwDataTampered();
        }
        return toVo(requireAddress(userId, addressId));
    }

    private void fillAddress(JstUserAddress address, UserAddressSaveDTO req) {
        address.setReceiverName(StringUtils.trim(req.getReceiverName()));
        address.setReceiverMobile(jstCipher.encrypt(StringUtils.trim(req.getReceiverMobile())));
        address.setProvince(StringUtils.trim(req.getProvince()));
        address.setCity(StringUtils.trim(req.getCity()));
        address.setDistrict(StringUtils.trim(req.getDistrict()));
        address.setAddressDetail(StringUtils.trim(req.getAddressDetail()));
        address.setPostalCode(StringUtils.trimToNull(req.getPostalCode()));
        address.setIsDefault(Objects.equals(req.getIsDefault(), 1) ? 1 : 0);
    }

    private UserAddressVO toVo(JstUserAddress address) {
        String mobile = decryptMobile(address.getReceiverMobile());
        UserAddressVO vo = new UserAddressVO();
        vo.setAddressId(address.getAddressId());
        vo.setReceiverName(address.getReceiverName());
        vo.setReceiverMobile(mobile);
        vo.setReceiverMobileMasked(MaskUtil.mobile(mobile));
        vo.setProvince(address.getProvince());
        vo.setCity(address.getCity());
        vo.setDistrict(address.getDistrict());
        vo.setAddressDetail(address.getAddressDetail());
        vo.setPostalCode(address.getPostalCode());
        vo.setIsDefault(address.getIsDefault());
        vo.setCreateTime(address.getCreateTime());
        vo.setUpdateTime(address.getUpdateTime());
        return vo;
    }

    private JstUserAddress requireAddress(Long userId, Long addressId) {
        JstUserAddress address = userAddressMapperExt.selectByIdAndUserId(addressId, userId);
        if (address == null) {
            throw new ServiceException(BizErrorCode.JST_USER_ADDRESS_NOT_FOUND.message(),
                    BizErrorCode.JST_USER_ADDRESS_NOT_FOUND.code());
        }
        return address;
    }

    private JstUser assertUserExists(Long userId) {
        JstUser user = jstUserMapper.selectJstUserByUserId(userId);
        if (user == null) {
            throw new ServiceException(BizErrorCode.JST_USER_NOT_FOUND.message(),
                    BizErrorCode.JST_USER_NOT_FOUND.code());
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new ServiceException(BizErrorCode.JST_USER_DISABLED.message(),
                    BizErrorCode.JST_USER_DISABLED.code());
        }
        return user;
    }

    private String decryptMobile(String mobileCipher) {
        if (StringUtils.isBlank(mobileCipher)) {
            return mobileCipher;
        }
        try {
            return jstCipher.decrypt(mobileCipher);
        } catch (Exception ex) {
            return mobileCipher;
        }
    }

    private String lockKey(Long userId) {
        return "lock:user:address:default:" + userId;
    }

    private String operatorName() {
        Long userId = SecurityUtils.getUserId();
        return userId == null ? "system" : String.valueOf(userId);
    }

    private void throwDataTampered() {
        throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
    }
}
