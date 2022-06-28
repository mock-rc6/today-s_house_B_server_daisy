package com.example.demo.src.main.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class GetMainRes {
    List<String>        eventList;
    List<Long>          eventIdList;
    List<Long>          categoryList;
    List<String>        houseImgList;
    List<String>        houseDescriptionList;
}