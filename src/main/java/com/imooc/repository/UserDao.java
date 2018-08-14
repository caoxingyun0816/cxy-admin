package com.imooc.repository;

import com.imooc.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by caoxingyun on 2018/7/25.
 * jdbctemplate
 */
public interface UserDao {
    public Integer getAllUsers();
}
