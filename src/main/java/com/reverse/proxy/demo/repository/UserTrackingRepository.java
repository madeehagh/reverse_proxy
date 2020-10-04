package com.reverse.proxy.demo.repository;

import com.reverse.proxy.demo.entity.User;
import com.reverse.proxy.demo.entity.UserTrackingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTrackingRepository extends JpaRepository<UserTrackingDetails, Long> {

    @Query(value = "select u from user_tracking_details u where u.user_user_id = :user_user_id ", nativeQuery = true)
    UserTrackingDetails findByUserUserId(@Param("user_user_id") long user_user_id);

}
