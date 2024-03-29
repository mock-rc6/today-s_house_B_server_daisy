package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.category.CategoryProvider;
import com.example.demo.src.store.dao.StoreDao;
import com.example.demo.src.store.model.*;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.model.GetUserKartRes;
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

    @Autowired
    private final UserProvider userProvider;

    @Transactional(readOnly = true)
    public GetStoreRes  retrieveStoreMain() throws BaseException{
        try{
            return  storeDao.retrieveStoreMain();
        }catch (Exception exception){
            throw   new BaseException(BaseResponseStatus.DATABASE_ERROR);
       }
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public int          checkItemId(long    itemId) throws BaseException{
        try{
            return  storeDao.checkItemId(itemId);
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public GetStoreItemRes    retrieveStoreItem(long   itemId, long userId) throws BaseException{
        if(checkItemId(itemId) == 0){
            throw new BaseException(BaseResponseStatus.ITEM_ID_NOT_EXISTS);
        }

        if(userProvider.checkUserId(userId) == 0){
            throw   new BaseException(BaseResponseStatus.USER_NOT_EXISTS);
        }

        //try{
            return storeDao.retrieveStoreItem(itemId, userId);
        //}catch (Exception exception){
        //    throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        //}
    }

    @Transactional(readOnly = true)
    public List<GetItemOptionRes>   retrieveItemOptions(long    itemId) throws BaseException{
        if(checkItemId(itemId) == 0){
            throw   new BaseException(BaseResponseStatus.ITEM_ID_NOT_EXISTS);
        }
        try{
            return  storeDao.retrieveItemOptions(itemId);
        }catch (Exception exception){
            throw   new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public int              checkOptionId(long  optionId)   throws BaseException{
        try{
            return storeDao.checkOptionId(optionId);
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    @Transactional(readOnly = true)
    public int              checkItemOption(long    itemId, long    optionId)   throws BaseException{
        if(checkItemId(itemId) == 0){
            throw new BaseException(BaseResponseStatus.ITEM_ID_NOT_EXISTS);
        }
        if(checkOptionId(optionId) == 0){
            throw new BaseException(BaseResponseStatus.OPTION_ID_NOT_EXISTS);
        }

        System.out.println(storeDao.checkItemOption(itemId, optionId));

        try{
            return storeDao.checkItemOption(itemId, optionId);
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public int      checkKartItem(long userId, long optionId)   throws BaseException{
        try{
            return storeDao.checkKartItem(userId, optionId);
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public List<GetInquiryRes>  retrieveOptionInquiry(long  optionId)   throws BaseException{
        if(checkOptionId(optionId) == 0){
            throw   new BaseException(BaseResponseStatus.OPTION_ID_NOT_EXISTS);
        }

        try{
            return  storeDao.retrieveOptionInquiry(optionId);
        }
        catch (Exception exception){
            throw   new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}