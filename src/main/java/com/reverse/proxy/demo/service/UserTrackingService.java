package com.reverse.proxy.demo.service;

import com.reverse.proxy.demo.entity.User;
import com.reverse.proxy.demo.entity.UserTrackingDetails;

import java.util.Map;

public interface UserTrackingService {
    void updateUserDetails(User user, String api);

    void updateSystemMetric(String req, int status);

    boolean isRequestLimitPermitted(User user, String requestURI);

    Map getSystemStatusMetric();

    UserTrackingDetails getTrackingDetails(long userId);
}
