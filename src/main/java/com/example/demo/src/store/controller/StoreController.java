package com.example.demo.src.store.controller;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.img.service.ImgService;
import com.example.demo.src.store.StoreProvider;
import com.example.demo.src.store.model.GetStoreCategoryRes;
import com.example.demo.src.store.model.GetStoreRes;
import com.example.demo.src.store.service.StoreService;
import com.example.demo.utils.ValidationRegex;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/store")
@AllArgsConstructor
public class StoreController {
    private final StoreProvider storeProvider;
    private final StoreService  storeService;
    private final ImgService    imgService;

    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetStoreRes>   retrieveStore() throws BaseException{
        try{
            GetStoreRes getStoreRes = storeProvider.retrieveStoreMain();
            return new BaseResponse<GetStoreRes>(getStoreRes);
        }catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{categoryId}")
    public BaseResponse<GetStoreCategoryRes>    retrieveStoreCategory(@PathVariable("categoryId")String id) throws BaseException{
        if(!ValidationRegex.canConvertLong(id)){
            return  new BaseResponse<>(BaseResponseStatus.INVALID_ID);
        }
        try{
            long    categoryId = Long.parseLong(id);
            GetStoreCategoryRes getStoreCategoryRes = storeProvider.retrieveStoreCategory(categoryId);
            return  new BaseResponse<GetStoreCategoryRes>(getStoreCategoryRes);
        }
        catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }
}