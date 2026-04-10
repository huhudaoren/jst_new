package com.ruoyi.jst.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface UserProfileLookupMapper {

    Map<String, Object> selectProfileStats(@Param("userId") Long userId);

    Map<String, Object> selectChannelBinding(@Param("userId") Long userId);
}
