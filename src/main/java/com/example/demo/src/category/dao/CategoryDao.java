package com.example.demo.src.category.dao;

import com.example.demo.src.category.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CategoryDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){this.jdbcTemplate = new JdbcTemplate(dataSource);}

    public int      checkCategoryId(long    categoryId){
        String      checkCategoryIdQuery = "SELECT  EXISTS(SELECT categoryId FROM Categories WHERE categoryId = ?);";
        long        checkCategoryIdParams = categoryId;

        return  this.jdbcTemplate.queryForObject(checkCategoryIdQuery, int.class, checkCategoryIdParams);
    }

    public int      checkSubCategoryId(long subCategoryId){
        String      checkSubCategoryIdQuery = "SELECT  EXISTS(SELECT subCategoryId FROM SubCategories WHERE subCategoryId = ?);";
        long        checkSubCategoryIdParams = subCategoryId;

        return  this.jdbcTemplate.queryForObject(checkSubCategoryIdQuery, int.class, checkSubCategoryIdParams);
    }

    public int      checkMiniCategoryId (long   miniCategoryId){
        String      checkMiniCategoryQuery = "SELECT  EXISTS(SELECT miniCategoryId FROM MiniCategories WHERE miniCategoryId = ?);";
        long        checkMiniCategoryParams = miniCategoryId;

        return  this.jdbcTemplate.queryForObject(checkMiniCategoryQuery, int.class, checkMiniCategoryParams);
    }

    public List<GetCategory> retrieveMainCategory(){
        String              retrieveMainCategoryQuery = "SELECT name, categoryId FROM Categories;";

        return this.jdbcTemplate.query(
                retrieveMainCategoryQuery,
                (rs, rowNum) -> new GetCategory(rs.getString("name"), rs.getLong("categoryId"))
        );
    }

    public List<GetCategory>    retrieveSubCategory(){
        String  retrieveSubCategoryQuery = "SELECT subCategoryId, name FROM SubCategories;";

        return this.jdbcTemplate.query(retrieveSubCategoryQuery,
                (rs, rowNum) -> new GetCategory(
                        rs.getString("name"), rs.getLong("subCategoryId")
                ));
    }
    public GetCategoryRes   retrieveCategory(long   categoryId){
        return  new GetCategoryRes(
                categoryId,
                retrieveMainCategory(),
                retrieveSubCategory()
        );
    }

    private List<GetCategory>    retrieveSmallestCategory(long   miniCategoryId){
        String                  retrieveSmallestCategoryQuery
                                = "SELECT smallestCategoryId, description\n" +
                                "FROM    SmallestCategories\n" +
                                "WHERE   miniCategoryId = ?;";
        long                    retrieveSmallestCategoryQueryParams = miniCategoryId;

        return  this.jdbcTemplate.query(
                retrieveSmallestCategoryQuery,
                (rs, rowNum) -> new GetCategory(rs.getString("description"),
                        rs.getLong("smallestCategoryId")),
                retrieveSmallestCategoryQueryParams
        );
    }

    private List<GetCategoryItemRes> retrieveMDPickItems(long    miniCategoryId){
        String                      retrieveMDPickItemsQuery = "SELECT\n" +
                "    I.itemId                as 'itemId',\n" +
                "    I.companyId             as 'companyId',\n" +
                "    I.itemName              as 'itemName',\n" +
                "    C.name                  as 'companyName',\n" +
                "    concat(round(IO.saledPrice*100/IO.price,1),'%') as 'saleRate',\n" +
                "    concat(IO.saledPrice,'원')   as 'price',\n" +
                "    (SELECT COUNT(*) FROM Reviews R WHERE R.optionId = IO.optionId) as 'review cnt',\n" +
                "    case when (SELECT AVG(score) FROM Reviews R WHERE R.optionId = IO.optionId) is not null\n" +
                "         then (SELECT AVG(score) FROM Reviews R WHERE R.optionId = IO.optionId)\n" +
                "         else 0 end\n" +
                "    as 'score'\n" +
                "FROM ((((Items I inner join MiniCategories MC on I.miniCategoryId = MC.miniCategoryId)\n" +
                "      inner join Companies C on I.companyId = C.companyId))\n" +
                "      inner join ItemOptions IO on IO.itemId = I.itemId)\n" +
                "WHERE I.isMDPick = 1 AND I.miniCategoryId = ?;";
        long                        retrieveMDPickItemsQueryParams = miniCategoryId;

        return  this.jdbcTemplate.query(retrieveMDPickItemsQuery,
                (rs, rowNum) -> new GetCategoryItemRes(
                        rs.getLong("itemId"),
                        rs.getLong("companyId"),
                        rs.getString("companyName"),
                        rs.getString("itemName"),
                        rs.getString("saleRate"),
                        rs.getString("price"),
                        rs.getInt("review cnt"),
                        rs.getDouble("score")
                )
                ,retrieveMDPickItemsQueryParams);
    }

    private List<GetCategoryItemRes> retrieveMiniCategoryItems(long    miniCategoryId){
        String                      retrieveMDPickItemsQuery = "SELECT\n" +
                "    I.itemId                as 'itemId',\n" +
                "    I.companyId             as 'companyId',\n" +
                "    I.itemName              as 'itemName',\n" +
                "    C.name                  as 'companyName',\n" +
                "    concat(round(IO.saledPrice*100/IO.price,1),'%') as 'saleRate',\n" +
                "    concat(IO.saledPrice,'원')   as 'price',\n" +
                "    (SELECT COUNT(*) FROM Reviews R WHERE R.optionId = IO.optionId) as 'review cnt',\n" +
                "    case when (SELECT AVG(score) FROM Reviews R WHERE R.optionId = IO.optionId) is not null\n" +
                "         then (SELECT AVG(score) FROM Reviews R WHERE R.optionId = IO.optionId)\n" +
                "         else 0 end\n" +
                "    as 'score'\n" +
                "FROM ((((Items I inner join MiniCategories MC on I.miniCategoryId = MC.miniCategoryId)\n" +
                "      inner join Companies C on I.companyId = C.companyId))\n" +
                "      inner join ItemOptions IO on IO.itemId = I.itemId)\n" +
                "WHERE I.isMDPick = 0 AND I.miniCategoryId = ?;";
        long                        retrieveMDPickItemsQueryParams = miniCategoryId;

        return  this.jdbcTemplate.query(retrieveMDPickItemsQuery,
                (rs, rowNum) -> new GetCategoryItemRes(
                        rs.getLong("itemId"),
                        rs.getLong("companyId"),
                        rs.getString("companyName"),
                        rs.getString("itemName"),
                        rs.getString("saleRate"),
                        rs.getString("price"),
                        rs.getInt("review cnt"),
                        rs.getDouble("score")
                )
                ,retrieveMDPickItemsQueryParams);
    }

    private List<GetCategoryEventsRes>   retrieveMiniCategoryEventList(long  miniCategoryId){
        String          retrieveMiniCategoryEventListQuery = "SELECT\n" +
                "    miniCategoryEventId,\n" +
                "    miniCategoryEventImgUrl\n" +
                "FROM MiniCategoryEvents\n" +
                "WHERE miniCategoryId= ? AND\n" +
                "      TIMESTAMPDIFF(SECOND, CURRENT_TIMESTAMP, due)>0;";
        long            retrieveMiniCategoryEventListQueryParams = miniCategoryId;

        return  this.jdbcTemplate.query(retrieveMiniCategoryEventListQuery,
                (rs, rowNum)-> new GetCategoryEventsRes(rs.getLong("miniCategoryEventId"),
                        rs.getString("miniCategoryEventImgUrl")),
                retrieveMiniCategoryEventListQueryParams);
    }

    public GetCategoryDetailRes     retrieveCategoryDetailRes(long  miniCategoryId){
        return  new GetCategoryDetailRes(
                miniCategoryId,
                retrieveSmallestCategory(miniCategoryId),
                retrieveMiniCategoryEventList(miniCategoryId),
                retrieveMDPickItems(miniCategoryId),
                retrieveMiniCategoryItems(miniCategoryId)
        );
    }
}