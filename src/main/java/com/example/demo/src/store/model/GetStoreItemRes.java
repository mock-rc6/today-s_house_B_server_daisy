package com.example.demo.src.store.model;

import com.example.demo.src.review.model.GetMyReviewsRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreItemRes {
    String                      itemName;
    List<String>                imgList;
    long                        companyId;
    String                      companyName;
    double                      score;
    int                         reviewCnt;
    String                      saleRate;
    String                      price;
    int                         scrapCnt;
    List<String>                itemInfoList;
    List<GetMyReviewsRes>       reviewList;
    int                         five;
    int                         four;
    int                         three;
    int                         two;
    int                         one;
    int                         inquiry;
}