package com.example.demo.src.store.dao;

import com.example.demo.src.category.model.GetCategory;
import com.example.demo.src.category.model.GetCategoryEventsRes;
import com.example.demo.src.review.model.GetMyReviewsRes;
import com.example.demo.src.store.model.*;
import com.example.demo.src.user.model.GetKartInfoRes;
import com.example.demo.src.user.model.GetUserKartRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class StoreDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private void    setDataSource(DataSource dataSource){this.jdbcTemplate = new JdbcTemplate(dataSource);}

    private List<GetStoreMainEvents>    retrieveStoreEvents(){
        String          retrieveStoreEventsQuery = "SELECT * FROM StoreEvents;";

        return  this.jdbcTemplate.query(retrieveStoreEventsQuery,
                (rs, rowNum)-> new GetStoreMainEvents(
                        rs.getLong("storeEventId"),
                        rs.getString("storeEventImgUrl"),
                        rs.getString("description")
                ));
    }

    private List<GetTodaysDealMainRes>  retrieveTodaysDealMain(){
        String      retrieveTodaysDealMainQuery = "SELECT\n" +
                "    I.itemId as 'itemId',\n" +
                "    C.name as 'company',\n" +
                "    C.companyId as 'companyId',\n" +
                "    concat(TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, due), '일 남음') as 'due' ,\n" +
                "    SC.name as 'subCategory',\n" +
                "    SC.subCategoryId as 'subcategoryId',\n" +
                "    concat(round((IO.price-IO.saledPrice)*100/IO.price),'%') as 'sale rate',\n" +
                "    I.itemName as 'itemName',\n" +
                "    concat(FORMAT(IO.saledPrice, 0),'원') as 'price',\n" +
                "    (SELECT COUNT(*) FROM Reviews R WHERE R.optionId = IO.optionId) as 'review num',\n" +
                "    CASE WHEN (SELECT AVG(score)   FROM Reviews R WHERE R.optionId = IO.optionId) is not null\n" +
                "         THEN (SELECT AVG(score)   FROM Reviews R WHERE R.optionId = IO.optionId)\n" +
                "         ELSE '0' end\n" +
                "        as 'score',\n" +
                "    (SELECT pictureUrl FROM ItemPictures IP WHERE IP.itemId = I.itemId GROUP BY itemId) as 'hotdealThumbnail'\n" +
                "FROM\n" +
                "    ((((TodayDeals TD inner join Items I on TD.itemId = I.itemId)\n" +
                "    inner join Companies C on C.companyId = I.companyId) inner join SubCategories SC on SC.subCategoryId = I.subCategoryId)\n" +
                "    inner join ItemOptions IO on IO.itemId = I.itemId)" +
                "GROUP BY I.itemId;";

        return  this.jdbcTemplate.query(
                retrieveTodaysDealMainQuery,
                (rs, rowNum) -> new GetTodaysDealMainRes(
                        rs.getLong("itemId"),
                        rs.getString("company"),
                        rs.getLong("companyId"),
                        rs.getString("due"),
                        rs.getString("subCategory"),
                        rs.getLong("subcategoryId"),
                        rs.getString("sale rate"),
                        rs.getString("price"),
                        rs.getString("itemName"),
                        rs.getInt("review num"),
                        rs.getDouble("score"),
                        rs.getString("hotdealThumbnail")
                )
        );
    }
    public GetStoreRes      retrieveStoreMain(){
        String      retrieveCategoryIdQuery = "SELECT categoryId FROM Categories";

        return  new GetStoreRes(
                retrieveStoreEvents(),
                this.jdbcTemplate.query(retrieveCategoryIdQuery, (rs, rowNum)->rs.getLong("categoryId")),
                retrieveTodaysDealMain()
        );
    }

    private List<GetCategory>     retrieveSubCategory(long    categoryId){
        String      retrieveSubCategoryQuery = "SELECT\n" +
                "    subCategoryId, SC.name\n" +
                "FROM\n" +
                "    SubCategories SC inner join Categories C on SC.categoryId = C.categoryId\n" +
                "WHERE\n" +
                "    C.categoryId = ?;";
        long        retrieveSubCategoryQueryParams = categoryId;

        return  this.jdbcTemplate.query(retrieveSubCategoryQuery,
                (rs, rowNum) -> new GetCategory(rs.getString("SC.name"), rs.getLong("subCategoryId"))
                ,retrieveSubCategoryQueryParams);
    }

    private List<GetCategoryEventsRes>  retrieveCategoryEventRes(long categoryId){
        String      retrieveCategoryEventResQuery = "SELECT\n" +
                "    C.categoryId, categoryEventImgUrl\n" +
                "FROM Categories C inner join CategoryEvents CE on C.categoryId = CE.categoryId\n" +
                "WHERE C.categoryId = ?;";
        long        retrieveCategoryEventResQueryParams = categoryId;

        return  this.jdbcTemplate.query(
                retrieveCategoryEventResQuery,
                (rs, rowNum) -> new GetCategoryEventsRes(
                        rs.getLong("C.categoryId"),
                        rs.getString("categoryEventImgUrl")
                )
                ,retrieveCategoryEventResQueryParams
        );
    }
    public GetStoreCategoryRes retrieveStoreCategory(long  categoryId){
        String      retrieveCategoryNameQuery = "SELECT name FROM Categories WHERE categoryId = ?;";
        long        retrieveCategoryNameQueryParams = categoryId;

        return  new GetStoreCategoryRes(
                this.jdbcTemplate.queryForObject(retrieveCategoryNameQuery, String.class, retrieveCategoryNameQueryParams),
                retrieveSubCategory(categoryId),
                retrieveCategoryEventRes(categoryId)
        );
    }

    public  int     checkItemId(long    itemId){
        String      checkItemIdQuery = "SELECT EXISTS(\n" +
                "    SELECT itemId   FROM Items\n" +
                "    WHERE itemId = ? AND status >=0\n" +
                "           );";
        long        checkItemIdQueryParams = itemId;

        return this.jdbcTemplate.queryForObject(checkItemIdQuery, int.class, checkItemIdQueryParams);
    }

    private List<GetMyReviewsRes>     retrieveItemReview(long itemId){
        String      retrieveItemReviewQuery = "SELECT\n" +
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
                "WHERE I.itemId = ?;";
        long        retrieveItemReviewQueryParams = itemId;

        String      retrieveReviewImgListQuery = "SELECT reviewPicUrl\n" +
                "FROM ((ReviewPics RP inner join Reviews R on RP.reviewId = R.reviewId)\n" +
                "     inner join ItemOptions IO on IO.optionId = R.optionId)\n" +
                "     inner join Items I on I.itemId = IO.itemId\n" +
                "WHERE I.itemId = ? AND R.userId = ?;";
        long        retrieveReviewImgListQueryParams = itemId;

        return  this.jdbcTemplate.query(
                retrieveItemReviewQuery,
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
                                retrieveReviewImgListQuery,
                                (rs2, rowNum2) -> rs2.getString("reviewPicUrl"),
                                retrieveReviewImgListQueryParams, rs.getLong("userId")
                        )
                )
                ,
                retrieveItemReviewQueryParams
        );

    }

    public GetStoreItemRes      retrieveStoreItem(long  itemId, long userId){
        String      retrieveStoreItemQuery = "SELECT\n" +
                "    I.itemName                                                                          AS 'itemName',\n" +
                "    C.companyId                                                                         AS 'companyId',\n" +
                "    C.name                                                                              AS 'companyName',\n" +
                "    case when\n" +
                "        round((SELECT AVG(score)  FROM Reviews R WHERE R.optionId = O.optionId),1) is not null\n" +
                "        then round((SELECT AVG(score)  FROM Reviews R WHERE R.optionId = O.optionId),1) else 0 end  AS 'score',\n" +
                "    (SELECT COUNT(*) FROM Reviews R WHERE R.optionId = O.optionId)                      AS 'reviewCnt',\n" +
                "    concat(round(100*(price-saledPrice)/price, 0),'%')                                          AS 'saleRate',\n" +
                "    CASE WHEN (SELECT COUNT(saledPrice) FROM ItemOptions O WHERE O.itemId = I.itemId)>1\n" +
                "         THEN concat(FORMAT(MIN(saledPrice),0), ' 외')\n" +
                "         ELSE concat(FORMAT(saledPrice,0),'원')                           END           AS 'price',\n" +
                "    (SELECT COUNT(scrapId) FROM Scraps S WHERE S.itemId = I.itemId)                     AS 'scrapCnt',\n" +
                "    (SELECT COUNT(reviewId) FROM Reviews R WHERE R.optionId = O.optionId AND R.score = 5)       AS 'five',\n" +
                "    (SELECT COUNT(reviewId) FROM Reviews R WHERE R.optionId = O.optionId AND R.score = 4)       AS 'four',\n" +
                "    (SELECT COUNT(reviewId) FROM Reviews R WHERE R.optionId = O.optionId AND R.score = 3)       AS 'three',\n" +
                "    (SELECT COUNT(reviewId) FROM Reviews R WHERE R.optionId = O.optionId AND R.score = 2)       AS 'two',\n" +
                "    (SELECT COUNT(reviewId) FROM Reviews R WHERE R.optionId = O.optionId AND R.score = 1)       AS 'one',\n" +
                "    (SELECT COUNT(inquiryId) FROM Inquiry WHERE Inquiry.optionId = O.optionId)                  AS 'inquiry',\n" +
                "    CASE WHEN scrapId is null THEN 'true' ELSE 'false' END                                      AS 'isScrap'\n" +
                "FROM (((\n" +
                "        (Items I left join ItemOptions O on I.itemId = O.itemId)\n" +
                "        inner join Companies C on C.companyId = I.companyId)\n" +
                "        left  join Reviews R on R.optionId = O.optionId)\n" +
                "        left  join ItemInfoPics IIP on I.itemId = IIP.itemId\n" +
                "     ) left join Scraps S on S.status = 1 AND S.itemId = I.itemId AND S.userId = ?\n" +
                "WHERE\n" +
                "    I.itemId = ?;\n";
        Object[]        retrieveStoreItemQueryParams = new Object[]{userId, itemId};

        String  retrieveItemImgQuery = "SELECT pictureUrl FROM ItemPictures WHERE itemId = ? ;";
        String  retrieveItemInfoImgQuery = "SELECT itemInfoPicUrl FROM ItemInfoPics WHERE itemId = ?;";

        return  this.jdbcTemplate.queryForObject(
          retrieveStoreItemQuery,
                (rs, rowNum) -> new GetStoreItemRes(
                        rs.getString("itemName"),
                        this.jdbcTemplate.query(retrieveItemImgQuery,
                                (rs2, rowNum2)-> rs2.getString("pictureUrl")
                                ,itemId),
                        rs.getLong("companyId"),
                        rs.getString("companyName"),
                        rs.getDouble("score"),
                        rs.getInt("reviewCnt"),
                        rs.getString("saleRate"),
                        rs.getString("price"),
                        rs.getInt("scrapCnt"),
                        this.jdbcTemplate.query(
                                retrieveItemInfoImgQuery,
                                (rs2, rowNum2)-> rs2.getString("itemInfoPicUrl")
                                , itemId
                        ),
                        retrieveItemReview(itemId)
                        , rs.getInt("five"),
                        rs.getInt("four"),
                        rs.getInt("three"),
                        rs.getInt("two"),
                        rs.getInt("one"),
                        rs.getInt("inquiry"),
                        Boolean.parseBoolean(rs.getString("isScrap"))
                ),
                retrieveStoreItemQueryParams
        );
    }

    public List<GetItemOptionRes>     retrieveItemOptions(long itemId){
        String      retrieveItemOptionQuery = "SELECT\n" +
                "    concat(round((price-saledPrice)*100/price,0), '%')                  AS 'saleRate',\n" +
                "    FORMAT(saledPrice,0)                                        AS 'saledPrice',\n" +
                "    O.optionId                                                  AS 'optionId',\n" +
                "    O.optionName                                                AS 'optionName',\n" +
                "    CASE WHEN   round(saledPrice*100/price,0)>=30.0\n" +
                "         THEN '특가' ELSE '' END                                 AS 'specialPrice',\n" +
                "    CASE WHEN   O.deliveryPrice = 0 THEN '무료배송'\n" +
                "         ELSE concat(O.deliveryPrice,'원') END                   AS 'delivery',\n" +
                "    (SELECT pictureUrl FROM ItemOptionPictures IO\n" +
                "              WHERE O.optionId = IO.optionId\n" +
                "              GROUP BY IO.optionId)                             AS 'thumbnail'\n" +
                "FROM (Items I inner join ItemOptions O on I.itemId = O.itemId)\n" +
                "WHERE I.itemId = ?;";
        long        retrieveItemOptionQueryParams = itemId;

        return  this.jdbcTemplate.query(retrieveItemOptionQuery,
                (rs, rowNum) -> new GetItemOptionRes(
                        rs.getString("saleRate"),
                        rs.getString("saledPrice"),
                        rs.getLong("optionId"),
                        rs.getString("optionName"),
                        rs.getString("specialPrice"),
                        rs.getString("delivery"),
                        rs.getString("thumbnail")
                )
                ,
                retrieveItemOptionQueryParams
        );
    }

    public  int         checkItemOption(long    itemId, long    optionId){
        String      checkItemOptionQuery = "SELECT EXISTS(\n" +
                "    SELECT optionId\n" +
                "    FROM (ItemOptions IO inner join Items I on IO.itemId = I.itemId)\n" +
                "    WHERE optionId = ? AND I.itemId = ?\n" +
                "           );";
        Object[]    checkItemOptionQueryParams = new Object[]{optionId, itemId};

        return this.jdbcTemplate.queryForObject(checkItemOptionQuery, int.class, checkItemOptionQueryParams);
    }

    public int          checkOptionId(long  optionId){
        String          checkOptionIdQuery = "SELECT EXISTS(\n" +
                "    SELECT optionID FROM ItemOptions WHERE optionId = ?\n" +
                "           );";
        long            checkOptionIdQueryParams = optionId;

        return this.jdbcTemplate.queryForObject(checkOptionIdQuery, int.class, checkOptionIdQueryParams);
    }

    public long         createKartItem(PostKartItemReq postKartItemReq, long userId){
        String          createKartItemQuery = "INSERT INTO KartItems(optionId, userId, number)\n" +
                "VALUES(?, ?, ?);";
        Object[]        createKartItemQueryParams = new Object[]{
                Long.parseLong(postKartItemReq.getOptionId()), userId, Integer.parseInt(postKartItemReq.getNumber())};
        this.jdbcTemplate.update(createKartItemQuery, createKartItemQueryParams);

        String          lastInsertIdQuery = "SELECT last_insert_id();";
        return  this.jdbcTemplate.queryForObject(lastInsertIdQuery,long.class);
    }

    public int          checkKartItem(long  userId, long optionId){
        String      checkKartItemQuery = "SELECT EXISTS(\n" +
                "    SELECT kartId FROM KartItems\n" +
                "    WHERE optionId = ? AND userId = ? AND status= 'N'\n" +
                "           );";

        Object[]    checkKartItemQueryParams = new Object[]{optionId, userId};

        return  this.jdbcTemplate.queryForObject(checkKartItemQuery, int.class,
                checkKartItemQueryParams);
    }

    public PostScrapRes     createItemScrap(PostScrapReq    postScrapReq){
        String          createItemScrapQuery = "INSERT INTO Scraps(userId, itemId)\n" +
                "VALUES(?, ?);";
        Object[]        createItemScrapQueryParams = new Object[]{postScrapReq.getUserId(), postScrapReq.getItemId()};
        this.jdbcTemplate.update(createItemScrapQuery, createItemScrapQueryParams);

        String          retrieveLastInsertIdQuery = "SELECT last_insert_id();";
        String          message = "성공적으로 스크랩되었습니다.";

        return  new PostScrapRes(
                this.jdbcTemplate.queryForObject(retrieveLastInsertIdQuery, long.class)
                ,message
        );
    }

    private GetInquiryAnswerRes retrieveInquiryAnswer(long  inquiryId){
        String      retrieveInquiryAnswerQuery = "SELECT\n" +
                "    CASE WHEN I.isPublic = 1 THEN IA.description\n" +
                "        ELSE '비밀글입니다.' END AS 'description',\n" +
                "    DATE_FORMAT(IA.createdAt, '%Y-%m-%d')   AS 'createdAt',\n" +
                "    U.name      AS 'name'\n" +
                "FROM (InquiryAnswers IA inner join Users U on IA.userId = U.userId)\n" +
                "     inner join Inquiry I on I.inquiryId = IA.inquiryId\n" +
                "WHERE IA.inquiryId = ?;";
        long        retireveInquiryAnswerQueryParams = inquiryId;

        return  this.jdbcTemplate.queryForObject(retrieveInquiryAnswerQuery,
                (rs, rowNum) -> new GetInquiryAnswerRes(
                        rs.getString("name"),
                        rs.getString("createdAt"),
                        rs.getString("description")
                )
                ,retireveInquiryAnswerQueryParams);
    }

    public List<GetInquiryRes>      retrieveOptionInquiry(long  optionId){
        String      retrieveOptionInquiryQuery = "SELECT\n" +
                "    I.inquiryId AS 'inquiryId',\n" +
                "    inquiryCategory     AS 'category',\n" +
                "    CASE WHEN isPublic = 1 THEN '공개' ELSE '비밀글입니다.' END AS 'isPublic',\n" +
                "    title,\n" +
                "    I.description   AS 'description',\n" +
                "    DATE_FORMAT(I.createdAt, '%Y-%m-%d') AS 'createdAt',\n" +
                "    concat(LEFT(U.name, 2), '***')   as 'name'\n" +
                "FROM (Inquiry I inner join Users U on I.userId = U.userId)\n" +
                "WHERE I.optionId = ?;";
        long        retrieveOptionInquiryQueryParams = optionId;

        return      this.jdbcTemplate.query(
                retrieveOptionInquiryQuery,
                (rs, rowNum) -> new GetInquiryRes(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("createdAt"),
                        rs.getString("name"),
                        rs.getString("category"),
                        retrieveInquiryAnswer(rs.getLong("inquiryId"))
                )
                ,retrieveOptionInquiryQueryParams
        );
    }
}