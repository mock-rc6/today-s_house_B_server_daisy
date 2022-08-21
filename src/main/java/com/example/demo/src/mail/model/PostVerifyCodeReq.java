package com.example.demo.src.mail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostVerifyCodeReq {
    String      email;
    String      code;
    long        id; // verification Id
}