package com.reverse.proxy.demo.service;

import com.reverse.proxy.demo.constants.ProxyConstants;
import com.reverse.proxy.demo.entity.User;
import com.reverse.proxy.demo.entity.UserTrackingDetails;
import com.reverse.proxy.demo.exception.ApplicationException;
import com.reverse.proxy.demo.repository.UserTrackingRepository;
import com.reverse.proxy.demo.response.ErrorResponse;
import com.reverse.proxy.demo.util.ProxyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class UserTrackingServiceImpl implements UserTrackingService{

    @Autowired
    UserTrackingRepository userTrackingRepository;

    private ConcurrentHashMap<Long, UserTrackingDetails> trackingDetails;
    private ConcurrentHashMap<Integer, Integer> systemStatusMetric;
    private HashMap<Long, Long> timestamps; // store time for each user id.

    /**
     * This API update count of API accessed by a User
     * @param user
     * @param api
     */
    @Override
    public void updateUserDetails(User user, String api) {
        if(null != user) {
            Long userId = user.getUserId();
            UserTrackingDetails userTrackingDetails = null;
            if(trackingDetails.containsKey(userId)) {
                userTrackingDetails = trackingDetails.get(userId);
                userTrackingDetails.setApiRequested(api);
                userTrackingDetails.setCount(userTrackingDetails.getCount() + 1);
            } else {
                userTrackingDetails = new UserTrackingDetails(api, 1, user);
            }
            trackingDetails.put(userId,userTrackingDetails);
            timestamps.put(userId, System.currentTimeMillis());
            System.out.println("count " + userTrackingDetails.getCount());
            try {
                userTrackingRepository.save(userTrackingDetails);
            }catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        }
    }

    @Override
    public void updateSystemMetric(String req, int status) {
        systemStatusMetric.put(status, systemStatusMetric.getOrDefault(status, 1) +1);
    }

    /**
     * This API is responsible for system metrics.
     * A user can get total number of hits coming to the system (success/ failed request count for the system)
     * @return
     */
    public ConcurrentMap<Integer, Integer> getSystemStatusMetric() {
        return systemStatusMetric;
    }

    /**
     * This method gives all the counts of APIs accessed by a user
     * Only authorised user can view his access details.
     * @param userID
     * @return
     */
    public UserTrackingDetails getTrackingDetails(long userID) {
        //return userTrackingRepository.Ø(userID) ;
        return trackingDetails.get(userID);
    }

    /**
     * This method checks number of hits by a User in a given time frame.
     * If number of requests exceeds the allowed limit rate, the method does not allow user to access the api.
     * @param user
     * @param requestURI
     * @return
     */
    @Override
    public boolean isRequestLimitPermitted(User user, String requestURI) {
        if(ProxyUtil.byPassAuthRequest(requestURI))
            return true;
        boolean nonExpiredLimit = false;
        Long userId = user.getUserId();
        UserTrackingDetails userTrackingDetails = getItem(userId);
        if(null != userTrackingDetails ) {
            long diff = System.currentTimeMillis()
                    - userTrackingDetails.getAccessedOn().getTime();
            nonExpiredLimit = !(diff <= ProxyConstants.REQUEST_TIME_DIFFERENCE
                    && userTrackingDetails.getCount() > ProxyConstants.REQUEST_COUNT);
        } else
            nonExpiredLimit = true;
        if(nonExpiredLimit)
            updateUserDetails(user, requestURI);
        return nonExpiredLimit;
    }

    /**
     *This method evicts older entry from the map
     * @param userId
     * @return
     */
    private UserTrackingDetails getItem(long userId) {
        if(!timestamps.containsKey(userId) || null == trackingDetails.get(userId))
            return null;
        if(timestamps.containsKey(userId) &&
                (System.currentTimeMillis() - timestamps.get(userId) > ProxyConstants.EXPIRY_TIME)) {
            timestamps.remove(userId);
            trackingDetails.remove(userId);
            return null;
        }
        timestamps.put(userId, System.currentTimeMillis());
        return trackingDetails.get(userId);
    }


    @PostConstruct
    public void load() {
        systemStatusMetric = new ConcurrentHashMap<>();
        trackingDetails = new ConcurrentHashMap<>();
        timestamps = new HashMap<>();
    }
    }