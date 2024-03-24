package com.yupi.usercenter;

import com.yupi.usercenter.model.domain.User;
import jakarta.annotation.Resource;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //增
        valueOperations.set("String","dog");
        valueOperations.set("Int",1);
        valueOperations.set("Double",2.0);
        User user = new User();
        user.setId(1L);
        user.setUsername("001");
        valueOperations.set("User",user);
        //查
        Object obj = valueOperations.get("String");
        Assertions.assertTrue("dog".equals((String) obj));
        obj=valueOperations.get("Int");
        Assertions.assertTrue(1==(Integer) obj);
        obj=valueOperations.get("Double");
        Assertions.assertTrue(2.0==(Double) obj);
        System.out.println(valueOperations.get("User"));


    }
}
