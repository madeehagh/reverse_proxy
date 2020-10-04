package com.reverse.proxy.demo.service;

import com.reverse.proxy.demo.entity.User;

public interface UserService {
     User registerUser(User user);
     User findByUserName(String phoneNumber);
  //   User findByUserId(String userId);
    }
