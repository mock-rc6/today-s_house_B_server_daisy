package com.example.demo.src.oauth.service;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.oauth.config.KakaoOAuth;
import com.example.demo.src.oauth.model.KakaoUser;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.src.user.model.PostLogInRes;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.service.UserService;
import com.example.demo.utils.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class OAuthService {
    private final KakaoOAuth kakaoOAuth;
    private final UserProvider userProvider;
    private final UserService userService;
    private final JwtService jwtService;

    public PostLogInRes kakaoLogIn(String   code)   throws BaseException {
        KakaoUser kakaoUser = kakaoOAuth.getKakaoUser(code);
        long        userId = kakaoUser.getUserId();
        String      email = kakaoUser.getEmail();
        String      userName = kakaoUser.getName();
        List<GetUserRes> user = userProvider.getUserByKakaoId(userId);
        if(user.size() == 0){
            List<GetUserRes> emailAccount = userProvider.getUserByEmail(email);
            if(emailAccount.size()>0){
                userService.createKakaoId(userId, email);
            }
            else{
                PostUserReq postUserReq = new PostUserReq(
                        email, "https://d2u3dcdbebyaiu.cloudfront.net/uploads/atch_img/309/59932b0eb046f9fa3e063b8875032edd_crop.jpeg", userName, "today-s-house-B"
                );
                userService.createUser(postUserReq);
                userService.createKakaoId(userId, email);
            }
        }
        try{
            long    kakaoId = userProvider.getUserByKakaoId(userId).get(0).getUserId();
            String  jwt = jwtService.createJwt(userId);
            return  new PostLogInRes(kakaoId, jwt);
        }
        catch (Exception ignored){
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }
    }

    public void     logOut(String code) throws BaseException{
          //try{
             kakaoOAuth.kakaoLogOut(code);
         //}
         //catch (Exception exception){
         //     throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
         //  }
    }
}