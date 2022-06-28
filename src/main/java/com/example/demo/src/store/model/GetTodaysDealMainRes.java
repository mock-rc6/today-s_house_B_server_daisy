package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
    int         reviewNum;
    double      score;
}