package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchKartOptionIdReq {
    long        kartId;
    long        userId;
    long        optionId;
}