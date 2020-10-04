package com.reverse.proxy.demo.service;

import com.reverse.proxy.demo.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class UserTrackingServiceImplTest {

    @Autowired
    UserTrackingService userTrackingService;

    User userRequest;

    @BeforeEach
    void setUp() {
        userRequest = getUser();
    }

    @Test
    void updateUserDetails_Test() {
        userTrackingService.updateUserDetails(userRequest, "/register");
        assertTrue(userTrackingService.getTrackingDetails(1L) != null);
    }

    @Test
    void updateSystemMetric_Test() {
        userTrackingService.updateSystemMetric("/register", 200);
        assertTrue(userTrackingService.getSystemStatusMetric().size() > 1);
    }

    @Test
    void getSystemStatusMetric_Test() {
        assertTrue(userTrackingService.getSystemStatusMetric().size() > 1);
    }

    @Test
    void isRequestLimitPermitted_Test() {
        assertEquals(userTrackingService.isRequestLimitPermitted(userRequest, "test"), true);
    }

    private User getUser() {
        User user = new User();
        user.setUserId(1);
        user.setUserName("maddy maddy");
        user.setPhoneNumber("9916154781");
        user.getCreatedAt();
        return user;
    }
}