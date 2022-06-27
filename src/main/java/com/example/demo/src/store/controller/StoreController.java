package com.example.demo.src.store.controller;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.img.service.ImgService;
import com.example.demo.src.store.StoreProvider;
import com.example.demo.src.store.model.GetStoreRes;
import com.example.demo.src.store.service.StoreService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/store")
@AllArgsConstructor
public class StoreController {
    private final StoreProvider storeProvider;
    private final StoreService  storeService;
    private final ImgService    imgService;
/*
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetStoreRes>   retrieveStore() throws BaseException{
        try{

        }catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }
    */
}