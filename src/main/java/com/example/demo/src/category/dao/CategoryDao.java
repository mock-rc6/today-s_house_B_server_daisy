package com.example.demo.src.category.dao;

import com.example.demo.src.category.model.GetCategoryRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

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

    public GetCategoryRes   retrieveCategory(long   categoryId){
        String              retrieveCategoryQuery = "SELECT name FROM SubCategories\n" +
                "WHERE categoryId = ?;";
        long                retrieveCategoryQueryParams = categoryId;
        String              subqueryForRetrieveCategoriesQuery = "SELECT name FROM Categories;";
        String              subqueryForRetrieveCategoryIdQuery = "SELECT categoryId FROM Categories;";
        String              retrieveCategoryIdQuery = "SELECT subCategoryId FROM SubCategories\n" +
                "WHERE categoryId = ?;";

        return  new GetCategoryRes(
                categoryId,
                this.jdbcTemplate.query(subqueryForRetrieveCategoriesQuery, (rs, rowNum)-> rs.getString("name")),
                this.jdbcTemplate.query(subqueryForRetrieveCategoryIdQuery, (rs, rowNum)-> rs.getLong("categoryId")),
                this.jdbcTemplate.query(retrieveCategoryQuery, (rs, rowNum)->rs.getString("name"), retrieveCategoryQueryParams),
                this.jdbcTemplate.query(retrieveCategoryIdQuery, (rs, rowNum)->rs.getLong("subCategoryId"), retrieveCategoryQueryParams)
        );
    }
}