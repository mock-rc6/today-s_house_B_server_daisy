package com.example.demo.src.user.dao;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
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
                "        SELECT email    FROM Users  WHERE   email = ? AND status != 'DEACTIVE'\n" +
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
                "    kartId," +
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
                "WHERE K.userId = ? AND K.status != 'Y';";
        long        retrieveUserKartQueryParams = userId;

        String  setKartStatusQuery = "UPDATE KartItems\n" +
                "SET status = 'N'\n" +
                "WHERE status = 'P' AND userId = ?;";
        long    setKartStatusQueryParams = userId;

        this.jdbcTemplate.update(setKartStatusQuery, setKartStatusQueryParams);

        return this.jdbcTemplate.query(
                retrieveUserKartQuery,
                (rs, rowNum) -> new GetKartInfoRes(
                        rs.getLong("kartId"),
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
                "    K.userId = ? AND K.status != 'Y';";
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
                "WHERE status != 'Y' AND kartId = ?;";
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
                "    K.userId = ? AND K.status != 'Y';";
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
                "    SELECT kartId FROM KartItems WHERE kartId = ? AND status != 'Y'\n" +
                "           );";
        long        checkKartIdQueryParams = kartId;

        return this.jdbcTemplate.queryForObject(checkKartIdQuery, int.class, checkKartIdQueryParams);
    }

    public PatchKartOptionRes   updateKartOption(PatchKartOptionIdReq   patchKartOptionIdReq){
        String          updateKartOptionQuery = "UPDATE KartItems\n" +
                "SET optionId = ?\n" +
                "WHERE kartId = ? AND status != 'Y';";
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
                "    K.userId = ? AND K.status != 'Y';";
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

    public PostScrapBookRes     createScrapBook(PostScrapBookReq postScrapBookReq){
        String                  createScrapBookQuery = "INSERT INTO ScrapBooks(userId, name, description)\n" +
                "VALUES(?, ?, ?);";
        Object[]                createScrapBookQueryParams = new Object[]{postScrapBookReq.getUserId(), postScrapBookReq.getName(), postScrapBookReq.getDescription()};
        this.jdbcTemplate.update(createScrapBookQuery, createScrapBookQueryParams);

        String                  retrieveInsertIdQuery = "SELECT last_insert_id();";
        String                  message = "성공적으로 스크랩북이 생성되었습니다.";
        return  new PostScrapBookRes(
                this.jdbcTemplate.queryForObject(retrieveInsertIdQuery, long.class)
                , message);
    }

    public List<GetUserCouponRes>   retrieveUserCoupons (long   userId){
        String      retrieveUserCouponsQuery = "SELECT\n" +
                "    couponId,\n" +
                "    DATE_FORMAT(due, '%Y년 %m월 %d일까지')       AS 'due',\n" +
                "    description,\n" +
                "    CASE WHEN saleAmount = 0 THEN concat(saleRate, '%')\n" +
                "         ELSE concat(FORMAT(saleAmount, 0),'원') END       AS 'benefit',\n" +
                "    CASE WHEN status = 'N'   THEN '받기'\n" +
                "         ELSE '받음' END                        AS 'received'\n" +
                "FROM Coupons\n" +
                "WHERE userId = ? AND status != 'Y' AND TIMESTAMPDIFF(SECOND, CURRENT_TIMESTAMP, due)>0;";
        long        retrieveUserCouponQueryParams = userId;

        return this.jdbcTemplate.query(
                retrieveUserCouponsQuery,
                (rs, rowNum) -> new GetUserCouponRes(
                        rs.getLong("couponId"),
                        rs.getString("due"),
                        rs.getString("description"),
                        rs.getString("benefit"),
                        rs.getString("received")
                )
                ,retrieveUserCouponQueryParams
        );
    }

    public int      checkPatchCouponReq(PatchCouponStatusReq patchCouponStatusReq){
        String      checkPatchCouponReqQuery = "SELECT EXISTS(\n" +
                "    SELECT couponId FROM Coupons\n" +
                "    WHERE TIMESTAMPDIFF(SECOND, CURRENT_TIMESTAMP, due) > 0 AND status != 'Y' AND couponId = ? AND userId = ?\n" +
                "           );";
        Object[]    checkPatchCouponReqQueryParams = new Object[]{patchCouponStatusReq.getCouponId(), patchCouponStatusReq.getUserId()};

        return  this.jdbcTemplate.queryForObject(checkPatchCouponReqQuery, int.class, checkPatchCouponReqQueryParams);
    }
    public void     updateCouponStatus(long     couponId){
        String      updateCouponStatusQuery = "UPDATE Coupons\n" +
                "SET status = 'R'\n" +
                "WHERE couponId = ?;";
        long        updateCouponStatusQueryParams = couponId;
        this.jdbcTemplate.update(updateCouponStatusQuery, updateCouponStatusQueryParams);

        return;
    }

    private List<GetScrapFoldersRes>      retrieveUserFolders(long    userId){
        String      retrieveUserFoldersQuery = "SELECT\n" +
                "    scrapBookId         AS 'folderId',\n" +
                "    name                AS 'folderName',\n" +
                "    (SELECT houseImgUrl FROM (HouseImgs inner join ScrapHousePics SHP on HouseImgs.housePicId = SHP.housePicId)\n" +
                "                        WHERE SHP.scrapBookId = SB.scrapBookId\n" +
                "                        GROUP BY SHP.scrapBookId HAVING MAX(SHP.createdAt)) AS 'houseImgUrl',\n" +
                "    (SELECT pictureUrl FROM (ItemPictures IP inner join ScrapItems SI on IP.itemId = SI.itemId)\n" +
                "                       WHERE SI.scrapBookId = SB.scrapBookId\n" +
                "                       GROUP BY SI.scrapBookId HAVING MAX(SI.createdAt)) AS 'itemImgUrl'\n" +
                "FROM (Scraps S inner join ScrapBooks SB on S.userId = SB.userId)\n" +
                "WHERE S.userId = ?\n" +
                "GROUP BY folderId;";
        long        retrieveUserFoldersQueryParams = userId;

        return  this.jdbcTemplate.query(retrieveUserFoldersQuery,
                (rs, rowNum) -> new GetScrapFoldersRes(
                        rs.getLong("folderId"),
                        rs.getString("folderName"),
                        (rs.getString("houseImgUrl" ) == null || rs.getString("itemImgUrl") == null
                        ? null : (rs.getString("houseImgUrl")==null? rs.getString("itemImgUrl"):rs.getString("houseImgUrl")))
                )
            ,retrieveUserFoldersQueryParams);
    }

    private List<String>            retrieveUserScrapItemCategories(long    userId){
        String      retrieveUserScrapItemCategoriesQuery = "SELECT\n" +
                "    MC.description\n" +
                "FROM ((Items I inner join ScrapItems SI on I.itemId = SI.itemId)\n" +
                "     inner join ScrapBooks SB on SB.scrapBookId = SI.scrapBookId)\n" +
                "     inner join MiniCategories MC on MC.miniCategoryId = I.miniCategoryId\n" +
                "WHERE SB.userId = ?\n" +
                "GROUP BY MC.description;";
        long        retrieveUserScrapItemCategoriesQueryParams = userId;

        return this.jdbcTemplate.query(retrieveUserScrapItemCategoriesQuery,
                (rs, rowNum) -> rs.getString("MC.description"), retrieveUserScrapItemCategoriesQueryParams);
    }

    private List<GetScrapItemRes>   retrieveScrapItems(long userId){
        String  retrieveScrapItemsQuery = "SELECT\n" +
                "    I.itemId        AS 'itemId',\n" +
                "    (SELECT pictureUrl FROM ItemPictures IP\n" +
                "                       WHERE IP.itemId = I.itemId GROUP BY IP.itemId HAVING MIN(IP.itemPicId))   AS 'thumbnail',\n" +
                "    MC.description  AS 'category',\n" +
                "    I.companyid     AS 'companyId',\n" +
                "    C.name          AS 'companyName',\n" +
                "    CASE WHEN (SELECT round(AVG(score),1)  FROM Reviews R WHERE R.optionId = IO.optionId) is null\n" +
                "        THEN 0 ELSE (SELECT round(AVG(score),1)  FROM Reviews R WHERE R.optionId = IO.optionId) END           AS 'score',\n" +
                "    CASE WHEN (SELECT COUNT(reviewId)   FROM Reviews R WHERE R.optionId = IO.optionId) is null\n" +
                "        THEN 0 ELSE FORMAT((SELECT count(reviewId)   FROM Reviews R WHERE R.optionId = IO.optionId),0) END AS 'reviewCnt',\n" +
                "    concat(round((IO.price-IO.saledPrice)*100/IO.price, 0) ,'%') AS 'saleRate',\n" +
                "    FORMAT(IO.saledPrice,0)     AS 'price',\n" +
                "    CASE WHEN round((IO.price-IO.saledPrice)*100/IO.price, 0) >= 30 THEN '특가' ELSE '할인가' END AS 'specialPrice'\n" +
                "FROM ((((Items I inner join ScrapItems SI on SI.itemId = I.itemId)\n" +
                "    inner join ScrapBooks SB on SB.scrapBookId = SI.scrapBookId)\n" +
                "    inner join Companies C on C.companyId = I.companyId)\n" +
                "    inner join MiniCategories MC on MC.miniCategoryId = I.miniCategoryId)\n" +
                "    left join ItemOptions IO on IO.itemId = I.itemId\n" +
                "WHERE SB.userId = ?\n" +
                "GROUP BY IO.itemId;";
        long    retrieveScrapItemsQueryParams = userId;

        return this.jdbcTemplate.query(retrieveScrapItemsQuery,
                (rs, rowNum) -> new GetScrapItemRes(
                        rs.getLong("itemId"),
                        rs.getString("thumbnail"),
                        rs.getString("category"),
                        rs.getLong("companyId"),
                        rs.getString("companyName"),
                        rs.getDouble("score"),
                        rs.getString("reviewCnt"),
                        rs.getString("saleRate"),
                        rs.getString("price"),
                        rs.getString("specialPrice")
                )
                ,retrieveScrapItemsQueryParams);
    }

    private List<GetScrapHousePicRes>   retrieveUserScrapHousePics(long userId){
        String  retrieveUserScrapHousePicsQuery = "SELECT\n" +
                "    U.userId    AS 'userId',\n" +
                "    U.name      AS 'userName',\n" +
                "    HP.housePicId   AS 'housePicId',\n" +
                "    HP.description  AS 'title',\n" +
                "    HI.houseImgUrl  AS 'thumbnail'\n" +
                "FROM (((HousePictures HP inner join ScrapHousePics SHP on HP.housePicId = SHP.housePicId)\n" +
                "     inner join ScrapBooks SB on SB.scrapBookId = SHP.scrapBookId)\n" +
                "     inner join Users U on U.userId = HP.userId)\n" +
                "    left join HouseImgs HI on HI.housePicId = HP.housePicId\n" +
                "WHERE SB.userId = ?\n" +
                "GROUP BY HI.housePicId;";
        long    retrieveUserScrapHousePicsQueryParams = userId;

        return this.jdbcTemplate.query(
                retrieveUserScrapHousePicsQuery,
                (rs, rowNum) -> new GetScrapHousePicRes(
                        rs.getLong("housePicId"),
                        rs.getString("thumbnail"),
                        rs.getString("userName"),
                        rs.getLong("userId"),
                        rs.getString("title")
                )
                ,retrieveUserScrapHousePicsQueryParams
        );
    }

    public GetScrapsRes   retrieveUserScraps(long userId){
        String      retrieveUserScrapsQuery = "SELECT name, profilePicUrl FROM Users WHERE userId = ?;";
        long        retrieveUserScrapsQueryParams = userId;

        return this.jdbcTemplate.queryForObject(
                retrieveUserScrapsQuery,
                (rs, rowNum) -> new GetScrapsRes(
                        retrieveUserScrapsQueryParams,
                        rs.getString("name"),
                        rs.getString("profilePicUrl"),
                        retrieveUserFolders(retrieveUserScrapsQueryParams),
                        retrieveUserScrapItemCategories(retrieveUserScrapsQueryParams),
                        retrieveScrapItems(retrieveUserScrapsQueryParams),
                        retrieveUserScrapHousePics(retrieveUserScrapsQueryParams)
                )
                ,retrieveUserScrapsQueryParams
        );
    }

    private List<GetCouponRes>      retrieveUserCouponsInKart(long    userId){
        String          retrieveUserCouponsQuery = "SELECT\n" +
                "    couponId,\n" +
                "    DATE_FORMAT(due, '%Y년 %m월 %d일까지') AS 'due',\n" +
                "    description,\n" +
                "    saleAmount,\n" +
                "    saleRate\n" +
                "FROM Coupons\n" +
                "WHERE userId = ? AND status = 'R'\n" +
                "      AND TIMESTAMPDIFF(SECOND, CURRENT_TIMESTAMP, due) > 0;";
        long            retrieveUserCouponsQueryParams = userId;

        return  this.jdbcTemplate.query(retrieveUserCouponsQuery,
                (rs, rowNum)-> new GetCouponRes(
                        rs.getLong("couponId"),
                        rs.getString("description"),
                        rs.getString("due"),
                        rs.getInt("saleAmount"),
                        rs.getInt("saleRate")
                ),
                retrieveUserCouponsQueryParams);
    }

    private List<GetKartInfoRes>    retrieveOrderedItems(List<Long> orderItems){
        int                     length = orderItems.size();
        List<GetKartInfoRes>    ret = new ArrayList<GetKartInfoRes>();
        String      retrieveOrderedItemsQuery = "SELECT\n" +
                "    (SELECT pictureUrl FROM ItemOptionPictures IOP\n" +
                "     WHERE IOP.optionId = IO.optionId\n" +
                "     GROUP BY IOP.optionId)                             AS 'thumbnail',\n" +
                "    IO.optionName                                       AS 'optionName',\n" +
                "    IO.optionId                                         AS 'optionId',\n" +
                "    number                                              AS 'itemNum',\n" +
                "    concat(FORMAT(number*saledPrice, 0),'원')           AS 'price',\n" +
                "    CASE WHEN deliveryPrice = 0 THEN '무료배송'\n" +
                "         ELSE concat(FORMAT(deliveryPrice,0),'원') end   AS 'delivery'\n" +
                "FROM ((KartItems K inner join ItemOptions IO on K.optionId = IO.optionId)\n" +
                "     inner join Items I on I.itemId = IO.itemId)\n" +
                "WHERE kartId = ?;";

        for(int i=0; i<length;++i){
            long        retrieveOrderedItemsQueryParams = orderItems.get(i);

            GetKartInfoRes getKartInfoRes = this.jdbcTemplate.queryForObject(
                    retrieveOrderedItemsQuery,
                    (rs, rowNum) -> new GetKartInfoRes(
                            retrieveOrderedItemsQueryParams,
                            rs.getString("thumbnail"),
                            rs.getString("optionName"),
                            rs.getLong("optionId"),
                            rs.getInt("itemNum"),
                            rs.getString("price"),
                            rs.getString("delivery")
                    )
                    ,retrieveOrderedItemsQueryParams
            );
            ret.add(getKartInfoRes);
        }

        return ret;
    }

    private List<GetCouponRes>      retrievePayCoupons(long    userId){
        String      retrievePayCouponsQuery = "SELECT\n" +
                "    couponId,\n" +
                "    DATE_FORMAT(due, '%Y년 %m월 %d일까지') AS 'due',\n" +
                "    description,\n" +
                "    saleAmount,\n" +
                "    saleRate\n" +
                "FROM Coupons\n" +
                "WHERE userId = 1 AND status = 'R'\n" +
                "      AND TIMESTAMPDIFF(SECOND, CURRENT_TIMESTAMP, due) > 0;";
        long        retrievePayCouponsQueryParams = userId;

        return this.jdbcTemplate.query(retrievePayCouponsQuery,
                (rs, rowNum) -> new GetCouponRes(
                        rs.getLong("couponId"),
                        rs.getString("description"),
                        rs.getString("due"),
                        rs.getInt("saleAmount"),
                        rs.getInt("saleRate") == 0? 0:
                                100-rs.getInt("saleRate")
                )
                ,
                retrievePayCouponsQueryParams);
    }

    private GetUserKartRes  retrievePaymentKart(GetOrderReq getOrderReq){
        String  retrievePaymentKartQuery = "SELECT\n" +
                "    concat(FORMAT(SUM(deliveryPrice),0),'원')                   AS 'delivery',\n" +
                "    concat(FORMAT(SUM(number), 0),'개')                       AS 'number',\n" +
                "    concat(FORMAT(SUM(saledPrice*number),0), '원')                     AS 'saledPrice',\n" +
                "    concat(FORMAT(SUM(price*number),0),'원')                           AS 'price',\n" +
                "    concat(FORMAT(SUM(price*number)-SUM(saledPrice*number),0),'원')           AS 'discountPrice'\n" +
                "FROM (KartItems K inner join ItemOptions IO on IO.optionId = K.optionId)\n" +
                "WHERE\n" +
                "    K.userId = ? AND K.status = 'P';";
        long    retrievePaymentKartQueryParams = getOrderReq.getUserId();

        return  this.jdbcTemplate.queryForObject(
                retrievePaymentKartQuery,
                (rs, rowNum) -> new GetUserKartRes(
                        retrieveOrderedItems(getOrderReq.getKartId()),
                        rs.getString("number"),
                        rs.getString("saledPrice"),
                        rs.getString("price"),
                        rs.getString("discountPrice"),
                        rs.getString("delivery")
                        )
                ,
                retrievePaymentKartQueryParams
        );
    }

    public  GetOrderRes     retrievePayment(GetOrderReq getOrderReq){
        String      retrievePaymentQuery = "SELECT point FROM Users WHERE userId = ?;";
        long        retrievePaymentQueryParams = getOrderReq.getUserId();

        int         length = getOrderReq.getKartId().size();
        String      checkPaymentKartIdQuery = "UPDATE KartItems\n" +
                "SET status = 'P'\n" +
                "WHERE kartId = ?;";

        for(int i=0;i<length;++i){
            long        checkPaymentKartIdQueryParams = getOrderReq.getKartId().get(i);
            this.jdbcTemplate.update(checkPaymentKartIdQuery, checkPaymentKartIdQueryParams);
        }

        return  new GetOrderRes(
                retrieveUserCouponsInKart(retrievePaymentQueryParams),
                this.jdbcTemplate.queryForObject(retrievePaymentQuery,
                        int.class, retrievePaymentQueryParams),
                retrievePaymentKart(getOrderReq)
        );
    }

    private long        createOrderId(PostOrderReq postOrderReq){
        String      createOrderQuery = "INSERT INTO ItemOrder(userId, orderName, phoneNumber, email, receivedName, receivedPhone, placeName, addressCode, address)\n" +
                "VALUES (?, ?,?, ?, ?, ?, ?, ?, ?);";
        Object[]    createOrderQueryParams = new Object[]{
                postOrderReq.getUserId(), postOrderReq.getOrderName(), postOrderReq.getPhoneNum(), postOrderReq.getEmail(),
                postOrderReq.getReceivedName(), postOrderReq.getReceivedPhone(), postOrderReq.getPlaceName(),postOrderReq.getAddressCode(), postOrderReq.getAddress()
        };
        this.jdbcTemplate.update(createOrderQuery, createOrderQueryParams);

        String      retrieveLastInsertIdQuery = "SELECT last_insert_id();";

        return  this.jdbcTemplate.queryForObject(retrieveLastInsertIdQuery, long.class);
    }

    private void        createReceiptId(PostOrderReq postOrderReq, long orderId){
        int length = postOrderReq.getKartId().size();
        String      updateKartStatusQuery = "UPDATE KartItems\n" +
                "SET status = 'Y'\n" +
                "WHERE kartId = ?;";

        String      createBoughtItemsQuery = "INSERT INTO BoughtItems(orderId, kartId, receiptId)\n" +
                "VALUES (?, ?, ?);";

        String      createReceiptQuery = "INSERT INTO Receipts(userId) VALUES (?)";
        long        createReceiptQueryParams = postOrderReq.getUserId();

        this.jdbcTemplate.update(createReceiptQuery, createReceiptQueryParams);

        String      retrieveLastInsertIdQuery = "SELECT last_insert_id();";

        long        receiptId = this.jdbcTemplate.queryForObject(retrieveLastInsertIdQuery, long.class);

        for(int i=0; i<length; ++i){
            long    updateKartStatusQueryParams = postOrderReq.getKartId().get(i);
            this.jdbcTemplate.update(updateKartStatusQuery, updateKartStatusQueryParams);

            Object[]    createBoughtItemsQueryParams = new Object[]{orderId, updateKartStatusQueryParams, receiptId};
            this.jdbcTemplate.update(createBoughtItemsQuery, createBoughtItemsQueryParams);
        }

        return;
    }

    public  PostOrderRes    createOrder(PostOrderReq postOrderReq){

        long        orderId = createOrderId(postOrderReq);

        createReceiptId(postOrderReq, orderId);

        String      retrieveOrderResultQuery = "SELECT\n" +
                "    receiptId,\n" +
                "    CASE WHEN (SELECT COUNT(boughtId) FROM BoughtItems BI WHERE BI.orderId = ?) > 1 THEN\n" +
                "        concat(MAX(itemName), ' 외') ELSE itemName END  AS 'itemName',\n" +
                "    concat(SUM(KI.number), '개') AS 'count',\n" +
                "    concat(FORMAT(SUM(saledPrice*KI.number)+SUM(deliveryPrice),0),'원') as 'price',\n" +
                "    MAX((SELECT pictureUrl FROM ItemPictures IP WHERE IP.itemId = I.itemId GROUP BY IP.itemId)) AS 'thumbnail'\n" +
                "FROM (((BoughtItems BI inner join KartItems KI on BI.kartId = KI.kartId)\n" +
                "     inner join ItemOptions IO ON IO.optionId = KI.optionId)\n" +
                "    inner join Items I on I.itemId = IO.itemId)\n" +
                "WHERE BI.orderId = ?;";
        Object[]    retrieveOrderResultQueryParams = new Object[]{orderId, orderId};

        return this.jdbcTemplate.queryForObject(
                retrieveOrderResultQuery,
                (rs, rowNum) -> new PostOrderRes(
                        rs.getLong("receiptId"),
                        rs.getString("itemName"),
                        rs.getString("thumbnail"),
                        rs.getString("count"),
                        rs.getString("price")
                )
                ,retrieveOrderResultQueryParams
        );
    }

    public int      checkCouponId(long  couponId){
        String      checkCouponIdQuery = "SELECT EXISTS(\n" +
                "    SELECT couponId FROM Coupons WHERE couponId = ?\n" +
                "           );";
        long        checkCouponIdQueryParams = couponId;

        return  this.jdbcTemplate.queryForObject(checkCouponIdQuery, int.class, checkCouponIdQueryParams);
    }


    public List<GetUserRes> getUserByKakaoId(long userId) {
        String  getUserByKakaoIdQuery = "SELECT * FROM Users WHERE (userId = ? AND status != 'DEACTIVATE')";
        long    getUserByKakaoIdQueryParams = userId;

        return this.jdbcTemplate.query(getUserByKakaoIdQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getLong("userId"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                )
                ,getUserByKakaoIdQueryParams);
    }

    public List<GetUserRes> getUserByEmail(String email) {
        String  getUserByEmailQuery = "SELECT * FROM Users WHERE (email = ? AND status != 'DEACTIVATE')";
        String    getUserByEmailQueryParams = email;

        return this.jdbcTemplate.query(getUserByEmailQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getLong("userId"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                )
                ,getUserByEmailQueryParams);
    }

    public int createKakaoId(long userId, String email) {
        String modifyUserNameQuery = "UPDATE Users SET userId = ? WHERE email = ? ";
        Object[] modifyUserNameParams = new Object[]{userId , email};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }

    public int  checkCouponCode(String  couponCode){
        String  checkCouponCodeQuery = "SELECT EXISTS(\n" +
                "    SELECT couponCodeId FROM CouponCodes\n" +
                "    WHERE couponCode = ? AND status = 'N'\n" +
                "           );";
        String  checkCouponCodeQueryParams = couponCode;

        return  this.jdbcTemplate.queryForObject(checkCouponCodeQuery, int.class, checkCouponCodeQueryParams);
    }

    private int updateCouponCodeStatus(String   couponCode){
        String  updateCouponCodeStatusQuery = "UPDATE CouponCodes\n" +
                "SET status = 'Y'\n" +
                "WHERE couponCode = ?;";
        String  updateCouponCodeStatusQueryParams = couponCode;

        return this.jdbcTemplate.update(updateCouponCodeStatusQuery, updateCouponCodeStatusQueryParams);
    }

    private GetCouponCodeRes        retrieveCouponCodeInfo(String   couponCode){
        String      retrieveCouponCodeInfoQuery = "SELECT due, description, saleAmount, saleRate\n" +
                "FROM CouponCodes WHERE couponCode = ?;";
        String      retrieveCouponCodeInfoQueryParams = couponCode;

        return  this.jdbcTemplate.queryForObject(
                retrieveCouponCodeInfoQuery,
                (rs, rowNum) -> new GetCouponCodeRes(
                        rs.getString("due"),
                        rs.getString("description"),
                        rs.getInt("saleAmount"),
                        rs.getInt("saleRate")
                )
                ,retrieveCouponCodeInfoQueryParams
        );
    }

    public PostCouponRes    createCoupon(PostCouponReq postCouponReq){
        int             res = updateCouponCodeStatus(postCouponReq.getCouponCode());
        if(res == 0){

        }

        GetCouponCodeRes getCouponCodeRes = retrieveCouponCodeInfo(postCouponReq.getCouponCode());

        String              createCouponQuery = "INSERT INTO Coupons(userId, due, description, saleAmount, saleRate)\n" +
                "VALUES (?, ?, ?, ?, ?);";
        Object[]            createCouponQueryParams = new Object[]{
                postCouponReq.getUserId(), getCouponCodeRes.getDue(), getCouponCodeRes.getDescription(),
                getCouponCodeRes.getSaleAmount(), getCouponCodeRes.getSaleRate()
        };
        this.jdbcTemplate.update(createCouponQuery, createCouponQueryParams);

        String      retrieveLastInsertId = "SELECT LAST_INSERT_ID();";

        PostCouponRes postCouponRes = new PostCouponRes(
                this.jdbcTemplate.queryForObject(retrieveLastInsertId, long.class),
                "성공적으로 쿠폰을 등록했습니다."
        );

        return  postCouponRes;
    }

    private List<GetLikePosts>        retrieveUserLikePosts(long  userId, long categoryId){
        String      retrieveUserLikesPostsQuery = "SELECT\n" +
                "    houseImgUrl                 AS 'thumbnail',\n" +
                "    HP.housePicId               AS 'housePicId',\n" +
                "    HPC.description             AS 'category'\n" +
                "FROM ((HousePicLikes HPL inner join HousePictures HP on HPL.housePicId = HP.housePicId)\n" +
                "     inner join HousePicCateogries  HPC on HPC.houseCategoryId = HP.houseCategoryId)\n" +
                "     left join HouseImgs HI on HI.housePicId = HP.housePicId\n" +
                "WHERE HPL.userId = ?\n" +
                (categoryId == 0 ? "" : "AND HPC.houseCategoryId = "+categoryId+"\n")+
                "GROUP BY HI.housePicId;";
        long        retrieveUserLikesPostsQueryParams = userId;

        return  this.jdbcTemplate.query(retrieveUserLikesPostsQuery,
                (rs, rowNum) -> new GetLikePosts(
                        rs.getLong("housePicId"),
                        rs.getString("thumbnail"),
                        rs.getString("category")
                )
                ,retrieveUserLikesPostsQueryParams);
    }

    private List<GetHouseCategoryRes>   retrieveLikeHouseCategory(long  userId){
        String      retrieveLikeHouseCategoryQuery = "SELECT\n" +
                "    HPC.description     AS 'category',\n" +
                "    COUNT(houseLikeId)  AS 'count'\n" +
                "FROM\n" +
                "    (HousePicLikes HPL inner join HousePictures HP on HPL.housePicId = HP.housePicId)\n" +
                "    inner join HousePicCateogries HPC on HPC.houseCategoryId = HP.housePicId\n" +
                "WHERE HPL.userId = ?\n" +
                "GROUP BY HP.houseCategoryId;";
        long        retrieveLikeHouseCategoryQueryParams = userId;

        return this.jdbcTemplate.query(
                retrieveLikeHouseCategoryQuery,
                (rs, rowNum) -> new GetHouseCategoryRes(
                        rs.getString("category"),
                        rs.getInt("count")
                )
                ,retrieveLikeHouseCategoryQueryParams
        );
    }

    public GetUserLikeRes       retrieveUserLike(long   userId, long categoryId){
        String      retrieveUserLikeCountQuery = "SELECT COUNT(houseLikeId)   FROM HousePicLikes\n" +
                "WHERE userId = ?;";
        long        retrieveUserLikeCountQueryParams = userId;

        return  new GetUserLikeRes(
          this.jdbcTemplate.queryForObject(retrieveUserLikeCountQuery, int.class, retrieveUserLikeCountQueryParams),
          retrieveUserLikePosts(userId, categoryId),
          retrieveLikeHouseCategory(userId)
        );
    }

    public int      checkLikeCategory(long  userId, long categoryId){
        String      checkLikeCategoryQuery = "SELECT EXISTS(\n" +
                "    SELECT HPL.houseLikeId\n" +
                "    FROM (HousePicLikes HPL inner join HousePictures HP on HPL.housePicId = HP.housePicId)\n" +
                "          inner join HousePicCateogries HPC on HPC.houseCategoryId = HP.houseCategoryId\n" +
                "    WHERE HPL.userId = ? AND HPC.houseCategoryId = ?\n" +
                "           );";

        Object[]    checkLikeCategoryQueryParams = new Object[]{
                userId, categoryId
        };

        return this.jdbcTemplate.queryForObject(checkLikeCategoryQuery, int.class, checkLikeCategoryQueryParams);
    }

    public List<GetUserFollowingRes>        retrieveFollowing(long  userId){
        String      retrieveFollowingQuery = "SELECT\n" +
                "    profilePicUrl,\n" +
                "    U.userId        AS 'userId',\n" +
                "    name\n" +
                "FROM (Users U inner join Follows F on U.userId = F.userId)\n" +
                "WHERE followedId = ?;";
        long        retrieveFollowingQueryParams = userId;

        return  this.jdbcTemplate.query(
                retrieveFollowingQuery,
                (rs, rowNum) -> new GetUserFollowingRes(
                        rs.getLong("userId"),
                        rs.getString("profilePicUrl"),
                        rs.getString("name")
                )
                ,retrieveFollowingQueryParams
        );
    }
}
