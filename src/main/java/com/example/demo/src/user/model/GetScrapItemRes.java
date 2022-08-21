package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetScrapItemRes {
    long        itemId;
    String      thumbnail;
    String      category;
    long        companyId;
    String      companyName;
    double      score;
    String      reviewCnt;
    String      saleRate;
    String      price;
    String      specialPrice;
}