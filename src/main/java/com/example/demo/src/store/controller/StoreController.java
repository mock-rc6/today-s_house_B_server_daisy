package com.example.demo.src.store.controller;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.store.StoreProvider;
import com.example.demo.src.store.model.*;
import com.example.demo.src.store.service.StoreService;
import com.example.demo.src.user.model.GetUserKartRes;
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
    public BaseResponse<GetStoreItemRes>   retrieveStoreItem(@RequestParam("id") String id,
                                                             @RequestParam("user") String user)    throws BaseException{
        if(!ValidationRegex.canConvertLong(id) || !ValidationRegex.canConvertLong(user)){
            return  new BaseResponse<>(BaseResponseStatus.INVALID_ID);
        }
        try{
            long    itemId = Long.parseLong(id);
            long    userId = Long.parseLong(user);
            long    jwtUserId = jwtService.getUserId();

            if(jwtUserId != userId){
                return  new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            GetStoreItemRes getStoreItemRes = storeProvider.retrieveStoreItem(itemId, userId);

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

    @ResponseBody
    @PostMapping("/{userId}/items")
    public BaseResponse<PostKartItemRes>       createKartItem(@PathVariable("userId") String id,
            @RequestParam("id") String item,
            @RequestBody PostKartItemReq postKartItemReq) throws BaseException{
        if(postKartItemReq.getNumber() == 0){
            return  new BaseResponse<>(BaseResponseStatus.EMPTY_OPTION_NUMBER);
        }

        if(postKartItemReq.getOptionId() == 0){
            return new BaseResponse<>(BaseResponseStatus.EMPTY_OPTION_ID);
        }

        if(!ValidationRegex.canConvertLong(id) || !ValidationRegex.canConvertLong(item)){
            // String 형을 int형이나 long형으로 변환할 수 있는지 여부
            return  new BaseResponse<>(BaseResponseStatus.INVALID_ID);
        }

        try{
            long    userId = Long.parseLong(id);
            long    jwtUserId = jwtService.getUserId();

            if(userId != jwtUserId){
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            long    itemId = Long.parseLong(item);
            long    kartId = storeService.createKartItem(postKartItemReq, userId, itemId);
            String  message="성공적으로 장바구니에 추가되었습니다.";

            PostKartItemRes postKartItemRes = new PostKartItemRes(kartId, message);

            return new BaseResponse<PostKartItemRes>(postKartItemRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    @ResponseBody
    @PostMapping("/items")
    public BaseResponse<PostScrapRes>   createItemScrap(@RequestParam("id") String id,
                                                        @RequestParam("user") String user)  throws BaseException{
        if(!ValidationRegex.canConvertLong(id) || !ValidationRegex.canConvertLong(user)){
            return  new BaseResponse<>(BaseResponseStatus.INVALID_ID);
        }

        try{
            long        userId = Long.parseLong(user);
            long        jwtUserId = jwtService.getUserId();

            if(userId != jwtUserId){
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            long        itemId = Long.parseLong(id);

            PostScrapReq postScrapReq = new PostScrapReq(itemId, userId);
            PostScrapRes postScrapRes = storeService.createItemScrap(postScrapReq);

            return new BaseResponse<PostScrapRes>(postScrapRes);
        }
        catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/inquirys")
    public BaseResponse<List<GetInquiryRes>>        retrieveInquiry(@RequestParam("id")String id)   throws BaseException{
        if(!ValidationRegex.canConvertLong(id)){
            return  new BaseResponse<>(BaseResponseStatus.INVALID_ID);
        }

        try{
            long        optionId = Long.parseLong(id);

            List<GetInquiryRes> getInquiryResList = storeProvider.retrieveOptionInquiry(optionId);

            return  new BaseResponse<List<GetInquiryRes>>(getInquiryResList);
        }
        catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/inquirys")
    public BaseResponse<PostInquiryRes>         createInquiry(@RequestParam("id")String id,
                                                              @RequestBody PostInquiryReq postInquiryReq)   throws BaseException{
        if(!ValidationRegex.canConvertLong(id)){
            return  new BaseResponse<>(BaseResponseStatus.INVALID_ID);
        }

        if(postInquiryReq.getCategory()==null){
            return  new BaseResponse<>(BaseResponseStatus.EMPTY_INQUIRY_CATEGORY);
        }

        if(postInquiryReq.getDescription() == null){
            return  new BaseResponse<>(BaseResponseStatus.EMPTY_INQUIRY_DESCRIPTION);
        }

        if(postInquiryReq.getIsPublic() != 0 && postInquiryReq.getIsPublic() != 1){
            return  new BaseResponse<>(BaseResponseStatus.INVALID_INQUIRY_ISPUBLIC);
        }

        try{
            long        optionId = Long.parseLong(id);
            long        userId = jwtService.getUserId();

            postInquiryReq.setUserId(userId);
            postInquiryReq.setOptionId(optionId);

            PostInquiryRes postInquiryRes = storeService.createInquiry(postInquiryReq);

            return  new BaseResponse<PostInquiryRes>(postInquiryRes);
        }
        catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }
}