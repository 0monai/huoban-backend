package com.yupi.usercenter.once;
import java.util.Date;

import com.yupi.usercenter.mapper.UserMapper;
import com.yupi.usercenter.model.domain.User;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public class InsertUsers {

    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入用户
     */
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM=10000000;
        for (int i = 0; i <INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假陌奈");
            user.setUserAccount("fakemonai");
            user.setAvatarUrl("");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setTags("[]");
            user.setPhone("13");
            user.setEmail("13");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("11111111");
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
