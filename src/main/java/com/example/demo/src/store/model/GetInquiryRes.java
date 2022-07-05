package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetInquiryRes {
    String                  title;
    String                  description;
    String                  createdAt;
    String                  userName;
    String                  category;
    GetInquiryAnswerRes     answer;
}