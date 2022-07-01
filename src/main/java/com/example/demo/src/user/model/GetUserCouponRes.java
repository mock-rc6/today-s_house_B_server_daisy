package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserCouponRes {
    long        couponId;
    String      due;
    String      description;
    String      benefit;
    String      received;
}