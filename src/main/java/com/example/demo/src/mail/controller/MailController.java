package com.example.demo.src.mail.controller;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.mail.model.PostVerifyCodeReq;
import com.example.demo.src.mail.service.MailService;
import com.example.demo.utils.ValidationRegex;
import jdk.nashorn.internal.parser.JSONParser;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/mails")
@AllArgsConstructor
public class MailController {
    @Autowired
    private final MailService   mailService;


    @ResponseBody
    @PostMapping("")
    public BaseResponse<Long>        sendEmail(@RequestBody String data)  throws BaseException{
        JSONObject  parser = new JSONObject(data);
        String      email = parser.getString("email");

        if(email == null){
            return  new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_EMAIL);
        }

        if(!ValidationRegex.isRegexEmail(email)){
            return  new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }

        try{

            mailService.sendCertificationMail(email);

            long    verifyCodeId = mailService.sendCertificationMail(email);

            return  new BaseResponse<Long>(verifyCodeId);
        }
        catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/verifications")
    public BaseResponse<String>       verifyEmailCode(@RequestBody PostVerifyCodeReq postVerifyCodeReq)   throws BaseException{
        if(postVerifyCodeReq.getCode() == null){
            return  new BaseResponse<>(BaseResponseStatus.EMPTY_VERIFICATION_CODE);
        }

        if(postVerifyCodeReq.getEmail() == null){
            return  new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_EMAIL);
        }

        try{
            int result = mailService.checkCode(postVerifyCodeReq);

            if(result == 0){
                return  new BaseResponse<>(BaseResponseStatus.INVALID_VERIFICATION_CODE);
            }

            String  message = "이메일 인증에 성공하였습니다.";

            return  new BaseResponse<String>(message);
        }
        catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }
}