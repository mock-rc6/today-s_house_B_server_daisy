package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.user.dao.UserDao;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
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
}
