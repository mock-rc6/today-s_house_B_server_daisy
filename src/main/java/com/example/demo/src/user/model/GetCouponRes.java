package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCouponRes {
    long        couponId;
    String      description;
    String      due;
    int         saleAmount; // 금액 할인 쿠폰
    int         saleRate; // 할인률
}