package com.imooc.repository;

import com.imooc.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.security.PublicKey;

/**
 * Created by caoxingyun on 2018/7/18.
 * spring data jpa
 */
public interface UserRepository extends JpaRepository<User,Integer>{
    public User findUserByUsername(String username);
}
