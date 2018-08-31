package com.imooc;

import com.imooc.domain.Girl;
import com.imooc.service.girl.GirlService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by caoxingyun
 * 2018-07-14 23:20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GirlServiceTest {

    @Autowired
    private GirlService girlService;

    @Test
    public void findOneTest() {
        Integer a = null;
        if(a !=1 ){
            System.out.println(a);
        }
    }
}
