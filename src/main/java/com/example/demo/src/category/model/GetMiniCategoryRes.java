package com.example.demo.src.category.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetMiniCategoryRes {
    String                          miniCategoryName;
    List<GetCategoryEventsRes>      categoryEventList;
    List<GetCategoryItemRes>        itemList;
    List<GetCategoryItemRes>        mdPickItemList;
}