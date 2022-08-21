package com.example.demo.src.review.controller;

import com.example.demo.src.review.ReviewProvider;
import com.example.demo.utils.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {
    @Autowired
    private final JwtService        jwtService;
    @Autowired
    private final ReviewProvider    reviewProvider;

}