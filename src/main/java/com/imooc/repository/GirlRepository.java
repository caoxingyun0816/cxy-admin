package com.imooc.repository;

import com.imooc.domain.Girl;
import com.imooc.entity.User;
import io.swagger.models.auth.In;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

/**
 * Created by caoxingyun 接口前可以不加@conpoent注解
 * 2018-07-18 23:17
 */
public interface GirlRepository extends JpaRepository<Girl, Integer> {

    /***
     * 查询分页数据
     * @param pageable //分页查询条件
     * @return
     */
    Page<Girl> findAll(Pageable pageable);

    //通过年龄来查询
    public List<Girl> findByAge(Integer age);

//    /***
//     * 注解直接写SQL
//     * @param age
//     * @param id
//     * @return
//     */
//    @Modifying
//    @Query("update girl set age = ?1 where id = ?2")
//    int modifyByAgeAndId(Integer age, Integer id);
//
//    @Transactional
//    @Modifying
//    @Query("delete from User where id = ?1")
//    void deleteByUserId(Long id);
//
//    @Transactional(timeout = 10)
//    @Query("select u from User u where u.emailAddress = ?1")
//    User findByAge(Integer age);

}
