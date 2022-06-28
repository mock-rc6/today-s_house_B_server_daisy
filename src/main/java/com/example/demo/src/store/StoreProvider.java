package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.store.dao.StoreDao;
import com.example.demo.src.store.model.GetStoreRes;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class StoreProvider {
    @Autowired
    private final StoreDao      storeDao;

    public GetStoreRes  retrieveStoreMain() throws BaseException{
        try{
            return  storeDao.retrieveStoreMain();
        }catch (Exception exception){
            throw   new BaseException(BaseResponseStatus.DATABASE_ERROR);
       }
    }
}