package com.imooc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by caoxingyun on 2018/9/5.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FcqWordTest {

    private static final String[] examples = {"The quick brown fox jumped over the lazy dogs",
            "美国民主党总统候选人希拉利是前总统克林顿的夫人"};
    //private static final Analyzer analyzer = new MIK_CAnalyzer();
    @Test
    public void wordTest(){

    }
}
