package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostInquiryReq {
    long        userId;
    long        optionId;
    int         isPublic;
    String      category;
    String      description;
}