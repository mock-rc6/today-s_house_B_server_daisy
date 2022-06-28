package com.example.demo.src.store.model;

import com.example.demo.src.category.model.GetCategory;
import com.example.demo.src.category.model.GetCategoryEventsRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreCategoryRes {
    String                          categoryName;
    List<GetCategory>               subCategoryList;
    List<GetCategoryEventsRes>      categoryEventList;
}