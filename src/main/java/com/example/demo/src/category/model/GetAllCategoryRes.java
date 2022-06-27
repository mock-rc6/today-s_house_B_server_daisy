package com.example.demo.src.category.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetAllCategoryRes {
    long            subCategoryId;
    List<String>    categoryList;
    List<String>    subCategoryList;
    List<String>    miniCategoryList;
}