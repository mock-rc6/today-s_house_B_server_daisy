package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserReq {
    String      email;
    String      profilePicUrl;
    String      name;
    String      password;
    String      gender;
    String      bornYear;
}