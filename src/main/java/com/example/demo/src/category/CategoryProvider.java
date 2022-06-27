package com.example.demo.src.category;

import com.example.demo.src.category.dao.CategoryDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryProvider {
    private final CategoryDao categoryDao;
}