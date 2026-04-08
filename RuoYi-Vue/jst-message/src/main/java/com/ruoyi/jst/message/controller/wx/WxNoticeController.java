package com.ruoyi.jst.message.controller.wx;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.message.dto.WxNoticeQueryDTO;
import com.ruoyi.jst.message.service.NoticeService;
import com.ruoyi.jst.message.vo.WxNoticeCardVO;
import com.ruoyi.jst.message.vo.WxNoticeDetailVO;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序公告公开接口 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Anonymous
@Validated
@RestController
@RequestMapping("/jst/wx/notice")
public class WxNoticeController extends BaseController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 小程序公告分页列表。
     *
     * @param query 查询条件
     * @return 公告卡片分页结果
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 @Anonymous
     */
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(@Validated WxNoticeQueryDTO query) {
        startPage();
        List<WxNoticeCardVO> list = noticeService.selectWxNoticeList(query);
        return getDataTable(list);
    }

    /**
     * 小程序公告详情。
     *
     * @param noticeId 公告ID
     * @return 公告详情
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 @Anonymous
     */
    @Anonymous
    @GetMapping("/{noticeId}")
    public AjaxResult detail(@PathVariable("noticeId") @NotNull Long noticeId) {
        WxNoticeDetailVO vo = noticeService.selectWxNoticeDetail(noticeId);
        return AjaxResult.success(vo);
    }
}
