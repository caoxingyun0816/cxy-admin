package com.imooc.service.user;

import com.imooc.entity.User;

import java.util.List;
import java.util.Map;

/**
 * Created by caoxingyun on 2018/7/31.
 */
public interface UserService {
    List<User> queryUserlistByParams(Map<String,Object> params);
}
