package com.example.demo.src.category.controller;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.category.CategoryProvider;
import com.example.demo.src.category.model.GetCategoryRes;
import com.example.demo.utils.ValidationRegex;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/categories")
@AllArgsConstructor
public class CategoryController {
    @Autowired
    private final CategoryProvider      categoryProvider;

    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetCategoryRes>       retrieveCategory(@RequestParam("category-id")String id)  throws BaseException{
        if(id == null){
            return  new BaseResponse<>(BaseResponseStatus.EMPTY_CATEGORY_ID);
        }
        if(!ValidationRegex.canConvertLong(id)){
            return new BaseResponse<>(BaseResponseStatus.INVALID_ID);
        }

        try{
            long            categoryId = Long.parseLong(id);
            GetCategoryRes  getAllCategory = categoryProvider.retrieveCategory(categoryId);
            return  new BaseResponse<GetCategoryRes>(getAllCategory);
        }
        catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }
    }
}