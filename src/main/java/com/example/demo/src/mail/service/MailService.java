package com.example.demo.src.mail.service;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.mail.dao.MailDao;
import com.example.demo.src.mail.model.PostVerifyCodeReq;
import com.example.demo.src.user.UserProvider;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MailService {

    @Autowired
    private final JavaMailSender    javaMailSender;
    private final   Logger  logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider      userProvider;

    @Autowired
    private final MailDao           mailDao;

    public long sendCertificationMail(String    email)  throws BaseException {
        if(userProvider.checkEmail(email) == 1){
            throw   new BaseException(BaseResponseStatus.DUPLICATED_EMAIL);
        }
        try{
            String  code = UUID.randomUUID().toString().substring(0, 6);
            sendMail(code, email);

            return  mailDao.createVerificationCode(code, email);
        }catch (Exception exception){
            exception.printStackTrace();
            throw   new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int  checkCode(PostVerifyCodeReq postVerifyCodeReq)  throws BaseException{
        try{
            return mailDao.checkCode(postVerifyCodeReq);
        }catch (Exception exception){
            throw   new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    private MimeMessage createMessage(String    code, String email) throws Exception{
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, email);
        message.setSubject("오늘의 집 모의외주 프로젝트 인증 번호입니다.");
        message.setText("이메일 인증코드: "+code);

        message.setFrom(new InternetAddress(Secret.RECIPIENT));

        return  message;
    }

    public void sendMail(String code, String email) throws Exception{
        try{
            MimeMessage mimeMessage = createMessage(code, email);
            javaMailSender.send(mimeMessage);
        }catch (MailException mailException){
            mailException.printStackTrace();
            throw   new IllegalAccessException();
        }
    }

}