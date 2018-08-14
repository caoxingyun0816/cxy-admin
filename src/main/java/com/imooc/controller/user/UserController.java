package com.imooc.controller.user;

import com.github.pagehelper.PageInfo;
import com.imooc.entity.User;
import com.imooc.service.user.UserService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caoxingyun on 2018/7/23.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/allUsers")
    public String allUsersView(){
        return "/user/allUsers";
    }

    @GetMapping("/addUser")
    public String addUserView(){
        return "/user/addUser";
    }

    @RequestMapping(value = "/queryUserList",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public Map queryLearnList(HttpServletRequest request , HttpServletResponse response){
        String page = request.getParameter("page"); // 取得当前页数,注意这是jqgrid自身的参数
        String rows = request.getParameter("rows"); // 取得每页显示行数，,注意这是jqgrid自身的参数
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("page", page);
        params.put("rows", rows);
        params.put("username", username);
        params.put("password", password);
        List<User> userList = userService.queryUserlistByParams(params);
        PageInfo<User> pageInfo =new PageInfo<User>(userList);
        Map map = new HashMap<String,Object>();
        map.put("rows", pageInfo);
        map.put("total", pageInfo.getPages());//总页数
        map.put("records",pageInfo.getTotal());//查询出的总记录数
        return map;
    }

}
