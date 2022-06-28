package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreRes {
    List<GetStoreMainEvents>    eventImgs;
    List<Long>                  categoryIdList;
    List<GetTodaysDealMainRes>      todaysDealList;
}