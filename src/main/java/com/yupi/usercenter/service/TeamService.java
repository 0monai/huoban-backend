package com.yupi.usercenter.service;

import com.yupi.usercenter.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.dto.TeamQuery;
import com.yupi.usercenter.model.request.TeamJoinRequest;
import com.yupi.usercenter.model.request.TeamQuitRequest;
import com.yupi.usercenter.model.request.TeamUpdateRequest;
import com.yupi.usercenter.model.vo.TeamUserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author 陌奈
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-03-17 14:41:35
*/
public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     *
     * @param team
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */

    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser);

    /**
     * 用户加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 用户退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);
    /**
     * 删除队伍
     * @param teamId
     * @return
     */
    boolean deleteTeam(long id,User loginUser);
}
