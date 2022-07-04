package com.example.demo.src.oauth.controller;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.oauth.service.OAuthService;
import com.example.demo.src.user.model.PostLogInRes;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth/kakao")
@AllArgsConstructor
public class OAuthController {
    @Autowired
    private final OAuthService oAuthService;

    @ResponseBody
    @GetMapping("")
    public BaseResponse<PostLogInRes> kakaoLogIn(@RequestParam("code")String  code){
        try{
            return new BaseResponse<PostLogInRes>(oAuthService.kakaoLogIn(code));
        }
        catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/kakao/logout")
    public BaseResponse<String>         kakaoLogOut(@RequestParam("code") String code){
        try{
            String  res = "로그아웃에 성공하였습니다.";
            oAuthService.logOut(code);
            return new BaseResponse<String>(res);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }
}