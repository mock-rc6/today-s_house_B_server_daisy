package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostOrderReq {
    long                userId;
    List<Long>          kartId;
    int                 points;
    long                couponId;
    String              orderName;
    String              phoneNum;
    String              email;
    String              receivedName;
    String              receivedPhone;
    String              placeName;
    String              addressCode;
    String              address;
}