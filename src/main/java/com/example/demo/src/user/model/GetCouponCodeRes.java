package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCouponCodeRes {
    String      due;
    String      description;
    int         saleAmount;
    int         saleRate;
}