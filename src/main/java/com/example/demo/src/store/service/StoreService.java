package com.example.demo.src.store.service;

import com.example.demo.src.store.StoreProvider;
import com.example.demo.src.store.dao.StoreDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StoreService {
    private final StoreDao      storeDao;
    private final StoreProvider storeProvider;


}