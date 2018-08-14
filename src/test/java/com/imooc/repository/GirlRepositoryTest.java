package com.imooc.repository;

import com.imooc.domain.Girl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by caoxingyun on 2018/8/3.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GirlRepositoryTest {

    private Logger logger = LoggerFactory.getLogger(GirlRepositoryTest.class);

    @Value("${didspance.blog.randomUUid}")
    private String value;

    @Autowired
    private GirlRepository girlRepository;

    @Test
    public void findAllTest(){
//        int page = 1; //从第0页开始的。1就是第二页
//        int size = 5;
//        Sort sort = new Sort(Sort.Direction.ASC,"id");
//        Pageable pageable = new PageRequest(page,size,sort);
//        logger.info("list-------------------------");
//        int a = girlRepository.modify(28,5);
//        logger.info("ais" + a);
//        Page<Girl> list = girlRepository.findAll(pageable);
//        list.getTotalPages();//总页数
//        list.getTotalElements();
//        List<Girl> girls = list.getContent(); //返回的数据
//        logger.info("list"+list);
        logger.info("value is :"+ value);
    }

}