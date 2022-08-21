package com.example.demo.src.main.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostReviewReq {
    long    userId;
    long    optionId;
    int     score;
    String  reviewDescription;
}