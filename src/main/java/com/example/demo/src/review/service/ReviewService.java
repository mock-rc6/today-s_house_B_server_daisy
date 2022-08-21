package com.example.demo.src.review.service;

import com.example.demo.src.review.dao.ReviewDao;
import com.example.demo.utils.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReviewService {
    private final JwtService        jwtService;
    private final ReviewDao         reviewDao;

}