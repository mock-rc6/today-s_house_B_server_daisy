package com.example.demo.src.user.controller;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.model.*;
import com.example.demo.src.user.service.UserService;
import com.example.demo.utils.ValidationRegex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;

    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @RequestMapping
    @PostMapping("")
    public BaseResponse<PostUserRes>     createUser(@RequestBody PostUserReq postUserReq){
        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_EMAIL);
        }
        if(!ValidationRegex.isRegexEmail(postUserReq.getEmail())){
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }
        if(postUserReq.getPassword() == null){
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_PASSWORD);
        }
        if(postUserReq.getPassword().length()<8){
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_SHORT_PASSWORD);
        }

        if(postUserReq.getName() == null){
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_NAME);
        }
        if(postUserReq.getProfilePicUrl()== null){
            postUserReq.setProfilePicUrl("");
        }

        // 실제 유저를 생성
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<PostUserRes>(postUserRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/log-in")
    public BaseResponse<PostLogInRes>   logIn(@RequestBody PostLogInReq postLogInReq){
        if(postLogInReq.getEmail() == null){
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_EMAIL);
        }
        if(!ValidationRegex.isRegexEmail(postLogInReq.getEmail())){
            return  new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }
        if(postLogInReq.getPassword()== null){
            return  new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_PASSWORD);
        }

        try{
            PostLogInRes postLogInRes = userProvider.logIn(postLogInReq);
            return new BaseResponse<PostLogInRes>(postLogInRes);
        }
        catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{userId}")
    public BaseResponse<String> updatePassword(@PathVariable("userId")String    id, @RequestBody PatchPasswordReq patchPasswordReq){
        if(!ValidationRegex.canConvertLong(id)){
            return  new BaseResponse<>(BaseResponseStatus.INVALID_ID);
        }
        if(patchPasswordReq.getPassword() == null){
            return new BaseResponse<>(BaseResponseStatus.PATCH_PASSWORD_EMPTY);
        }
        if(patchPasswordReq.getPassword().length()<8){
            return new BaseResponse<>(BaseResponseStatus.PATCH_PASSWORD_SHORT);
        }
        try{
            long    userId = Long.parseLong(id);
            long    jwtUserId = jwtService.getUserId();

            if(userId != jwtUserId){
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            patchPasswordReq.setUserId(userId);

            userService.updatePassword(patchPasswordReq);

            String res = "성공적으로 비밀번호가 변경되었습니다.";
            return  new BaseResponse<String>(res);
        }
        catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }
}
