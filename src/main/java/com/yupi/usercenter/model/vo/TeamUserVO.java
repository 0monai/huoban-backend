package com.yupi.usercenter.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Data
public class TeamUserVO implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *更新时间
     */
    private Date updateTime;


    /**
     * 创建人
     */
    private UserVO createUser;

    /**
     * 已加入人数
     */
    private Integer hasJoinNum;

    /**
     * 是否已加入队伍
     */
    private boolean  hasJoin = false;

}
