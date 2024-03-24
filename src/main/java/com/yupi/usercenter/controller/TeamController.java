package com.yupi.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.usercenter.common.BaseResponse;
import com.yupi.usercenter.common.DeleteRequest;
import com.yupi.usercenter.common.ErrorCode;
import com.yupi.usercenter.common.ResultUtils;
import com.yupi.usercenter.exception.BusinessException;
import com.yupi.usercenter.model.domain.Team;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.domain.UserTeam;
import com.yupi.usercenter.model.dto.TeamQuery;
import com.yupi.usercenter.model.request.TeamAddRequest;
import com.yupi.usercenter.model.request.TeamJoinRequest;
import com.yupi.usercenter.model.request.TeamQuitRequest;
import com.yupi.usercenter.model.request.TeamUpdateRequest;
import com.yupi.usercenter.model.vo.TeamUserVO;
import com.yupi.usercenter.service.TeamService;
import com.yupi.usercenter.service.UserService;
import com.yupi.usercenter.service.UserTeamService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: shayu
 * @date: 2023/01/30
 * @ClassName: yupao-backend01
 * @Description:    队伍controller
 */

@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:5173/"})
@Slf4j
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private TeamService teamService;

    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        if (teamAddRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }


    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,HttpServletRequest request){
        if (teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id){
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }


    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(
                                                     @RequestParam(value = "id", required = false) Long id,
                                                     @RequestParam(value = "searchText", required = false) String searchText,
                                                     @RequestParam(value = "status",required = false) Integer status,
                                                     @RequestParam(value = "name", required = false) String name,
                                                     @RequestParam(value = "description", required =false) String description,
                                                     HttpServletRequest request) {
        TeamQuery teamQuery = new TeamQuery();
        teamQuery.setStatus(status);
        teamQuery.setId(id);
        teamQuery.setSearchText(searchText);
        teamQuery.setName(name);
        teamQuery.setDescription(description);
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
//        List<TeamUserVO> teamList = teamService.listTeams(teamQuery,isAdmin);
//        List<Long> teamIdList = teamList.stream().map(TeamUserVO::getUserId).collect(Collectors.toList());
//        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
//
//        try {
//            User loginUser = userService.getLoginUser(request);
//            userTeamQueryWrapper.eq("userId", loginUser.getId());
//            userTeamQueryWrapper.in("teamId",teamIdList);
//            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
//            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
//            teamList.forEach(team -> {
//                boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
//                team.setHasJoin(hasJoin);
//            });
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        return ResultUtils.success(teamList);

        // 1、查询队伍列表
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin);
        final List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        // 2、判断当前用户是否已加入队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        try {
            User loginUser = userService.getLoginUser(request);
            userTeamQueryWrapper.eq("userId", loginUser.getId());
            userTeamQueryWrapper.in("teamId", teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            // 已加入的队伍 id 集合
            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            teamList.forEach(team -> {
                boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        } catch (Exception e) {}
        // 3查询已加入队伍的人数
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        userTeamJoinQueryWrapper.in("teamId",teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(userTeamJoinQueryWrapper);
        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(team -> {
           team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(),new ArrayList<>()).size());
        });
        return ResultUtils.success(teamList);
    }

//    @GetMapping("/list")
//    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request) {
//        if (teamQuery == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        boolean isAdmin = userService.isAdmin(request);
//        // 1、查询队伍列表
//        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin);
//        final List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
//        // 2、判断当前用户是否已加入队伍
//        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
//        try {
//            User loginUser = userService.getLoginUser(request);
//            userTeamQueryWrapper.eq("userId", loginUser.getId());
//            userTeamQueryWrapper.in("teamId", teamIdList);
//            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
//            // 已加入的队伍 id 集合
//            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
//            teamList.forEach(team -> {
//                boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
//                team.setHasJoin(hasJoin);
//            });
//        } catch (Exception e) {}
//        return ResultUtils.success(teamList);
//    }


    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listPageTeams
        (  @RequestParam("pageNum") Integer pageNum,
           @RequestParam("pageSize") Integer pageSize,
           @RequestParam(value = "id", required = false) Long id,
           @RequestParam(value = "searchText", required = false) String searchText)
    {
        TeamQuery teamQuery = new TeamQuery();
        teamQuery.setPageNum(pageNum);
        teamQuery.setPageSize(pageSize);
        teamQuery.setId(id);
        teamQuery.setSearchText(searchText);
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(),teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page,queryWrapper);
        return ResultUtils.success(resultPage);
    }

    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "searchText", required = false) String searchText,
            @RequestParam(value = "status",required = false) Integer status,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required =false) String description,
            HttpServletRequest request) {
        TeamQuery teamQuery = new TeamQuery();
        teamQuery.setStatus(status);
        teamQuery.setId(id);
        teamQuery.setSearchText(searchText);
        teamQuery.setName(name);
        teamQuery.setDescription(description);
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery,true);
        return ResultUtils.success(teamList);
    }


    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "idList", required = false) List<Long> idList,
            @RequestParam(value = "searchText", required = false) String searchText,
            @RequestParam(value = "status",required = false) Integer status,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required =false) String description,
            HttpServletRequest request) {
        TeamQuery teamQuery = new TeamQuery();
        teamQuery.setStatus(status);
        teamQuery.setId(id);
        teamQuery.setIdList(idList);
        teamQuery.setSearchText(searchText);
        teamQuery.setName(name);
        teamQuery.setDescription(description);
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        //取出不重复的队伍id
        Map<Long, List<UserTeam>> listMap = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> ids = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(ids);

        List<TeamUserVO> teamList = teamService.listTeams(teamQuery,true);
        return ResultUtils.success(teamList);
    }


    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request){
        if (teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result=teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request){
        if (teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result=teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest , HttpServletRequest request){
        if (deleteRequest==null||deleteRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long id=deleteRequest.getId();
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(id,loginUser);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }



}
