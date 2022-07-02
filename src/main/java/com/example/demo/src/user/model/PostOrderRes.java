package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostOrderRes {
    long        receiptId;
    String      orderedItem;
    String      orderThumbnail;
    String      price;
    String      count; // 주문한 상품 개수
    String      delivery;
}