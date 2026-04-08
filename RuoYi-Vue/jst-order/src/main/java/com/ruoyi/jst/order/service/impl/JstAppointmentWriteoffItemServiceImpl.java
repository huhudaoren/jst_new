package com.ruoyi.jst.order.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.order.mapper.JstAppointmentWriteoffItemMapper;
import com.ruoyi.jst.order.domain.JstAppointmentWriteoffItem;
import com.ruoyi.jst.order.service.IJstAppointmentWriteoffItemService;

/**
 * 核销子项（团队部分核销/到场/礼品/仪式独立流转）Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstAppointmentWriteoffItemServiceImpl implements IJstAppointmentWriteoffItemService 
{
    @Autowired
    private JstAppointmentWriteoffItemMapper jstAppointmentWriteoffItemMapper;

    /**
     * 查询核销子项（团队部分核销/到场/礼品/仪式独立流转）
     * 
     * @param writeoffItemId 核销子项（团队部分核销/到场/礼品/仪式独立流转）主键
     * @return 核销子项（团队部分核销/到场/礼品/仪式独立流转）
     */
    @Override
    public JstAppointmentWriteoffItem selectJstAppointmentWriteoffItemByWriteoffItemId(Long writeoffItemId)
    {
        return jstAppointmentWriteoffItemMapper.selectJstAppointmentWriteoffItemByWriteoffItemId(writeoffItemId);
    }

    /**
     * 查询核销子项（团队部分核销/到场/礼品/仪式独立流转）列表
     * 
     * @param jstAppointmentWriteoffItem 核销子项（团队部分核销/到场/礼品/仪式独立流转）
     * @return 核销子项（团队部分核销/到场/礼品/仪式独立流转）
     */
    @Override
    public List<JstAppointmentWriteoffItem> selectJstAppointmentWriteoffItemList(JstAppointmentWriteoffItem jstAppointmentWriteoffItem)
    {
        return jstAppointmentWriteoffItemMapper.selectJstAppointmentWriteoffItemList(jstAppointmentWriteoffItem);
    }

    /**
     * 新增核销子项（团队部分核销/到场/礼品/仪式独立流转）
     * 
     * @param jstAppointmentWriteoffItem 核销子项（团队部分核销/到场/礼品/仪式独立流转）
     * @return 结果
     */
    @Override
    public int insertJstAppointmentWriteoffItem(JstAppointmentWriteoffItem jstAppointmentWriteoffItem)
    {
        jstAppointmentWriteoffItem.setCreateTime(DateUtils.getNowDate());
        return jstAppointmentWriteoffItemMapper.insertJstAppointmentWriteoffItem(jstAppointmentWriteoffItem);
    }

    /**
     * 修改核销子项（团队部分核销/到场/礼品/仪式独立流转）
     * 
     * @param jstAppointmentWriteoffItem 核销子项（团队部分核销/到场/礼品/仪式独立流转）
     * @return 结果
     */
    @Override
    public int updateJstAppointmentWriteoffItem(JstAppointmentWriteoffItem jstAppointmentWriteoffItem)
    {
        jstAppointmentWriteoffItem.setUpdateTime(DateUtils.getNowDate());
        return jstAppointmentWriteoffItemMapper.updateJstAppointmentWriteoffItem(jstAppointmentWriteoffItem);
    }

    /**
     * 批量删除核销子项（团队部分核销/到场/礼品/仪式独立流转）
     * 
     * @param writeoffItemIds 需要删除的核销子项（团队部分核销/到场/礼品/仪式独立流转）主键
     * @return 结果
     */
    @Override
    public int deleteJstAppointmentWriteoffItemByWriteoffItemIds(Long[] writeoffItemIds)
    {
        return jstAppointmentWriteoffItemMapper.deleteJstAppointmentWriteoffItemByWriteoffItemIds(writeoffItemIds);
    }

    /**
     * 删除核销子项（团队部分核销/到场/礼品/仪式独立流转）信息
     * 
     * @param writeoffItemId 核销子项（团队部分核销/到场/礼品/仪式独立流转）主键
     * @return 结果
     */
    @Override
    public int deleteJstAppointmentWriteoffItemByWriteoffItemId(Long writeoffItemId)
    {
        return jstAppointmentWriteoffItemMapper.deleteJstAppointmentWriteoffItemByWriteoffItemId(writeoffItemId);
    }
}
