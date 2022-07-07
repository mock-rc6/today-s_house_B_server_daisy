package com.example.demo.src.mail.dao;

import com.example.demo.src.mail.model.PostVerifyCodeReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class MailDao {
    private JdbcTemplate      jdbcTemplate;

    @Autowired
    public void     setDataSource(DataSource    dataSource){this.jdbcTemplate = new JdbcTemplate(dataSource);}

    public long     createVerificationCode(String   code, String email){
        String      createVerificationCodeQuery = "INSERT INTO VerifyCodes(code, email) VALUES(?, ?);";
        Object[]    createVerificationCodeQueryParams = new Object[]{code, email};

        this.jdbcTemplate.update(createVerificationCodeQuery, createVerificationCodeQueryParams);

        String      retrieveLastInsertIdQuery = "SELECT LAST_INSERT_ID();";

        return  this.jdbcTemplate.queryForObject(retrieveLastInsertIdQuery, long.class);
    }

    public int      checkCode(PostVerifyCodeReq postVerifyCodeReq){
        String      checkCodeQuery = "SELECT EXISTS(\n" +
                "    SELECT codeId   FROM VerifyCodes\n" +
                "    WHERE email = ? AND TIMESTAMPDIFF(SECOND , createdAt, CURRENT_TIMESTAMP) < 180\n" +
                "          AND code = ? AND codeId = ?\n" +
                "           );";
        Object[]    checkCodeQueryParams = new  Object[]{
                postVerifyCodeReq.getEmail(), postVerifyCodeReq.getCode(), postVerifyCodeReq.getId()
        };

        return  this.jdbcTemplate.queryForObject(checkCodeQuery, int.class, checkCodeQueryParams);
    }

}