package com.reverse.proxy.demo.repository;

import com.reverse.proxy.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.userName = ?1")
    User findByUserName(String userName);
/*
    @Query("select u from User u where u.userId = ?1")
    User findByUserId(String userId);*/
}
