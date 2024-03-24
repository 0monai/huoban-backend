package com.yupi.usercenter.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;
    @Test
    void test(){
        RList<String> rList = redissonClient.getList("test-list");
//        rList.add("yupi");
//        System.out.println(rList.get(0));
        rList.remove(0);
    }
}
