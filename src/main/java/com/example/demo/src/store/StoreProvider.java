package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.category.CategoryProvider;
import com.example.demo.src.store.dao.StoreDao;
import com.example.demo.src.store.model.GetStoreCategoryRes;
import com.example.demo.src.store.model.GetStoreItemRes;
import com.example.demo.src.store.model.GetStoreRes;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class StoreProvider {
    @Autowired
    private final StoreDao          storeDao;
    @Autowired
    private final CategoryProvider  categoryProvider;
    public GetStoreRes  retrieveStoreMain() throws BaseException{
        try{
            return  storeDao.retrieveStoreMain();
        }catch (Exception exception){
            throw   new BaseException(BaseResponseStatus.DATABASE_ERROR);
       }
    }

    public GetStoreCategoryRes  retrieveStoreCategory(long  categoryId) throws BaseException{
        if(categoryProvider.checkCategoryId(categoryId) == 0){
            throw   new BaseException(BaseResponseStatus.GET_CATEGORY_NOT_EXISTS);
        }

        try{
            return  storeDao.retrieveStoreCategory(categoryId);
        }catch(Exception exception){
            throw   new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int          checkItemId(long    itemId) throws BaseException{
        try{
            return  storeDao.checkItemId(itemId);
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetStoreItemRes    retrieveStoreItem(long   itemId) throws BaseException{
        if(checkItemId(itemId) == 0){
            throw new BaseException(BaseResponseStatus.ITEM_ID_NOT_EXISTS);
        }
        //try{
            return storeDao.retrieveStoreItem(itemId);
        //}
       // catch (Exception exception){
       //     throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
       // }
    }
}