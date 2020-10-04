package com.reverse.proxy.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data

@JsonIgnoreProperties
@EnableAutoConfiguration
@Table(name = "user_tracking_details")
public class UserTrackingDetails {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int trackingId;
    @OneToOne(targetEntity = User.class, cascade = CascadeType.REFRESH)
    private User user;
    @Column(columnDefinition="text")
    private String apiRequested;
    @Column(columnDefinition = "")
    private int count;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date accessedOn;

    @PrePersist
    public void onCreate() {
        accessedOn = new Date();
    }

    @PreUpdate
    public void onUpdate() {
        accessedOn = new Date();
    }

    public UserTrackingDetails(){}

    public UserTrackingDetails(String apiName, int count, User user) {
        this.apiRequested = apiName;
        this.count = count;
        this.user = user;
    }
}
