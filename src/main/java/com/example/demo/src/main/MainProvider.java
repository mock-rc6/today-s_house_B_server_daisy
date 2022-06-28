package com.example.demo.src.main;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.main.DAO.MainDao;
import com.example.demo.src.main.model.GetEventDetailRes;
import com.example.demo.src.main.model.GetEventsRes;
import com.example.demo.src.main.model.GetMainRes;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
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

    public int      checkEventId(long   eventId)    throws BaseException{
        try{
            return mainDao.checkEventId(eventId);
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetEventDetailRes    retrieveEventDetails(long   eventId)    throws BaseException{
        if(checkEventId(eventId) == 0){
            throw   new BaseException(BaseResponseStatus.EVENT_NOT_EXISTS);
        }

        try{
            GetEventDetailRes   getEventDetailRes = mainDao.retrieveEventDetails(eventId);
            return  getEventDetailRes;
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetMainRes       retrieveMain()  throws BaseException{
        try{
            GetMainRes  getMainRes = mainDao.retrieveMain();
            return      getMainRes;
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}