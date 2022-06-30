package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.user.dao.UserDao;
import com.example.demo.src.user.model.GetUserKartRes;
import com.example.demo.src.user.model.PostLogInReq;
import com.example.demo.src.user.model.PostLogInRes;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional(readOnly = true)
public class UserProvider {

    private final UserDao userDao;
    private final JwtService    jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    public int      checkEmail(String   email)  throws BaseException{
        try{
            return userDao.checkEmail(email);
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int      checkName(String    name)   throws BaseException{
        try{
            return  userDao.checkName(name);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int      checkUserId(long    userId) throws BaseException{
        try{
            return  userDao.checkUserId(userId);
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostLogInRes logIn(PostLogInReq postLogInReq)    throws BaseException{
        if(checkEmail(postLogInReq.getEmail()) == 0){
            throw new BaseException(FAILED_TO_LOGIN);
        }

        String pwd;
        try{
            pwd = new SHA256().encrypt(postLogInReq.getPassword());
            postLogInReq.setPassword(pwd);
        }catch (Exception ignored){
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        try{
            int result = userDao.checkPassword(postLogInReq);

            if(result == 0){
                throw new BaseException(FAILED_TO_LOGIN);
            }

            long    userId = userDao.logIn(postLogInReq);
            String  jwt = jwtService.createJwt(userId);
            PostLogInRes postLogInRes = new PostLogInRes(userId, jwt);

            return  postLogInRes;
        }catch (Exception exception){
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    public GetUserKartRes retrieveUserKartInfos(long  userId) throws BaseException{
        if(checkUserId(userId) == 0){
            throw new BaseException(BaseResponseStatus.USER_NOT_EXISTS);
        }

        try{
            return  userDao.retrieveUserKartInfos(userId);
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
