package com.example.demo.src.user.service;



import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.store.StoreProvider;
import com.example.demo.src.user.dao.UserDao;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import com.example.demo.utils.ValidationRegex;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
@Transactional
@AllArgsConstructor
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;
    private final StoreProvider storeProvider;

    @Transactional
    public PostUserRes      createUser(PostUserReq postUserReq) throws BaseException{
        if(userProvider.checkEmail(postUserReq.getEmail()) == 1){
            throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_EMAIL);
        }

        if(userProvider.checkName(postUserReq.getName()) == 1){
            throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_NAME);
        }
        String pw;
        try{
            pw = new SHA256().encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pw);
        }catch (Exception ignored){
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }

        try{

            long        userId = userDao.createUser(postUserReq);
            String      jwt    = jwtService.createJwt(userId);

            return  new PostUserRes(userId, jwt);
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    @Transactional
    public void     updatePassword(PatchPasswordReq patchPasswordReq)   throws BaseException{
        if(userProvider.checkUserId(patchPasswordReq.getUserId()) == 0){
            throw new BaseException(BaseResponseStatus.USER_NOT_EXISTS);
        }

        String  pwd;

        try{
            pwd = new SHA256().encrypt(patchPasswordReq.getPassword());
            patchPasswordReq.setPassword(pwd);
        }
        catch (Exception ignored){
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }

        try{
            int result = userDao.updatePassword(patchPasswordReq);

            if(result == 0){
                throw   new BaseException(BaseResponseStatus.UPDATE_FAIL_PASSWORD);
            }
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public PatchKartOptionRes updateKartOptionNums(PatchKartOptionReq patchKartOptionReq) throws BaseException{
        if(userProvider.checkUserId(patchKartOptionReq.getUserId()) == 0) {
            throw new BaseException(USER_NOT_EXISTS);
        }

        if(userProvider.checkKartId(patchKartOptionReq.getKartId()) == 0){
            throw new BaseException(KART_ID_NOT_EXISTS);
        }

        //try{
            return  userDao.updateKartOptionNum(patchKartOptionReq);
        //}
       // catch (Exception exception){
       //     throw new BaseException(DATABASE_ERROR);
       // }
    }

    public PatchKartOptionRes updateKartOption(PatchKartOptionIdReq patchKartOptionIdReq) throws BaseException{
        if(userProvider.checkUserId(patchKartOptionIdReq.getUserId()) == 0) {
            throw new BaseException(USER_NOT_EXISTS);
        }

        if(userProvider.checkKartId(patchKartOptionIdReq.getKartId()) == 0){
            throw new BaseException(KART_ID_NOT_EXISTS);
        }

        if(storeProvider.checkOptionId(patchKartOptionIdReq.getOptionId()) == 0){
            throw new BaseException(OPTION_ID_NOT_EXISTS);
        }

        try {
            return  userDao.updateKartOption(patchKartOptionIdReq);
        }catch (Exception exception){
            throw   new BaseException(DATABASE_ERROR);
        }
    }

    public PostScrapBookRes         createScrapBook(PostScrapBookReq    postScrapBookReq) throws BaseException{
        if(userProvider.checkUserId(postScrapBookReq.getUserId()) == 0){
            throw new BaseException(USER_NOT_EXISTS);
        }

        try{
            return userDao.createScrapBook(postScrapBookReq);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void             updateCouponStatus(PatchCouponStatusReq patchCouponStatusReq)   throws BaseException{
        if(userProvider.checkUserId(patchCouponStatusReq.getUserId()) == 0){
            throw new BaseException(USER_NOT_EXISTS);
        }

        if(userProvider.checkPatchCouponStatusReq(patchCouponStatusReq) == 0){
            throw   new BaseException(INVALID_PATCH_COUPON_STATUS_REQUEST);
        }

        try{
            userDao.updateCouponStatus(patchCouponStatusReq.getCouponId());

            return;
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostOrderRes    createOrder(PostOrderReq postOrderReq)  throws BaseException{
        if(userProvider.checkUserId(postOrderReq.getUserId()) == 0){
            throw new BaseException(USER_NOT_EXISTS);
        }

        int length = postOrderReq.getKartId().size();

        for(int i=0;i<length;++i){
            if(userProvider.checkKartId(postOrderReq.getKartId().get(i)) == 0){
                throw new BaseException(KART_ID_NOT_EXISTS);
            }
        }

        if(postOrderReq.getCouponId()!= 0 && userProvider.checkCouponId(postOrderReq.getCouponId()) == 0){
            throw new BaseException(INVALID_COUPON_ID);
        }

       // try{
            return  userDao.createOrder(postOrderReq);
      //  }catch (Exception exception){
      //      throw new BaseException(DATABASE_ERROR);
      //  }

    }
}
