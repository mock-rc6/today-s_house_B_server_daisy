package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserFollowerRes {
    long        userId;
    String      name;
    String      profilePicUrl;
}