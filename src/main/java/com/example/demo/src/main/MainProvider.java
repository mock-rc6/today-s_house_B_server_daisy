package com.example.demo.src.main;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.main.DAO.MainDao;
import com.example.demo.src.main.model.GetEventsRes;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MainProvider {
    @Autowired
    private final MainDao   mainDao;

    public List<GetEventsRes>   retrieveEvents()    throws BaseException{
        try{
            List<GetEventsRes>  retrieveResult = mainDao.retrieveEvents();
            return retrieveResult;
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

}