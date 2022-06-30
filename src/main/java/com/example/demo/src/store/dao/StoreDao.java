package com.example.demo.src.store.dao;

import com.example.demo.src.category.model.GetCategory;
import com.example.demo.src.category.model.GetCategoryEventsRes;
import com.example.demo.src.review.model.GetMyReviewsRes;
import com.example.demo.src.store.model.*;
import lombok.AllArgsConstructor;
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
                "    concat(round(IO.saledPrice*100/IO.price),'%') as 'sale rate',\n" +
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
                "    inner join ItemOptions IO on IO.itemId = I.itemId);";

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

    public GetStoreItemRes      retrieveStoreItem(long  itemId){
        String      retrieveStoreItemQuery = "SELECT\n" +
                "    I.itemName                                                                          AS 'itemName',\n" +
                "    C.companyId                                                                         AS 'companyId',\n" +
                "    C.name                                                                              AS 'companyName',\n" +
                "    round((SELECT AVG(score)  FROM Reviews R WHERE R.optionId = O.optionId),1)          AS 'score',\n" +
                "    (SELECT COUNT(*) FROM Reviews R WHERE R.optionId = O.optionId)                      AS 'reviewCnt',\n" +
                "    concat(round(100*saledPrice/price, 0),'%')                                          AS 'saleRate',\n" +
                "    CASE WHEN (SELECT COUNT(saledPrice) FROM ItemOptions O WHERE O.itemId = I.itemId)>1\n" +
                "         THEN concat(FORMAT(MIN(saledPrice),0), ' 외')\n" +
                "         ELSE concat(FORMAT(saledPrice,0),'원')                           END           AS 'price',\n" +
                "    (SELECT COUNT(scrapId) FROM Scraps S WHERE S.itemId = I.itemId)                     AS 'scrapCnt',\n" +
                "    (SELECT COUNT(reviewId) FROM Reviews R WHERE R.optionId = O.optionId AND R.score = 5)       AS 'five',\n" +
                "    (SELECT COUNT(reviewId) FROM Reviews R WHERE R.optionId = O.optionId AND R.score = 4)       AS 'four',\n" +
                "    (SELECT COUNT(reviewId) FROM Reviews R WHERE R.optionId = O.optionId AND R.score = 3)       AS 'three',\n" +
                "    (SELECT COUNT(reviewId) FROM Reviews R WHERE R.optionId = O.optionId AND R.score = 2)       AS 'two',\n" +
                "    (SELECT COUNT(reviewId) FROM Reviews R WHERE R.optionId = O.optionId AND R.score = 1)       AS 'one',\n" +
                "    (SELECT COUNT(inquiryId) FROM Inquiry WHERE Inquiry.itemId = I.itemId)              AS 'inquiry'\n" +
                "FROM (((\n" +
                "        (Items I inner join ItemOptions O on I.itemId = O.itemId)\n" +
                "        inner join Companies C on C.companyId = I.companyId)\n" +
                "        left  join Reviews R on R.optionId = O.optionId)\n" +
                "        left  join ItemInfoPics IIP on I.itemId = IIP.itemId\n" +
                "     )\n" +
                "WHERE\n" +
                "    I.itemId = ?;";
        long        retrieveStoreItemQueryParams = itemId;

        String  retrieveItemImgQuery = "SELECT pictureUrl FROM ItemPictures WHERE itemId = ? ;";
        String  retrieveItemInfoImgQuery = "SELECT itemInfoPicUrl FROM ItemInfoPics WHERE itemId = ?;";

        return  this.jdbcTemplate.queryForObject(
          retrieveStoreItemQuery,
                (rs, rowNum) -> new GetStoreItemRes(
                        rs.getString("itemName"),
                        this.jdbcTemplate.query(retrieveItemImgQuery,
                                (rs2, rowNum2)-> rs2.getString("itemInfoPicUrl")
                                ,retrieveStoreItemQueryParams),
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
                                , retrieveStoreItemQueryParams
                        ),
                        retrieveItemReview(itemId)
                        , rs.getInt("five"),
                        rs.getInt("four"),
                        rs.getInt("three"),
                        rs.getInt("two"),
                        rs.getInt("one"),
                        rs.getInt("inquiry")
                ),
                retrieveStoreItemQueryParams
        );
    }
}