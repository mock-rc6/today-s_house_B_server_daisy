package com.example.demo.src.store.controller;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.img.service.ImgService;
import com.example.demo.src.store.StoreProvider;
import com.example.demo.src.store.model.GetItemOptionRes;
import com.example.demo.src.store.model.GetStoreCategoryRes;
import com.example.demo.src.store.model.GetStoreItemRes;
import com.example.demo.src.store.model.GetStoreRes;
import com.example.demo.src.store.service.StoreService;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.ValidationRegex;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/store")
@AllArgsConstructor
public class StoreController {
    @Autowired
    private final StoreProvider storeProvider;
    @Autowired
    private final StoreService  storeService;
    @Autowired
    private final ImgService    imgService;
    @Autowired
    private final JwtService    jwtService;

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

    @ResponseBody
    @GetMapping("/items")
    public BaseResponse<GetStoreItemRes>   retrieveStoreItem(@RequestParam("id") String id)    throws BaseException{
        if(!ValidationRegex.canConvertLong(id)){
            return  new BaseResponse<>(BaseResponseStatus.INVALID_ID);
        }
        try{
            long    itemId = Long.parseLong(id);
            GetStoreItemRes getStoreItemRes = storeProvider.retrieveStoreItem(itemId);

            return  new BaseResponse<GetStoreItemRes>(getStoreItemRes);
        }
        catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userId}/items")
    public BaseResponse<List<GetItemOptionRes>>     retrieveItemOptions(@PathVariable("userId") String user
            ,@RequestParam(name="id", required = true) String id) throws BaseException{
        if(!ValidationRegex.canConvertLong(id) || !ValidationRegex.canConvertLong(user)){
            return new BaseResponse<>(BaseResponseStatus.INVALID_ID);
        }
        try{
            long    userId = Long.parseLong(user);
            long    itemId = Long.parseLong(id);
            long    jwtUserId = jwtService.getUserId();

            if(jwtUserId != userId){
                return  new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            List<GetItemOptionRes> getItemOptionResList = storeProvider.retrieveItemOptions(itemId);

            return  new BaseResponse<List<GetItemOptionRes>>(getItemOptionResList);
        }
        catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }
}