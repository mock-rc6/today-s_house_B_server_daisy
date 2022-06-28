package com.example.demo.src.user.service;



import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.user.dao.UserDao;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.model.PatchPasswordReq;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.PostUserRes;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import com.example.demo.utils.ValidationRegex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

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
}
