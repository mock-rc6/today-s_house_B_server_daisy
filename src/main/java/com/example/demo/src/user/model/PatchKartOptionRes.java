package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PatchKartOptionRes {
    List<GetKartInfoRes>        kartItemList;
    String                       itemNum;
    String                      saledPrice;
    String                      price;
    String                      discountPrice;
    String                      delivery;
}