package com.reverse.proxy.demo.service;

import com.reverse.proxy.demo.entity.User;
import com.reverse.proxy.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    public User registerUser(User user) {
       return userRepository.save(user);
    }

    @Override
    public User findByUserName(String phoneNumber) {
        return userRepository.findByUserName(phoneNumber);
    }

   /* @Override
    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }
*/

}
