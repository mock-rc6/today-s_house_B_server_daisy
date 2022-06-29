package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetMyReviewsRes {
    long            userId;
    String          userName;
    String          profilePic;
    double          score;
    String            createdAt;
    String       buyAt;
    String       itemName;
    String           description;
    List<String>    imgList;
}