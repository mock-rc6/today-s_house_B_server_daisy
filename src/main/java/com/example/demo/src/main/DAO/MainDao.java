package com.example.demo.src.main.DAO;

import com.example.demo.src.main.model.*;
import com.example.demo.src.review.model.GetMyReviewsRes;
import com.example.demo.src.store.model.GetInquiryAnswerRes;
import com.example.demo.src.store.model.GetInquiryRes;
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

        String      retrieveEventNameQuery = "SELECT description FROM rising_test.Events WHERE eventId = ?;";
        long        retrieveEventNameQueryParams = eventId;

        return  new GetEventDetailRes(eventId,
                this.jdbcTemplate.queryForObject(retrieveEventNameQuery, String.class,retrieveEventNameQueryParams),
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
                "    R.reviewId AS 'reviewId',\n" +
                "   reviewPicUrl\n" +
                "FROM (((Reviews R inner join Users U on R.userId = U.userId)\n" +
                "      inner join ItemOptions IO on IO.optionId = R.optionId)\n" +
                "      inner join Items I on I.itemId = IO.itemId)\n" +
                "      left join ReviewPics RP on RP.reviewId = R.reviewId \n"+
                "WHERE U.userId = ?\n")+
                (isPhotoReview? "AND EXISTS (SELECT reviewPicId FROM ReviewPics RP WHERE RP.reviewId = R.reviewId) = 1\n": "")+
                (isBestReviews?"ORDER BY score DESC;" : "ORDER BY createdAt DESC;");
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
                        rs.getString("reviewPicUrl")
                )
                ,
                retrieveMyReviewsQueryParams
        );
    }

    public GetReviewWriteRes        retrieveReviewWrite(long    optionId, long    userId){
        String      retrieveReviewWrite = "SELECT\n" +
                "    concat(concat(itemName,' '),optionName)        AS 'name',\n" +
                "    pictureUrl      AS 'thumbnail',\n" +
                "    I.itemId        AS 'itemId'\n" +
                "FROM\n" +
                "    (Items I inner join ItemPictures IP on I.itemId = IP.itemId)\n" +
                "    left join ItemOptions IO on IO.itemId = I.itemId\n" +
                "WHERE\n" +
                "    IO.optionId = ?\n" +
                "GROUP BY IP.itemId;";
        long        retrieveReviewWriteQuery = optionId;

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

    public int      checkBoughtItem(long    optionId){
        String      checkBoughtItemQuery = "SELECT EXISTS(\n" +
                "    SELECT kartId FROM (KartItems KI inner join ItemOptions IO on KI.optionId = IO.optionId)\n" +
                "    WHERE KI.status = 'Y' AND IO.optionId = ?\n" +
                "           );";
        long        checkBoughtItemQueryParams = optionId;

        return  this.jdbcTemplate.queryForObject(checkBoughtItemQuery, int.class, checkBoughtItemQueryParams);
    }

    public PostReviewRes    createReview(PostReviewReq  postReviewReq){
        int         buy = checkBoughtItem(postReviewReq.getOptionId());

        String      createReviewQuery = "INSERT INTO Reviews(optionId, userId, score, description, buy)\n" +
                "VALUE(?,?,?,?,?);";
        Object[]    createReviewQueryParams = new Object[]{
                postReviewReq.getOptionId(), postReviewReq.getUserId(), postReviewReq.getScore(),
                postReviewReq.getReviewDescription(), buy
        };
        this.jdbcTemplate.update(createReviewQuery, createReviewQueryParams);

        String      retrieveLastInsertQuery = "SELECT LAST_INSERT_ID();";
        return new PostReviewRes(
                this.jdbcTemplate.queryForObject(retrieveLastInsertQuery, long.class)
                ,"성공적으로 리뷰가 등록되었습니다.");
    }

    public int      checkReviewOption(long  userId, long    optionId){
        String      checkReviewOptionQuery = "SELECT EXISTS(\n" +
                "    SELECT reviewId\n" +
                "    FROM (Reviews R inner join ItemOptions IO on IO.optionId = R.optionId)\n" +
                "        inner join Items I on I.itemId = IO.optionId\n" +
                "    WHERE R.optionId = ? AND R.userId = ?\n" +
                "           );";
        Object[]    checkReviewOptionQueryParams = new Object[]{optionId, userId};

        return  this.jdbcTemplate.queryForObject(checkReviewOptionQuery, int.class, checkReviewOptionQueryParams);
    }

    public GetGuestOrderRes     retrieveGuestOrder(GetGuestOrderReq getGuestOrderReq){
        String      retrieveGuestOrderQuery = "SELECT\n" +
                "    CASE WHEN COUNT(guestOrderItemId) > 1 THEN concat(concat(concat(itemName, ' '), optionName), ' 외')\n" +
                "    ELSE concat(concat(itemName, ' '), optionName) END AS 'orderItemName',\n" +
                "    concat(FORMAT((SELECT\n" +
                "         SUM(count*IO.saledPrice) + SUM(deliveryPrice)\n" +
                "     FROM GuestOrders WHERE GuestOrders.guestOrderId = GOI.guestOrderId\n" +
                "         ),0), '원') AS 'price',\n" +
                "    concat(SUM(count),'개')  AS 'count',\n" +
                "    CASE WHEN GO.status = 'PURCHASED' THEN '입금 완료'\n" +
                "         WHEN GO.status = 'READY' THEN '상품 준비 중'\n" +
                "        WHEN GO.status = 'DELIVERY' THEN '상품 배송 중'\n" +
                "        ELSE '배송 완료' END AS 'status',\n" +
                "    DATE_FORMAT(createdAt, '%Y-%m-%d')  as 'orderDate',\n" +
                "    DATE_FORMAT(updatedAt, '%Y-%m-%d')  as 'updateDate',\n" +
                "    orderName,\n" +
                "    phoneNumber,\n" +
                "    receivedName,\n" +
                "    placeName,\n" +
                "    receivedPhone,\n" +
                "    addressCode,\n" +
                "    address\n" +
                "FROM\n" +
                "    ((GuestOrderItem GOI inner join GuestOrders GO on GOI.guestOrderId = GO.guestOrderId)\n" +
                "    inner join ItemOptions IO on IO.optionId = GOI.optionId)\n" +
                "    inner join Items I on I.itemId = IO.optionId\n" +
                "WHERE email = ? AND GO.guestOrderId = ?;";
        Object[]    retrieveGuestOrderQueryParams = new Object[]{
                getGuestOrderReq.getEmail(), getGuestOrderReq.getOrderNum()
        };

        return  this.jdbcTemplate.queryForObject(
                retrieveGuestOrderQuery,
                (rs, rowNum) -> new GetGuestOrderRes(
                        rs.getString("orderItemName"),
                        rs.getString("price"),
                        rs.getString("count"),
                        rs.getString("status"),
                        rs.getString("orderDate"),
                        rs.getString("updateDate"),
                        rs.getString("orderName"),
                        rs.getString("phoneNumber"),
                        rs.getString("placeName"),
                        rs.getString("receivedPhone"),
                        rs.getString("addressCode"),
                        rs.getString("address")
                )
                ,retrieveGuestOrderQueryParams
        );
    }

    public int      checkGuestOrderId(long  guestOrderId){
        String      checkGuestOrderIdQuery = "SELECT EXISTS(\n" +
                "    SELECT guestOrderId\n" +
                "    FROM GuestOrders WHERE guestOrderId = ?\n" +
                "           );";
        long        checkGuestOrderIdQueryParams = guestOrderId;

        return  this.jdbcTemplate.queryForObject(checkGuestOrderIdQuery, int.class, checkGuestOrderIdQueryParams);
    }

    public int      checkGuestOrderEmail(String email){
        String      checkGuestOrderEmailQuery = "SELECT EXISTS(\n" +
                "    SELECT guestOrderId\n" +
                "    FROM GuestOrders WHERE email= ?\n" +
                "           );";
        String      checkGuestOrderEmailQueryParams = email;

        return  this.jdbcTemplate.queryForObject(checkGuestOrderEmailQuery, int.class, checkGuestOrderEmailQueryParams);

    }
}