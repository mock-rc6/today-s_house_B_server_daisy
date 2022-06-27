package com.example.demo.src.main.controller;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.main.MainProvider;
import com.example.demo.src.main.model.GetEventsRes;
import com.example.demo.utils.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
}