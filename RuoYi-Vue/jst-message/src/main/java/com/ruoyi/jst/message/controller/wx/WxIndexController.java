package com.ruoyi.jst.message.controller.wx;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.jst.message.service.NoticeService;
import com.ruoyi.jst.message.vo.BannerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序首页公开接口 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Anonymous
@RestController
@RequestMapping("/jst/wx/index")
public class WxIndexController extends BaseController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 首页 banner 列表。
     *
     * @return banner 列表
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 @Anonymous
     */
    @Anonymous
    @GetMapping("/banner")
    public AjaxResult banner() {
        List<BannerVO> list = noticeService.selectBannerList();
        return AjaxResult.success(list);
    }
}
