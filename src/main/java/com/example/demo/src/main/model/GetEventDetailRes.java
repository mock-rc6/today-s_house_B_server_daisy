package com.example.demo.src.main.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class GetEventDetailRes {
    long            eventId;
    String          eventName;
    List<String>    imgUrlList;
}