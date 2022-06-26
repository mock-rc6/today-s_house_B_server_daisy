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
                "        SELECT email    FROM Users  WHERE   email = ?\n" +
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
                "    WHERE   userId = ?\n" +
                "           );";
        long  checkUserIdQueryParams = userId;

        return this.jdbcTemplate.queryForObject(
                checkUserIdQuery, int.class, checkUserIdQueryParams
        );
    }
}
