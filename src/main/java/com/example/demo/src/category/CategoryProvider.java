package com.example.demo.src.category;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.category.dao.CategoryDao;
import com.example.demo.src.category.model.GetCategory;
import com.example.demo.src.category.model.GetCategoryDetailRes;
import com.example.demo.src.category.model.GetCategoryRes;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CategoryProvider {
    @Autowired
    private final CategoryDao categoryDao;

    public int      checkCategoryId(long    categoryId) throws BaseException{
        try{
            return  categoryDao.checkCategoryId(categoryId);
        }catch (Exception exception){
            throw   new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int      checkSubCategoryId(long subCategoryId)  throws BaseException{
        try{
            return  categoryDao.checkSubCategoryId(subCategoryId);
        }catch (Exception exception){
            throw   new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int      checkMiniCategoryId(long    miniCategoryId) throws BaseException{
        try{
            return  categoryDao.checkMiniCategoryId(miniCategoryId);
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetCategoryRes   retrieveCategory(long   categoryId) throws BaseException{
        if(checkCategoryId(categoryId) == 0){
            throw   new BaseException(BaseResponseStatus.GET_CATEGORY_NOT_EXISTS);
        }

        try{
            GetCategoryRes  categoryResult = categoryDao.retrieveCategory(categoryId);
            return categoryResult;
        }
        catch (Exception exception){
            throw  new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetCategoryDetailRes     retrieveMiniCategoryDetailRes(long  miniCategoryId) throws BaseException{
        if(checkMiniCategoryId(miniCategoryId) == 0){
            throw new BaseException(BaseResponseStatus.MINI_CATEGORY_NOT_EXISTS);
        }

        try{
            return  categoryDao.retrieveCategoryDetailRes(miniCategoryId);
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}