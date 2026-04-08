package com.ruoyi.jst.order.mapper;

import java.util.List;
import com.ruoyi.jst.order.domain.JstAppointmentWriteoffItem;

/**
 * 核销子项（团队部分核销/到场/礼品/仪式独立流转）Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstAppointmentWriteoffItemMapper 
{
    /**
     * 查询核销子项（团队部分核销/到场/礼品/仪式独立流转）
     * 
     * @param writeoffItemId 核销子项（团队部分核销/到场/礼品/仪式独立流转）主键
     * @return 核销子项（团队部分核销/到场/礼品/仪式独立流转）
     */
    public JstAppointmentWriteoffItem selectJstAppointmentWriteoffItemByWriteoffItemId(Long writeoffItemId);

    /**
     * 查询核销子项（团队部分核销/到场/礼品/仪式独立流转）列表
     * 
     * @param jstAppointmentWriteoffItem 核销子项（团队部分核销/到场/礼品/仪式独立流转）
     * @return 核销子项（团队部分核销/到场/礼品/仪式独立流转）集合
     */
    public List<JstAppointmentWriteoffItem> selectJstAppointmentWriteoffItemList(JstAppointmentWriteoffItem jstAppointmentWriteoffItem);

    /**
     * 新增核销子项（团队部分核销/到场/礼品/仪式独立流转）
     * 
     * @param jstAppointmentWriteoffItem 核销子项（团队部分核销/到场/礼品/仪式独立流转）
     * @return 结果
     */
    public int insertJstAppointmentWriteoffItem(JstAppointmentWriteoffItem jstAppointmentWriteoffItem);

    /**
     * 修改核销子项（团队部分核销/到场/礼品/仪式独立流转）
     * 
     * @param jstAppointmentWriteoffItem 核销子项（团队部分核销/到场/礼品/仪式独立流转）
     * @return 结果
     */
    public int updateJstAppointmentWriteoffItem(JstAppointmentWriteoffItem jstAppointmentWriteoffItem);

    /**
     * 删除核销子项（团队部分核销/到场/礼品/仪式独立流转）
     * 
     * @param writeoffItemId 核销子项（团队部分核销/到场/礼品/仪式独立流转）主键
     * @return 结果
     */
    public int deleteJstAppointmentWriteoffItemByWriteoffItemId(Long writeoffItemId);

    /**
     * 批量删除核销子项（团队部分核销/到场/礼品/仪式独立流转）
     * 
     * @param writeoffItemIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstAppointmentWriteoffItemByWriteoffItemIds(Long[] writeoffItemIds);
}
