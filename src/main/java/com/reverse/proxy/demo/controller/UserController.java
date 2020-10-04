package com.reverse.proxy.demo.controller;

import com.reverse.proxy.demo.entity.User;
import com.reverse.proxy.demo.entity.UserTrackingDetails;
import com.reverse.proxy.demo.response.ErrorResponse;
import com.reverse.proxy.demo.service.UserService;
import com.reverse.proxy.demo.service.UserTrackingService;
import com.reverse.proxy.demo.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserTrackingService userTrackingService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @PostMapping(path="/register", consumes = "application/json")
    public ResponseEntity registerUser(@RequestBody User user) {
         User registeredUser = userService.registerUser(user);
         if(null == registeredUser) {
             ErrorResponse errorResponse = getErrorResponse();
             return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
         }

       // final User userDetails = userService.findByPhoneNumber(user.getUserName());
        final String jwt = jwtTokenUtil.generateToken(registeredUser);
         registeredUser.setAccessToken(jwt);
        return new ResponseEntity<>(user, HttpStatus.OK);

        //return new ResponseEntity<>(user, HttpStatus.OK);
    }

    private ErrorResponse getErrorResponse() {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("Exception occurred");
        return errorResponse;
    }

    @GetMapping("/test")
    public String testHello() {
        return "Hello World!" ;
    }

    @GetMapping("/getAPIData")
    public ResponseEntity getDataForDashBoard(String userId, String trackingId) {
        if(null == userId || null == trackingId) {
            ErrorResponse errorResponse = getErrorResponse();
            return new ResponseEntity(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        UserTrackingDetails userTrackingDetails = userTrackingService.getTrackingDetails(Long.valueOf(userId));
        if (null == userTrackingDetails) {
            ErrorResponse errorResponse = getErrorResponse();
            errorResponse.setMessage("No record found for user");
            return new ResponseEntity(errorResponse, HttpStatus.OK);
        }
        return new ResponseEntity(userTrackingDetails, HttpStatus.OK);
    }

    @GetMapping("/application-data")
    public Map getStatusMetric() {
        return userTrackingService.getSystemStatusMetric();
    }
}
