package com.example.demo.src.main.DAO;

import com.example.demo.src.main.model.*;
import com.example.demo.src.review.model.GetMyReviewsRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class MainDao {
    @Autowired
    private JdbcTemplate        jdbcTemplate;

    public void setDataSource(DataSource    dataSource){this.jdbcTemplate = new JdbcTemplate(dataSource);}

    public List<GetEventsRes>   retrieveEvents() {
        String      retrieveEventsQuery = "SELECT\n" +
                "    eventId,\n" +
                "    bannerPic,\n" +
                "    CASE WHEN TIMESTAMPDIFF(SECOND, CURRENT_TIMESTAMP, due) < 0\n" +
                "         THEN '종료'\n" +
                "         WHEN TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, due) < 1\n" +
                "         THEN '오늘 종료'\n" +
                "         ELSE concat(TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, due),'일 남음') END\n" +
                "    AS 'due'\n" +
                "FROM\n" +
                "    rising_test.Events\n" +
                "WHERE\n" +
                "    TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, due) > -30 AND\n" +
                "    TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, due) < 30\n" +
                ";";

        return this.jdbcTemplate.query(retrieveEventsQuery,
                (rs, rowNum) -> new GetEventsRes(
                        rs.getLong("eventId"),
                        rs.getString("bannerPic"),
                        rs.getString("due")
                )
                );
    }

    public int  checkEventId(long   eventId){
        String      checkEventIdQuery = "SELECT EXISTS(\n" +
                "    SELECT eventId FROM rising_test.Events\n" +
                "    WHERE TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, due) > -30 AND\n" +
                "          TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, due)<30 AND\n" +
                "          eventId = ?\n" +
                "           );";
        long        checkEventIdQueryParams = eventId;

        return this.jdbcTemplate.queryForObject(checkEventIdQuery, int.class, checkEventIdQueryParams);
    }

    public GetEventDetailRes    retrieveEventDetails(long   eventId){
        String      retrieveEventDetailsQuery = "SELECT\n" +
                "    imgUrl\n" +
                "FROM    EventImgs   WHERE eventId = ?;";
        long        retrieveEventDetailsQueryParams = eventId;

        return  new GetEventDetailRes(eventId,
                this.jdbcTemplate.query(retrieveEventDetailsQuery,
                        (rs, rowNum)-> rs.getString("imgUrl")
                        ,retrieveEventDetailsQueryParams));
    }

    public List<GetMainEventRes>      retrieveMainEvents(){
        String      getEventInfosQuery = "SELECT eventId, bannerPic\n" +
                "FROM rising_test.Events\n" +
                "WHERE TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, due) > -30 AND\n" +
                "      TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, due) < 30;";

        return  this.jdbcTemplate.query(
                getEventInfosQuery,
                (rs, rowNum) -> new GetMainEventRes(
                        rs.getString("bannerPic"),
                        rs.getLong("eventId")
                )
        );
    }

    public List<GetMainHouseRes>    retrieveMainHouse(){
        String      getHouseInfosQuery = "SELECT houseImgUrl, description\n" +
                "FROM HousePictures HP inner join HouseImgs HI on HP.housePicId = HI.housePicId\n" +
                "GROUP BY HP.housePicId;";

        return  this.jdbcTemplate.query(getHouseInfosQuery,
                (rs, rowNum) -> new GetMainHouseRes(
                        rs.getString("houseImgUrl"),
                        rs.getString("description")
                ));
    }
    public GetMainRes       retrieveMain()  {
        String      getCategoryIdQuery = "SELECT categoryId FROM Categories;";

        return  new GetMainRes(
                retrieveMainEvents(),
                this.jdbcTemplate.query(getCategoryIdQuery, (rs, rowNum) -> rs.getLong("categoryId")),
                retrieveMainHouse()
        );
    }

    public GetMyProfileRes  retrieveMyProfile(long  userId){
        String              retrieveMyProfileQuery = "SELECT\n" +
                "    U.name              as 'name',\n" +
                "    U.profilePicUrl     as 'profile',\n" +
                "    (SELECT COUNT(F.userId) FROM Follows F\n" +
                "            WHERE F.userId = U.userId) as 'follows',\n" +
                "    (SELECT COUNT(*) FROM Follows F WHERE F.followedId = U.userId) as 'followers',\n" +
                "    (SELECT COUNT(housePicId) FROM HousePicLikes HPC WHERE U.userId = HPC.userId) as 'likes',\n" +
                "    (SELECT COUNT(scrapId)  FROM Scraps S WHERE S.userId = U.userId) as 'scraps',\n" +
                "    (SELECT COUNT(receiptId) FROM Receipts R WHERE R.userId = U.userId AND\n" +
                "                                                   TIMESTAMPDIFF(MONTH, R.createdAt, CURRENT_TIMESTAMP)<=3) AS 'order history',\n" +
                "    (SELECT COUNT(couponId) FROM Coupons C WHERE C.userId = U.userId) as 'coupons',\n" +
                "    U.point as 'points',\n" +
                "    ((SELECT COUNT(inquiryId) FROM Inquiry I WHERE I.userId = U.userId)\n" +
                "     +(SELECT COUNT(inquiryAnswerId) FROM InquiryAnswers IA WHERE IA.userId = U.userId)) as 'inquiry',\n" +
                "    (SELECT COUNT(reviewId) FROM Reviews R WHERE R.userId = U.userId) as 'my reviews'\n" +
                "FROM\n" +
                "    Users U\n" +
                "WHERE userId = ?;";
        long                retrieveMyProfileQueryParams = userId;

        return  this.jdbcTemplate.queryForObject(retrieveMyProfileQuery,
                (rs, rowNum)->new GetMyProfileRes(
                        rs.getString("name"),
                        rs.getString("profile"),
                        rs.getInt("follows"),
                        rs.getInt("followers"),
                        rs.getInt("likes"),
                        rs.getInt("scraps"),
                        rs.getInt("order history"),
                        rs.getInt("coupons"),
                        rs.getInt("points"),
                        rs.getInt("inquiry"),
                        rs.getInt("my reviews")
                ),
                retrieveMyProfileQueryParams);
    }

    public GetMyShoppingRes     retrieveMyShopping(long userId){
        String              retrieveMyShoppingQuery = "SELECT\n" +
                "    (SELECT COUNT(couponId) FROM Coupons C WHERE C.userId = U.userId) as 'coupons',\n" +
                "    U.point as 'points',\n" +
                "    U.level as 'level',\n" +
                "    (SELECT COUNT(receiptId) FROM Receipts R\n" +
                "     WHERE TIMESTAMPDIFF(MONTH, R.createdAt, CURRENT_TIMESTAMP)<3 AND R.userId = U.userId\n" +
                "           AND R.status = 0) as 'waiting',\n" +
                "    (SELECT COUNT(receiptId) FROM Receipts R\n" +
                "     WHERE TIMESTAMPDIFF(MONTH, R.createdAt, CURRENT_TIMESTAMP)<3 AND R.userId = U.userId\n" +
                "           AND R.status = 1) as 'paid',\n" +
                "    (SELECT COUNT(receiptId) FROM Receipts R\n" +
                "     WHERE TIMESTAMPDIFF(MONTH, R.createdAt, CURRENT_TIMESTAMP)<3 AND R.userId = U.userId\n" +
                "           AND R.status = 2) as 'ready',\n" +
                "    (SELECT COUNT(receiptId) FROM Receipts R\n" +
                "     WHERE TIMESTAMPDIFF(MONTH, R.createdAt, CURRENT_TIMESTAMP)<3 AND R.userId = U.userId\n" +
                "           AND R.status = 3) as 'delivery',\n" +
                "    (SELECT COUNT(receiptId) FROM Receipts R\n" +
                "     WHERE TIMESTAMPDIFF(MONTH, R.createdAt, CURRENT_TIMESTAMP)<3 AND R.userId = U.userId\n" +
                "           AND R.status = 4) as 'finish',\n" +
                "    (SELECT COUNT(receiptId) FROM Receipts R\n" +
                "     WHERE TIMESTAMPDIFF(MONTH, R.createdAt, CURRENT_TIMESTAMP)<3 AND R.userId = U.userId\n" +
                "           AND R.status = 5) as 'reviewWritten',\n" +
                "    (SELECT COUNT(receiptId) FROM Receipts R\n" +
                "     WHERE TIMESTAMPDIFF(MONTH, R.createdAt, CURRENT_TIMESTAMP)<3 AND R.userId = U.userId) as 'bought',\n" +
                "    (SELECT COUNT(reviewId) FROM Reviews R WHERE R.userId = U.userId) as 'review',\n" +
                "    (SELECT COUNT(inquiryId) FROM Inquiry I WHERE I.userId = U.userId) as 'inquiry',\n" +
                "    (SELECT COUNT(scrapBookId) FROM ScrapBooks S WHERE S.userId = U.userId) as 'scraps'\n" +
                "FROM\n" +
                "    Users U\n" +
                "WHERE userId = ?;";
        long                retrieveMyShoppingQueryParams = userId;

        return this.jdbcTemplate.queryForObject(
                retrieveMyShoppingQuery,
                (rs, rowNum) -> new GetMyShoppingRes(
                        rs.getInt("coupons"),
                        rs.getInt("points"),
                        rs.getString("level"),
                        rs.getInt("waiting"),
                        rs.getInt("paid"),
                        rs.getInt("ready"),
                        rs.getInt("delivery"),
                        rs.getInt("finish"),
                        rs.getInt("reviewWritten"),
                        rs.getInt("bought"),
                        rs.getInt("review"),
                        rs.getInt("inquiry"),
                        rs.getInt("scraps")
                )
                , retrieveMyShoppingQueryParams
        );
    }

    /*
    private int                     checkReviewPhoto(long   userId){
        String      checkReviewPhotoQuery = "SELECT EXISTS(\n" +
                "    SELECT reviewPicId FROM (ReviewPics RP inner join Reviews R on RP.reviewId = R.reviewId)\n" +
                "    WHERE  R.userId = ?\n" +
                "           );";
        long        checkReviewPhotoQueryParams = userId;

        return  this.jdbcTemplate.queryForObject(checkReviewPhotoQuery, int.class, checkReviewPhotoQueryParams);
    }*/

    public List<GetMyReviewsRes>      retrieveMyReviews(long  userId, boolean isPhotoReview, boolean isBestReviews){
        String      retrieveMyReviewsQuery = ("SELECT\n" +
                "    R.userId        as 'userId',\n" +
                "    U.name          as 'userName',\n" +
                "    U.profilePicUrl as 'profilePic',\n" +
                "    R.score         as 'score',\n" +
                "    DATE_FORMAT(R.createdAt,'%Y-%m-%d') as 'createdAt',\n" +
                "    CASE WHEN R.buy = 0 THEN '오늘의집 구매'\n" +
                "         ELSE '다른 쇼핑몰 구매' END as 'buyAt',\n" +
                "    I.itemName  as 'itemName',\n" +
                "    LEFT(R.description, 200) as 'description',\n" +
                "    reviewId\n" +
                "FROM (((Reviews R inner join Users U on R.userId = U.userId)\n" +
                "      inner join ItemOptions IO on IO.optionId = R.optionId)\n" +
                "      inner join Items I on I.itemId = IO.itemId)\n" +
                "WHERE U.userId = ?\n")+
                (isPhotoReview? "AND EXISTS (SELECT reviewPicId FROM ReviewPics RP WHERE RP.reviewId = R.reviewId) = 1\n": "")+
                (isBestReviews?"ORDER BY score DESC;" : "ORDER BY createdAt DESC;");

        String      retrieveReviewImgsQuery = "SELECT reviewPicUrl\n" +
                "FROM (ReviewPics RP inner join Reviews R on RP.reviewId = R.reviewId)\n" +
                "WHERE R.userId = ? AND R.reviewId = ?;";

        long        retrieveMyReviewsQueryParams = userId;

        return  this.jdbcTemplate.query(
          retrieveMyReviewsQuery,
                (rs, rowNum) -> new GetMyReviewsRes(
                        rs.getLong("userId"),
                        rs.getString("userName"),
                        rs.getString("profilePic"),
                        rs.getDouble("score"),
                        rs.getString("createdAt"),
                        rs.getString("buyAt"),
                        rs.getString("itemName"),
                        rs.getString("description"),
                        this.jdbcTemplate.query(
                            retrieveReviewImgsQuery,
                                (rs2, rowNum2) -> rs2.getString("reviewPicUrl"),
                                retrieveMyReviewsQueryParams, rs.getLong("reviewId")
                        )
                )
                ,
                retrieveMyReviewsQueryParams
        );
    }

    public GetReviewWriteRes        retrieveReviewWrite(long    itemId, long    userId){
        String      retrieveReviewWrite = "SELECT\n" +
                "    itemName        AS 'name',\n" +
                "    pictureUrl      AS 'thumbnail',\n" +
                "    I.itemId        AS 'itemId'\n" +
                "FROM\n" +
                "    (Items I inner join ItemPictures IP on I.itemId = IP.itemId)\n" +
                "WHERE\n" +
                "    I.itemId = ?\n" +
                "GROUP BY IP.itemId;";
        long        retrieveReviewWriteQuery = itemId;

        String      subQuery = "SELECT reviewPicUrl\n" +
                "FROM ReviewPics RP inner join Reviews R on RP.reviewId = R.reviewId\n" +
                "WHERE R.userId = ?;";
        long        subQueryParams = userId;

        return this.jdbcTemplate.queryForObject(retrieveReviewWrite,
                (rs, rowNum) -> new GetReviewWriteRes(
                        rs.getString("name"),
                        rs.getString("thumbnail"),
                        rs.getLong("itemId"),
                        this.jdbcTemplate.query(subQuery,
                                (rs2, rowNum2) -> rs2.getString("reviewPicUrl")
                                ,subQueryParams)
                )
                ,retrieveReviewWriteQuery);
    }

    public int      checkItemId(long    itemId){
        String      checkItemIdQuery = "SELECT EXISTS(\n" +
                "    SELECT itemId FROM Items WHERE itemId = ?\n" +
                "           );";
        long        checkItemIdQueryParams = itemId;

        return  this.jdbcTemplate.queryForObject(checkItemIdQuery, int.class, checkItemIdQueryParams);
    }
}