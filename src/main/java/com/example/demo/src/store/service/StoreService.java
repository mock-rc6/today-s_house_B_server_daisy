package com.example.demo.src.store.service;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.store.StoreProvider;
import com.example.demo.src.store.dao.StoreDao;
import com.example.demo.src.store.model.*;
import com.example.demo.src.user.UserProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class StoreService {

    private final StoreDao      storeDao;
    private final StoreProvider storeProvider;
    private final UserProvider  userProvider;

    public long     createKartItem(PostKartItemReq postKartItemReq, long userId, long itemId) throws BaseException{
        int num = Integer.parseInt(postKartItemReq.getNumber());
        long optionId = Long.parseLong(postKartItemReq.getOptionId());

        if(num<=0){
            throw new BaseException(BaseResponseStatus.INVALID_ITEM_NUMBER);
        }

        if(userProvider.checkUserId(userId) == 0){
            throw   new BaseException(BaseResponseStatus.USER_NOT_EXISTS);
        }

        if(storeProvider.checkOptionId(optionId) == 0){
            throw   new BaseException(BaseResponseStatus.OPTION_ID_NOT_EXISTS);
        }

        if(storeProvider.checkItemOption(itemId, optionId) == 0){
            throw   new BaseException(BaseResponseStatus.INVALID_ITEM_OPTION);
        }

        if(storeProvider.checkKartItem(userId, optionId) == 1){
            throw   new BaseException(BaseResponseStatus.KART_ITEM_ALREADY_EXISTS);
        }

        try{
            return storeDao.createKartItem(postKartItemReq, userId);
        }catch (Exception exception){
            throw   new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public PostScrapRes     createItemScrap(PostScrapReq postScrapReq)  throws BaseException{
        if(userProvider.checkUserId(postScrapReq.getUserId()) == 0){
            throw   new BaseException(BaseResponseStatus.USER_NOT_EXISTS);
        }

        if(storeProvider.checkItemId(postScrapReq.getItemId()) == 0){
            throw   new BaseException(BaseResponseStatus.ITEM_ID_NOT_EXISTS);
        }

        try{
            return  storeDao.createItemScrap(postScrapReq);
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public PostInquiryRes   createInquiry(PostInquiryReq postInquiryReq)    throws BaseException{
        if(storeProvider.checkOptionId(postInquiryReq.getOptionId()) == 0){
            throw   new BaseException(BaseResponseStatus.OPTION_ID_NOT_EXISTS);
        }

        if(userProvider.checkUserId(postInquiryReq.getUserId()) == 0){
            throw   new BaseException(BaseResponseStatus.USER_NOT_EXISTS);
        }

        try{
            return  storeDao.createInquriy(postInquiryReq);
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}