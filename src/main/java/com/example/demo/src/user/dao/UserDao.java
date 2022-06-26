package com.example.demo.src.user.dao;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int  checkEmail(String   email){
        String      checkEmailQuery = "SELECT\n" +
                "    EXISTS(\n" +
                "        SELECT email    FROM Users  WHERE   email = ? AND status = 'ACTIVE'\n" +
                "        );";
        String      checkEmailQueryParams = email;

        return this.jdbcTemplate.queryForObject(
          checkEmailQuery,
          int.class,
          checkEmailQueryParams
        );
    }

    public int  checkName(String    name){
        String      checkNameQuery = "SELECT\n" +
                "    EXISTS(\n" +
                "        SELECT name    FROM Users WHERE name = ?\n" +
                "        );";
        String      checkNameQueryParams = name;

        return  this.jdbcTemplate.queryForObject(
                checkNameQuery,
                int.class,
                checkNameQueryParams
        );
    }

    public int  checkUserId(long    userId){
        String  checkUserIdQuery = "SELECT EXISTS(\n" +
                "    SELECT userId   FROM Users\n" +
                "    WHERE   userId = ? AND status = 'ACTIVE'\n" +
                "           );";
        long  checkUserIdQueryParams = userId;

        return this.jdbcTemplate.queryForObject(
                checkUserIdQuery, int.class, checkUserIdQueryParams
        );
    }

    public long createUser(PostUserReq  postUserReq){
        String      createUserQuery = "INSERT INTO Users(email, profilePicUrl, name, password, gender, bornYear)\n" +
                "VALUES\n" +
                "    (?, ?, ?, ?, ?, ?);";

        Object[]    createUserQueryParams = new Object[]{postUserReq.getEmail(), postUserReq.getProfilePicUrl(),
        postUserReq.getName(), postUserReq.getPassword(), postUserReq.getGender(), Integer.parseInt(postUserReq.getBornYear())};

        // 데이터 삽입 쿼리
        this.jdbcTemplate.update(createUserQuery, createUserQueryParams);

        String      getNewUserIdQuery = "SELECT LAST_INSERT_ID();";
        return  this.jdbcTemplate.queryForObject(getNewUserIdQuery, long.class);
    }

    public int      checkPassword(PostLogInReq postLogInReq){
        String      checkPasswordQuery = "SELECT EXISTS(\n" +
                "    SELECT userId\n" +
                "    FROM Users\n" +
                "    WHERE\n" +
                "        email = ? AND\n" +
                "        password = ? AND status = 'ACTIVE'\n" +
                "           );";
        Object[]    checkPasswordQueryParams = new Object[]{postLogInReq.getEmail(), postLogInReq.getPassword()};

        return  this.jdbcTemplate.queryForObject(checkPasswordQuery, int.class, checkPasswordQueryParams);
    }

    public long     logIn(PostLogInReq postLogInReq){
        String      logInQuery = "SELECT\n" +
                "    userId\n" +
                "FROM Users\n" +
                "WHERE\n" +
                "    email = ? AND\n" +
                "    password = ? AND status = 'ACTIVE';";
        Object[]    logInQueryParams = new Object[]{postLogInReq.getEmail(), postLogInReq.getPassword()};

        return  this.jdbcTemplate.queryForObject(logInQuery, Long.class, logInQueryParams);
    }

    public int      updatePassword(PatchPasswordReq patchPasswordReq){
        String      updatePasswordQuery = "UPDATE Users\n" +
                "SET password = ?\n" +
                "WHERE userId = ? AND status = 'ACTIVE';";
        Object[]    updatePasswordQueryParams = new Object[]{patchPasswordReq.getPassword(), patchPasswordReq.getUserId()};

        return this.jdbcTemplate.update(updatePasswordQuery, updatePasswordQueryParams);
    }
}
