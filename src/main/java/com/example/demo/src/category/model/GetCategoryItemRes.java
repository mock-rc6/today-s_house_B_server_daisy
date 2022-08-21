package com.example.demo.src.category.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCategoryItemRes {
    long            itemId;
    long            companyId;
    String          companyName;
    String          itemName;
    String          saleRate;
    String          price;
    int             reviewCnt;
    double          score;
}