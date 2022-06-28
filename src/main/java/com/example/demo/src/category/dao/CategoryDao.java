package com.example.demo.src.category.dao;

import com.example.demo.src.category.model.GetCategory;
import com.example.demo.src.category.model.GetCategoryRes;
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
}