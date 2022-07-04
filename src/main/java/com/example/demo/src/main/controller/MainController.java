package com.example.demo.src.main.controller;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.main.MainProvider;
import com.example.demo.src.main.model.*;
import com.example.demo.src.review.model.GetMyReviewsRes;
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

    /*
    * [GET] /events
    * 현재 마감기간이 30일 이전이거나
    * 마감된지 30일이 안 된 이벤트들 목록을 불러온다.
    * */
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

    /*
    * [GET] /app/events/:eventId
    * :eventId에 대한 세부 화면이 불러와진다.
    * 오늘의 집에서는 세부 정보들이 이미지로 되어있으므로
    * :eventId에 대한 이벤트 정보 이미지 파일들을 가져온다.
    * */
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

    /*
    * [GET] /app : 메인 화면을 불러오는 컨트롤러
    * 메인 화면에는 이벤트 배너나 카테고리 등이 포함되어 있다.
    * */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetMainRes>     retrieveMain()  throws BaseException{
        try{
            GetMainRes  getMainRes = mainProvider.retrieveMain();
            return  new BaseResponse<GetMainRes>(getMainRes);
        }catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }

    /*
    * [GET] /app/:userId
    * 유저 마이페이지 화면
    * */
    @ResponseBody
    @GetMapping("/{userId}")
    public BaseResponse<GetMyProfileRes>    retrieveUserProfile(@PathVariable("userId") String id) throws BaseException{
        if(!ValidationRegex.canConvertLong(id)){
            return  new BaseResponse<>(BaseResponseStatus.INVALID_ID);
        }
        try{
            long    userId = Long.parseLong(id);
            long    jwtUserId = jwtService.getUserId();

            if(userId != jwtUserId){
                return  new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            GetMyProfileRes getMyProfileRes = mainProvider.retrieveMyProfile(userId);
            return  new BaseResponse<GetMyProfileRes>(getMyProfileRes);
        }catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }

    /*
    * [GET] /app/:userId/my-shoppings
    * 마이페이지 옆 탭의 마이 쇼핑을 호출했을 때
    * */

    @ResponseBody
    @GetMapping("/{userId}/my-shoppings")
    public BaseResponse<GetMyShoppingRes>  retrieveMyShopping(@PathVariable("userId")  String id)  throws BaseException{
        if(!ValidationRegex.canConvertLong(id)){
            return  new BaseResponse<>(BaseResponseStatus.INVALID_ID);
        }

        try{
            long    userId = Long.parseLong(id);
            long    jwtUserId = jwtService.getUserId();

            if(userId != jwtUserId){
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            GetMyShoppingRes getMyShoppingRes = mainProvider.retrieveMyShopping(userId);
            return  new BaseResponse<GetMyShoppingRes>(getMyShoppingRes);
        }
        catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }

    /*
    * [GET] /app/:userId/reviews
    * 해당 유저가 작성한 리뷰들을 모아서 볼 수 있다.
    * 쿼리 파라미터로 필터링을 한다. 이때, 필터링은 필수가 아니다.
    * */
    @ResponseBody
    @GetMapping("/{userId}/reviews")
    public BaseResponse<List<GetMyReviewsRes>>      retrieveMyReviews(@PathVariable("userId") String id,
                                                                      @RequestParam(name = "picture-reviews", defaultValue = "false")
                                                                      String    isPictureReviews,
                                                                      @RequestParam(name = "best-reviews", defaultValue = "true")
                                                                      String    isBestReviews
                                                                      ) throws BaseException{
        if(!ValidationRegex.canConvertLong(id)){
            return  new BaseResponse<>(BaseResponseStatus.INVALID_ID);
        }
        if(!ValidationRegex.isBoolean(isPictureReviews) || !ValidationRegex.isBoolean(isBestReviews)){
            return  new BaseResponse<>(BaseResponseStatus.TYPE_ERROR_NOT_BOOLEAN);
        }
        try{
            long    userId = Long.parseLong(id);
            long    jwtUserId = jwtService.getUserId();

            if(userId != jwtUserId){
                return  new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            boolean pictureReviews = Boolean.parseBoolean(isPictureReviews);
            boolean bestReviews = Boolean.parseBoolean(isBestReviews);

            List<GetMyReviewsRes>   getMyReviewsResList = mainProvider.retrieveMyReviewRes(userId, pictureReviews, bestReviews);

            return  new BaseResponse<List<GetMyReviewsRes>>(getMyReviewsResList);
        }catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/reviews/{userId}")
    public BaseResponse<GetReviewWriteRes>      retrieveReviewWrite(@PathVariable("userId")String id,
                                                                    @RequestParam("id") String item)    throws BaseException{
        if(!ValidationRegex.canConvertLong(id) || !ValidationRegex.canConvertLong(item)){
            return  new BaseResponse<>(BaseResponseStatus.INVALID_ID);
        }

        try{
            long        userId = Long.parseLong(id);
            long        jwtUserId = jwtService.getUserId();

            if(userId != jwtUserId){
                return  new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            long itemId = Long.parseLong(item);

            GetReviewWriteRes   getReviewWriteRes = mainProvider.retrieveReviewWrite(itemId, userId);

            return new BaseResponse<GetReviewWriteRes>(getReviewWriteRes);
        }
        catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }
}