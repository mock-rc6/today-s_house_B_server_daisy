package com.example.demo.src.main.service;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.main.DAO.MainDao;
import com.example.demo.src.main.MainProvider;
import com.example.demo.src.main.model.PostReviewReq;
import com.example.demo.src.main.model.PostReviewRes;
import com.example.demo.src.store.StoreProvider;
import com.example.demo.src.user.UserProvider;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class MainService {
    @Autowired
    private final MainDao       mainDao;
    @Autowired
    private final MainProvider  mainProvider;
    @Autowired
    private final StoreProvider storeProvider;
    @Autowired
    private final UserProvider  userProvider;

    public PostReviewRes        createReview(PostReviewReq postReviewReq)   throws BaseException{
        if(userProvider.checkUserId(postReviewReq.getUserId()) == 0){
            throw   new BaseException(BaseResponseStatus.USER_NOT_EXISTS);
        }
        if(storeProvider.checkOptionId(postReviewReq.getOptionId()) == 0){
            throw   new BaseException(BaseResponseStatus.OPTION_ID_NOT_EXISTS);
        }
        try{
            return  mainDao.createReview(postReviewReq);
        }catch (Exception e){
            throw   new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}