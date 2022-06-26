package com.example.demo.src.store.dao;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class StoreDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private void    setDataSource(DataSource dataSource){this.jdbcTemplate = new JdbcTemplate(dataSource);}

}