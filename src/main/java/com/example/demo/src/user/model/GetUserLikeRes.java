package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserLikeRes {
    int                                     count;
    List<GetLikePosts>                      postList;
    List<GetHouseCategoryRes>               categoryList;
}