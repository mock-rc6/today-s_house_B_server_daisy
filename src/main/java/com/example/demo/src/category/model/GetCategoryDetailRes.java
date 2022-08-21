package com.example.demo.src.category.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetCategoryDetailRes {
    long                            miniCategoryId;
    List<GetCategory>               smallestCategoryList;
    List<GetCategoryEventsRes>      categoryEventList;
    List<GetCategoryItemRes>        MDPickItemList;
    List<GetCategoryItemRes>        miniCategoryItemList;
}