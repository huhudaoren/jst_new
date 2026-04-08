package com.ruoyi.jst.event.controller.wx;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.event.dto.WxContestQueryDTO;
import com.ruoyi.jst.event.service.ContestService;
import com.ruoyi.jst.event.vo.CategoryStatVO;
import com.ruoyi.jst.event.vo.WxContestCardVO;
import com.ruoyi.jst.event.vo.WxContestDetailVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序公开赛事 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Anonymous
@Validated
@RestController
@RequestMapping("/jst/wx/contest")
public class WxContestController extends BaseController {

    @Autowired
    private ContestService contestService;

    /**
     * 小程序赛事列表。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public TableDataInfo list(@Valid WxContestQueryDTO query) {
        startPage();
        List<WxContestCardVO> list = contestService.selectWxList(query);
        return getDataTable(list);
    }

    /**
     * 小程序赛事详情。
     *
     * @param contestId 赛事ID
     * @return 详情结果
     */
    @GetMapping("/{contestId}")
    public AjaxResult detail(@PathVariable("contestId") Long contestId) {
        WxContestDetailVO vo = contestService.getWxDetail(contestId);
        return AjaxResult.success(vo);
    }

    /**
     * 首页热门赛事。
     *
     * @param limit 限制数量
     * @return 热门赛事列表
     */
    @GetMapping("/hot")
    public AjaxResult hot(@RequestParam(value = "limit", defaultValue = "6") Integer limit) {
        List<WxContestCardVO> list = contestService.selectHotList(limit);
        return AjaxResult.success(list);
    }

    /**
     * 赛事分类统计。
     *
     * @return 分类统计
     */
    @GetMapping("/categories")
    public AjaxResult categories() {
        List<CategoryStatVO> list = contestService.selectCategoryStats();
        return AjaxResult.success(list);
    }
}
