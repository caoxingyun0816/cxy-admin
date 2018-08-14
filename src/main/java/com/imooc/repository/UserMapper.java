package com.imooc.repository;

import com.imooc.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by caoxingyun on 2018/7/30.
 * springboot 集成 mybatis  注解方式 非注解方式
 * Mybatis注解的方式好简单，只要定义一个dao接口，然后sql语句通过注解写在接口方法上。
 * 最后给这个接口添加@Mapper注解或者在启动类上添加@MapperScan(“com.dudu.dao”)注解都行。
 */
@Component
@Mapper
public interface UserMapper {
//    映射器是一个你创建来绑定你映射的语句的接口。映射器接口的实例是从 SqlSession 中获得的。因此从技术层面讲，任何映射器实例的最大作用域是和请求它们的 SqlSession 相同的。尽管如此，映射器实例的最佳作用域是方法作用域。也就是说，映射器实例应该在调用它们的方法中被请求，用过之后即可废弃。并不需要显式地关闭映射器实例，尽管在整个请求作用域（request scope）保持映射器实例也不会有什么问题，但是很快你会发现，像 SqlSession 一样，在这个作用域上管理太多的资源的话会难于控制。所以要保持简单，最好把映射器放在方法作用域（method scope）内。下面的示例就展示了这个实践：
//
//    SqlSession session = sqlSessionFactory.openSession();
//try {
//        UserMapper mapper = session.getMapper(UserMapper.class);
//        // do work
//    } finally {
//        session.close();
//    }
//    @Update("update user set username=#{username} where id = #{id}")
    int update(User user);

    @Insert("insert into user(username,password,age,role_id) values(#{username},#{password},#{age},#{roleId})")
    int add(User user);

    @DeleteProvider(type = UserSqlBuilder.class,method = "deleteById")
    int deleteByIds(@Param("ids") String[] ids);

    @Select("select * from user where id = #{id}")
    @Results(id = "userMap", value = {//相当于xml中定义的returnmap
            @Result(column = "id", property = "id", javaType = Integer.class),
            @Result(property = "username", column = "username", javaType = String.class),
            @Result(property = "password", column = "password", javaType = String.class),
            @Result(property = "age", column = "age", javaType = Integer.class),
            @Result(property = "roleId", column = "role_id", javaType = Integer.class)
    })
    User queryUserById(@Param("id") Integer id);

    @SelectProvider(type = UserSqlBuilder.class,method = "queryUserlistByParams")
    List<User> queryUserlistByParams(Map<String,Object> params);

//    注意，使用#符号和$符号的不同：

//    // This example creates a prepared statement, something like select * from teacher where name = ?;
//    @Select("Select * from user where name = #{name}")
//    User selectTeachForGivenName(@Param("name") String name);
//
//    // This example creates n inlined statement, something like select * from teacher where name = 'someName';
//    @Select("Select * from user where name = '${name}'")
//    User selectTeachForGivenName(@Param("name") String name);
//
// 简单的语句只需要使用@Insert、@Update、@Delete、@Select这4个注解即可，但是有些复杂点需要动态SQL语句，
// 就比如上面方法中根据查询条件是否有值来动态添加sql的，就需要使用@InsertProvider、@UpdateProvider、
// @DeleteProvider、@SelectProvider等注解。
//  这些可选的 SQL 注解允许你指定一个类名和一个方法在执行时来返回运行 允许创建动态 的 SQL。
// 基于执行的映射语句, MyBatis 会实例化这个类,然后执行由 provider 指定的方法.
// 该方法可以有选择地接受参数对象.(In MyBatis 3.4 or later, it’s allow multiple parameters)
// 属性: type,method。type 属性是类。method 属性是方法名。

    class  UserSqlBuilder{
        public String deleteById(@Param("ids") final String [] ids){
            StringBuffer sql =new StringBuffer();
            sql.append("DELETE FROM learn_resource WHERE id in(");
            for (int i=0;i<ids.length;i++){
                if(i==ids.length-1){
                    sql.append(ids[i]);
                }else{
                    sql.append(ids[i]).append(",");
                }
            }
            sql.append(")");
            return sql.toString();
        }

        public String queryUserlistByParams(final Map<String,Object> params){
            StringBuffer sql = new StringBuffer();
            sql.append("select * from user where 1= 1");
            if((String)params.get("username") != null){
                sql.append("and username like '% ").append((String) params.get("username")).append("%'");
            }
            return sql.toString();
        }
    }
}
