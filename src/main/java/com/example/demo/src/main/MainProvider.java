package com.example.demo.src.main;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.main.DAO.MainDao;
import com.example.demo.src.main.model.*;
import com.example.demo.src.review.model.GetMyReviewsRes;
import com.example.demo.src.store.StoreProvider;
import com.example.demo.src.store.model.GetInquiryRes;
import com.example.demo.src.user.UserProvider;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class MainProvider {
    @Autowired
    private final MainDao       mainDao;
    @Autowired
    private final UserProvider  userProvider;
    @Autowired
    private final StoreProvider storeProvider;

    public List<GetEventsRes>   retrieveEvents()    throws BaseException{
        try{
            List<GetEventsRes>  retrieveResult = mainDao.retrieveEvents();
            return retrieveResult;
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int      checkEventId(long   eventId)    throws BaseException{
        try{
            return mainDao.checkEventId(eventId);
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetEventDetailRes    retrieveEventDetails(long   eventId)    throws BaseException{
        if(checkEventId(eventId) == 0){
            throw   new BaseException(BaseResponseStatus.EVENT_NOT_EXISTS);
        }

        try{
            GetEventDetailRes   getEventDetailRes = mainDao.retrieveEventDetails(eventId);
            return  getEventDetailRes;
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetMainRes       retrieveMain()  throws BaseException{
        try{
            GetMainRes  getMainRes = mainDao.retrieveMain();
            return      getMainRes;
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetMyProfileRes  retrieveMyProfile(long  userId) throws BaseException{
        if(userProvider.checkUserId(userId) == 0){
            throw new BaseException(BaseResponseStatus.USER_NOT_EXISTS);
        }
        try{
            return  mainDao.retrieveMyProfile(userId);
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetMyShoppingRes retrieveMyShopping(long userId) throws BaseException{
        if(userProvider.checkUserId(userId) == 0){
            throw new BaseException(BaseResponseStatus.USER_NOT_EXISTS);
        }

        try{
            return  mainDao.retrieveMyShopping(userId);
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<GetMyReviewsRes>    retrieveMyReviewRes(long  userId, boolean isPhotoReview, boolean isBestReviews) throws BaseException{
        if(userProvider.checkUserId(userId) == 0){
            throw   new BaseException(BaseResponseStatus.USER_NOT_EXISTS);
        }

        try{
             return mainDao.retrieveMyReviews(userId, isPhotoReview, isBestReviews);
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetReviewWriteRes        retrieveReviewWrite(long    optionId, long    userId) throws BaseException{
        if(storeProvider.checkOptionId(optionId) == 0){
            throw new BaseException(BaseResponseStatus.ITEM_ID_NOT_EXISTS);
        }

        if(userProvider.checkUserId(userId) == 0){
            throw   new BaseException(BaseResponseStatus.USER_NOT_EXISTS);
        }

        try{
            return mainDao.retrieveReviewWrite(optionId, userId);
        }catch (Exception exception){
            throw   new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int              checkReviewOption(long  userId, long optionId)  throws BaseException{
        try{
            return  mainDao.checkReviewOption(userId, optionId);
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    private int      checkGuestOrderId(long  guestOrderId) throws BaseException{
        try{
            return mainDao.checkGuestOrderId(guestOrderId);
        }
        catch (Exception exception){
            throw   new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    private int     checkGuestOrderEmail(String email)  throws BaseException{
        try{
            return  mainDao.checkGuestOrderEmail(email);
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    public GetGuestOrderRes     retrieveGuestOrder(GetGuestOrderReq getGuestOrderReq)   throws BaseException{
        if(checkGuestOrderEmail(getGuestOrderReq.getEmail()) == 0){
            throw   new BaseException(BaseResponseStatus.GUEST_ORDER_EMAIL_NOT_EXISTS);
        }

        if(checkGuestOrderId(getGuestOrderReq.getOrderNum()) == 0){
            throw   new BaseException(BaseResponseStatus.GUEST_ORDER_NUMBER_NOT_EXISTS);
        }

        try{
            return  mainDao.retrieveGuestOrder(getGuestOrderReq);
        }catch (Exception exception){
            throw   new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}