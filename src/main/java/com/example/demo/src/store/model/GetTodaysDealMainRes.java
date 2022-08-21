package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetTodaysDealMainRes {
    long        itemId;
    String      companyName;
    long        companyId;
    String      due;
    String      subCategory;
    long        subCategoryId;
    String      saleRate;
    String      price;
    String      itemName;
    int         reviewNum;
    double      score;
    String      hotDealThumbnail;
}