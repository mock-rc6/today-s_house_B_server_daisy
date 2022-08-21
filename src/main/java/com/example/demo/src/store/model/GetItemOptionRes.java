package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetItemOptionRes {
    String      saleRate;
    int         saledPrice;
    long        optionId;
    String      optionName;
    String      specialPrice;
    String      delivery;
    String      thumbnail;
}