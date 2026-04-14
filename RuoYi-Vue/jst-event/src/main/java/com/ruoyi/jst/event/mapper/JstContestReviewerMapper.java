package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.JstContestReviewer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 赛事评审老师 Mapper 接口。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstContestReviewerMapper {

    /**
     * 根据赛事ID查询评审老师列表。
     *
     * @param contestId 赛事ID
     * @return 评审老师列表
     * @关联表 jst_contest_reviewer
     */
    List<JstContestReviewer> selectByContestId(@Param("contestId") Long contestId);

    /**
     * 查询用户负责评审的赛事ID列表。
     *
     * @param userId 用户ID
     * @return 赛事ID列表
     * @关联表 jst_contest_reviewer
     */
    List<Long> selectContestIdsByUserId(@Param("userId") Long userId);

    /**
     * 新增评审老师。
     *
     * @param entity 评审老师
     * @return 影响行数
     * @关联表 jst_contest_reviewer
     */
    int insertReviewer(@Param("entity") JstContestReviewer entity);

    /**
     * 批量新增评审老师（忽略已存在的唯一键冲突）。
     *
     * @param list 评审老师列表
     * @return 影响行数
     * @关联表 jst_contest_reviewer
     */
    int batchInsertIgnore(@Param("list") List<JstContestReviewer> list);

    /**
     * 删除赛事下某个评审老师。
     *
     * @param contestId 赛事ID
     * @param userId    用户ID
     * @return 影响行数
     * @关联表 jst_contest_reviewer
     */
    int deleteByContestAndUser(@Param("contestId") Long contestId, @Param("userId") Long userId);

    /**
     * 删除赛事下所有评审老师。
     *
     * @param contestId 赛事ID
     * @return 影响行数
     * @关联表 jst_contest_reviewer
     */
    int deleteByContestId(@Param("contestId") Long contestId);

    /**
     * 更新评审老师角色。
     *
     * @param id   主键
     * @param role 角色
     * @return 影响行数
     * @关联表 jst_contest_reviewer
     */
    int updateRole(@Param("id") Long id, @Param("role") String role);
}
