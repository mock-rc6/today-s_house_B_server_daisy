package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetInquiryRes {
    String                  description;
    String                  createdAt;
    String                  userName;
    String                  category;
    String                  status;
    GetInquiryAnswerRes     answer;
}