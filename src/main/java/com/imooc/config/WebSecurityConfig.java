package com.imooc.config;

import com.imooc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.servlet.http.HttpServlet;

/**
 * Created by caoxingyun on 2018/7/18.
 */
@Configuration
@EnableWebSecurity//通过@EnableWebSecurity注解开启Spring Security的功能
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {//继承WebSecurityConfigurerAdapter，并重写它的方法来设置一些web安全的细节
    @Autowired
    private UserRepository userRepository;

    /***
     * 采用注解方式，默认开启csrf
     * @param httpSecurity
     * 对于spring security4.0+无论是xml配置形式还是Java config形式，csrf默认都是开启的
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()//通过authorizeRequests()定义哪些URL需要被保护、哪些不需要被保护。例如以上代码指
                //.antMatchers("/","/css/**","/images/**","/js/**")//过滤静态资源
                .antMatchers("/","/**","/parse")//过滤静态资源
                .permitAll()//定了/和不需要任何认证就可以访问，其他的路径都必须通过身份验证,拦截了其他请求，必须要登录
                .antMatchers("/girls/**").access("hasRole('ROLE_ADMIN')")//必须要有ADMIN角色，才能访问
                .antMatchers("/user/**").access("hasRole('ROLE_USER')")//必须要有USER角色，才能访问
                .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/login")//通过formLogin()定义当需要用户登录时候，转到的登录页面。
                    .defaultSuccessUrl("/index").failureUrl("/login?error")//登录成功是跳转的页面
                    .permitAll()
                    .and()
                .logout().logoutSuccessUrl("/login?logout")
                    .permitAll();
        httpSecurity.headers().frameOptions().sameOrigin();//解决前端使用layui。tab嵌套iframe,在加入security之前并无异常。
        //springmvc 在整合spring security时，浏览器发送请求
        //Refused to display 'url' in a frame because it set 'X-Frame-Options' to 'deny'.
        httpSecurity.sessionManagement().invalidSessionUrl("/login");//ssesion 失效后跳转


        httpSecurity
                .csrf().disable(); //默认开启csrf 可以自己关闭，csrf防止钓鱼网站  开启式 post 请求 会被拦截
        //在某些场合下，我们可能会需要将csrf token值存储在cookie中，此时可以用CookieCsrfTokenRepository来实现这个功能，默认情况下
        // 写入到cookie中的key是XSRF-TOKEN，读取时从request header的X-XSRF-TOKEN中或者parameter的_csrf中读取。
//        httpSecurity
//                .csrf()
//                .csrfTokenRepository(new CookieCsrfTokenRepository());
//    }
}

//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .userDetailsService(new UserDetailsService() {
//                    @Override
//                    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//                        return userRepository.findUserByUsername(username);
//                    }
//                });
//    }

    /**
     * 在内存中设置三个用户
     * @param auth
     * @throws Exception
     */
    //configureGlobal(AuthenticationManagerBuilder auth)方法，在内存中创建了一个用户，该用户的名称为user，密码为password，用户角色为USER。
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("cxy").password("pass").roles("ADMIN");
        auth.inMemoryAuthentication().withUser("lxf").password("pass").roles("USER");
        auth.inMemoryAuthentication().withUser("gl").password("pass").roles("DBA");
    }
}
