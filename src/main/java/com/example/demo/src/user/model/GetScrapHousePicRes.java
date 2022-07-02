package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetScrapHousePicRes {
    long        housePicId;
    String      thumbnail;
    String      userName; //작성자 이름
    long        userId;
    String      title;
}