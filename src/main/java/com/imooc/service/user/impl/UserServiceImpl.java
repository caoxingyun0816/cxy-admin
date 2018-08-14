package com.imooc.service.user.impl;

import com.github.pagehelper.PageHelper;
import com.imooc.entity.User;
import com.imooc.repository.UserMapper;
import com.imooc.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by caoxingyun on 2018/7/31.
 */
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> queryUserlistByParams(Map<String, Object> params) {

        //PageHelper是一款好用的开源免费的Mybatis第三方物理分页插件
//        什么时候会导致不安全的分页？
//        PageHelper 方法使用了静态的 ThreadLocal 参数，分页参数和线程是绑定的。
//        只要你可以保证在 PageHelper 方法调用后紧跟 MyBatis 查询方法，这就是安全的。
//          因为 PageHelper 在 finally 代码段中自动清除了 ThreadLocal 存储的对象。
        // PageHelper 物理分页 pageNum是第几页，pageSize是每页多少条,true 表示要统计总数
        PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("rows").toString()),true);
        //返回结果list，已经是Page对象，Page对象是一个ArrayList。
        //使用ThreadLocal来传递和保存Page对象，每次查询，都需要单独设置PageHelper.startPage()方法。
        List<User> userList = userMapper.queryUserlistByParams(params);
        return userList;
    }
}
