package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserScrapRes {
    long                    userId;
    String                  userName;
    String                  profilePicUrl;
    List<GetScrapsRes>      itemScrapList;
    List<GetScrapsRes>      houseScrapList;
}