package com.reverse.proxy.demo.entity;

import lombok.Data;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "userName")})
@Data
@EnableAutoConfiguration
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userId;
    @Column(columnDefinition = "text")
    private String userName;
    @Column(columnDefinition = "text")
    private String phoneNumber;
    @Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
    @Transient
    private String accessToken;

    @PrePersist
    public void onCreate() {
        createdAt = new Date();
    }
}
