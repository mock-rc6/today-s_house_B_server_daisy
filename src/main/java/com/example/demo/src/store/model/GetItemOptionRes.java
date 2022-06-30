package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetItemOptionRes {
    String      saleRate;
    String      saledPrice;
    long        optionId;
    String      optionName;
    String      specialPrice;
    String      delivery;
    String      thumbnail;
}