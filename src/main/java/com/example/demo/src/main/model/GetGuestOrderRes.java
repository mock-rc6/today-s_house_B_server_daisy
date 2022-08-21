package com.example.demo.src.main.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetGuestOrderRes {
    String      orderItemName;
    String      price;
    String      count;
    String      status;
    String      orderDate;
    String      updateDate;
    String      orderName;
    String      phoneNumber;
    String      placeName;
    String      receivedPhone;
    String      addressCode;
    String      address;
}