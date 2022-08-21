package com.example.demo.src.main.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMyProfileRes {
    String      name;
    String      profilePic;
    int         follows;
    int         followers;
    int         likes;
    int         scraps;
    int         orderHistory;
    int         coupons;
    int         points;
    int         inquiry;
    int         myReviews;
}