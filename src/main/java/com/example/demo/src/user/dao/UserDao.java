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
                "        SELECT name    FROM Users WHERE name = ? AND status = 'ACTIVE'\n" +
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
        String      createUserQuery = "INSERT INTO Users(email, profilePicUrl, name, password)\n" +
                "VALUES\n" +
                "    (?, ?, ?, ?);";

        Object[]    createUserQueryParams = new Object[]{postUserReq.getEmail(), postUserReq.getProfilePicUrl(),
        postUserReq.getName(), postUserReq.getPassword()};

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

    private List<GetKartInfoRes>      retrieveUserKart(long   userId){
        String      retrieveUserKartQuery = "SELECT\n" +
                "    (SELECT pictureUrl FROM ItemOptionPictures IOP\n" +
                "     WHERE IOP.optionId = IO.optionId\n" +
                "     GROUP BY IOP.optionId)                             AS 'thumbnail',\n" +
                "    IO.optionName                                       AS 'optionName',\n" +
                "    IO.optionId                                         AS 'optionId',\n" +
                "    number                                              AS 'itemNum',\n" +
                "    concat(FORMAT(number*saledPrice, 0),'원')        AS 'price',\n" +
                "    CASE WHEN deliveryPrice = 0 THEN '무료배송'\n" +
                "         ELSE concat(FORMAT(deliveryPrice,0),'원') end   AS 'delivery'\n" +
                "FROM ((KartItems K inner join ItemOptions IO on K.optionId = IO.optionId)\n" +
                "     inner join Items I on I.itemId = IO.itemId)\n" +
                "WHERE K.userId = ? AND K.status = 'N';";
        long        retrieveUserKartQueryParams = userId;

        return this.jdbcTemplate.query(
                retrieveUserKartQuery,
                (rs, rowNum) -> new GetKartInfoRes(
                        rs.getString("thumbnail"),
                        rs.getString("optionName"),
                        rs.getLong("optionId"),
                        rs.getInt("itemNum"),
                        rs.getString("price"),
                        rs.getString("delivery")
                )
                ,retrieveUserKartQueryParams
        );
    }

    public GetUserKartRes retrieveUserKartInfos(long  userId){
        String      retrieveUserKartInfosQuery = "SELECT\n" +
                "    concat(FORMAT(SUM(deliveryPrice),0),'원')                   AS 'delivery',\n" +
                "    concat(FORMAT(SUM(number), 0),'개')                       AS 'number',\n" +
                "    concat(FORMAT(SUM(saledPrice*number),0), '원')                     AS 'saledPrice',\n" +
                "    concat(FORMAT(SUM(price*number),0),'원')                           AS 'price',\n" +
                "    concat(FORMAT(SUM(price*number)-SUM(saledPrice*number),0),'원')           AS 'discountPrice'\n" +
                "FROM (KartItems K inner join ItemOptions IO on IO.optionId = K.optionId)\n" +
                "WHERE\n" +
                "    K.userId = ? AND K.status = 'N';";
        long        retrieveUserKartInfosQueryParams = userId;

        return  this.jdbcTemplate.queryForObject(retrieveUserKartInfosQuery,
                (rs, rowNum)-> new GetUserKartRes(
                        retrieveUserKart(userId),
                        rs.getString("number"),
                        rs.getString("saledPrice"),
                        rs.getString("price"),
                        rs.getString("discountPrice"),
                        rs.getString("delivery")
                ),
                retrieveUserKartInfosQueryParams);
    }

    public PatchKartOptionRes   updateKartOptionNum(PatchKartOptionReq patchKartOptionReq){
        String      updateKartOptionNumQuery = "UPDATE KartItems\n" +
                "SET number = ?\n" +
                "WHERE status = 'N' AND kartId = ?;";
        Object[]    updateKartOptionNumQueryParams = new Object[]{
                patchKartOptionReq.getNumber(), patchKartOptionReq.getKartId()
        };
        this.jdbcTemplate.update(updateKartOptionNumQuery, updateKartOptionNumQueryParams);

        String      retrieveUserKartQuery = "SELECT\n" +
                "    concat(FORMAT(SUM(deliveryPrice),0),'원')                   AS 'delivery',\n" +
                "    concat(FORMAT(SUM(number), 0),'개')                       AS 'number',\n" +
                "    concat(FORMAT(SUM(saledPrice*number),0), '원')                     AS 'saledPrice',\n" +
                "    concat(FORMAT(SUM(price*number),0),'원')                           AS 'price',\n" +
                "    concat(FORMAT(SUM(price*number)-SUM(saledPrice*number),0),'원')           AS 'discountPrice'\n" +
                "FROM (KartItems K inner join ItemOptions IO on IO.optionId = K.optionId)\n" +
                "WHERE\n" +
                "    K.userId = ? AND K.status = 'N';";
        long        retrieveUserKartQueryParams = patchKartOptionReq.getUserId();

        return this.jdbcTemplate.queryForObject(
                retrieveUserKartQuery,
                (rs, rowNum)-> new PatchKartOptionRes(
                        retrieveUserKart(patchKartOptionReq.getUserId()),
                        rs.getString("number"),
                        rs.getString("saledPrice"),
                        rs.getString("price"),
                        rs.getString("discountPrice"),
                        rs.getString("delivery")
                ),
                retrieveUserKartQueryParams
        );
    }

    public int      checkKartId(long    kartId){
        String      checkKartIdQuery = "SELECT EXISTS(\n" +
                "    SELECT kartId FROM KartItems WHERE kartId = ? AND status = 'N'\n" +
                "           );";
        long        checkKartIdQueryParams = kartId;

        return this.jdbcTemplate.queryForObject(checkKartIdQuery, int.class, checkKartIdQueryParams);
    }

    public PatchKartOptionRes   updateKartOption(PatchKartOptionIdReq   patchKartOptionIdReq){
        String          updateKartOptionQuery = "UPDATE KartItems\n" +
                "SET optionId = ?\n" +
                "WHERE kartId = ? AND status = 'N';";
        Object[]        updateKartOptionQueryParams = new Object[] {patchKartOptionIdReq.getOptionId(), patchKartOptionIdReq.getKartId()};
        this.jdbcTemplate.update(updateKartOptionQuery, updateKartOptionQueryParams);

        String      retrieveUserKartQuery = "SELECT\n" +
                "    concat(FORMAT(SUM(deliveryPrice),0),'원')                   AS 'delivery',\n" +
                "    concat(FORMAT(SUM(number), 0),'개')                       AS 'number',\n" +
                "    concat(FORMAT(SUM(saledPrice*number),0), '원')                     AS 'saledPrice',\n" +
                "    concat(FORMAT(SUM(price*number),0),'원')                           AS 'price',\n" +
                "    concat(FORMAT(SUM(price*number)-SUM(saledPrice*number),0),'원')           AS 'discountPrice'\n" +
                "FROM (KartItems K inner join ItemOptions IO on IO.optionId = K.optionId)\n" +
                "WHERE\n" +
                "    K.userId = ? AND K.status = 'N';";
        long        retrieveUserKartQueryParams = patchKartOptionIdReq.getUserId();

        return this.jdbcTemplate.queryForObject(
                retrieveUserKartQuery,
                (rs, rowNum)-> new PatchKartOptionRes(
                        retrieveUserKart(patchKartOptionIdReq.getUserId()),
                        rs.getString("number"),
                        rs.getString("saledPrice"),
                        rs.getString("price"),
                        rs.getString("discountPrice"),
                        rs.getString("delivery")
                ),
                retrieveUserKartQueryParams
        );
    }
}
