package com.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LoginLogServiceTest {
   @Autowired
   private LoginLogService loginLogService;

    @Test
    public void test() {
       String  ip = "202.108.22.5";
       String location = loginLogService.resolveIpLocation(ip);
       System.out.println(location);
    }
}
