package com.example.demo.src.main.controller;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.main.MainProvider;
import com.example.demo.src.main.model.GetEventDetailRes;
import com.example.demo.src.main.model.GetEventsRes;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.ValidationRegex;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app")
@AllArgsConstructor
public class MainController {
    @Autowired
    private final MainProvider  mainProvider;
    @Autowired
    private final JwtService    jwtService;

    @ResponseBody
    @GetMapping("/events")
    public BaseResponse<List<GetEventsRes>> retrieveEvents()    throws BaseException{
        try{
            List<GetEventsRes>  events = mainProvider.retrieveEvents();
            return new BaseResponse<List<GetEventsRes>>(events);
        }catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/events/{eventId}")
    public BaseResponse<GetEventDetailRes>  retrieveEventDetails(@PathVariable("eventId")   String id)  throws BaseException{
        if(id == null){
            return  new BaseResponse<>(BaseResponseStatus.EMPTY_PATH_VARIABLE);
        }
        if(!ValidationRegex.canConvertLong(id)){
            return  new BaseResponse<>(BaseResponseStatus.INVALID_ID);
        }

        try{
            long                eventId = Long.parseLong(id);
            GetEventDetailRes   getEventDetailRes = mainProvider.retrieveEventDetails(eventId);
            return  new BaseResponse<>(getEventDetailRes);
        }catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }
}