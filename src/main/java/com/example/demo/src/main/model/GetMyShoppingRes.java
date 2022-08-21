package com.example.demo.src.main.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMyShoppingRes {
    int             coupons;
    int             points;
    String          level;
    int             waiting;
    int             paid;
    int             ready;
    int             delivery;
    int             finish;
    int             reviewWritten;
    int             bought;
    int             review;
    int             inquiry;
    int             scraps;
}