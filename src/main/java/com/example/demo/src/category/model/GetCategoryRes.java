package com.example.demo.src.category.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetCategoryRes {
    long            categoryId;
    List<String>    categoryList;
    List<Long>      categoryIdList;
    List<String>    subCategoryList;
    List<Long>      subCategoryIdList;
}