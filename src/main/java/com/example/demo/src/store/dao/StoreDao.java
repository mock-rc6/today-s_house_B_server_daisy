package com.example.demo.src.store.dao;

import com.example.demo.src.category.model.GetCategory;
import com.example.demo.src.category.model.GetCategoryEventsRes;
import com.example.demo.src.store.model.GetStoreCategoryRes;
import com.example.demo.src.store.model.GetStoreMainEvents;
import com.example.demo.src.store.model.GetStoreRes;
import com.example.demo.src.store.model.GetTodaysDealMainRes;
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
                "    concat(IO.saledPrice,'원') as 'price',\n" +
                "    (SELECT COUNT(*) FROM Reviews R WHERE R.optionId = IO.optionId) as 'review num',\n" +
                "    CASE WHEN (SELECT AVG(score)   FROM Reviews R WHERE R.optionId = IO.optionId) is not null\n" +
                "         THEN (SELECT AVG(score)   FROM Reviews R WHERE R.optionId = IO.optionId)\n" +
                "         ELSE '0' end\n" +
                "        as 'score'\n" +
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
                        rs.getInt("review num"),
                        rs.getDouble("score")
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
}