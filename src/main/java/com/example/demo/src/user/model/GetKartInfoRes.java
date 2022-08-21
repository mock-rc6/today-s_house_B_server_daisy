package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetKartInfoRes {
    long        kartId;
    String      imgUrl;
    String      optionName;
    long        optionId;
    int         itemNum;
    String      price;
    String      delivery;
}