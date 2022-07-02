package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetScrapsRes {
    long                            userId; // 유저 id
    String                          userName; // 유저이름
    String                          profileThumbnail; // 유저 프로필 썸네일
    List<GetScrapFoldersRes>        folderList; // 폴더 리스트들
    List<String>                    categoryList; // 아이템 카테고리 리스트
    List<GetScrapItemRes>           itemScrapList;
    List<GetScrapHousePicRes>       houseScrapList;
}