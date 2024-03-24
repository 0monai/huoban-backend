package com.yupi.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamJoinRequest implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}
