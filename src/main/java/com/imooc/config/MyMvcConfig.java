package com.imooc.config;

import com.imooc.Intercepter.DemoInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * @Bean
 *  public InternalResourceViewResolver viewResolver();
 * 注入InternalResourceViewResolver类：
 * Created by caoxingyun on 2018/7/20.
 * * 说明：springmvc下有一个接口叫ViewResolver，(我们的viewResolver都实现该接口)，实现这个接口要重写
 * resolverName(),这个方法的返回值接口View，而view的职责就是使用model、request、response对象，并
 * 渲染视图(不一定是html、可能是json、xml、pdf等)给浏览器 。
 *
 */
@Configuration //如果加其他注解，可能会影响静态资源路径
public class MyMvcConfig  extends WebMvcConfigurerAdapter{
    //viewResolver
    //设置页面的前缀后缀
//    @Bean
//    public InternalResourceViewResolver viewResolver() {
//        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//        //viewResolver.setPrefix("/WEB-INF/classes/views/");//打war后默认编译的路径
//        viewResolver.setPrefix("/WEB-INF/views/");//使用tomcat7:run插件后要放的位置
//        viewResolver.setSuffix(".jsp");
//        viewResolver.setViewClass(JstlView.class);
//        return viewResolver;
//    }
//
//    //静态资源映射
//    //addResourceLocations指的是文件放置的目录，addResoureHandler指的是对外暴露的访问路径
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css");
//        registry.addResourceHandler("/image/**").addResourceLocations("classpath:/static/image");
//    }
//     自定义拦截器
     //拦截器addInterceptors
//    @Bean
//    public DemoInterceptor demoInterceptor() {
//
//        return new DemoInterceptor();
//    }
//
    //addPathPatterns 用于添加拦截规则
    //// excludePathPatterns 用户排除拦截
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {// 2
//        registry.addInterceptor(new demoInterceptor()).addPathPatterns("/**").excludePathPatterns("/login");
//        super.addInterceptors(registry);
//    }

    /***
     * 统一管理controller直接跳转到对应页面，不用直接写对应的controller
     * 页面跳转addViewControllers
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/systemParameter/systemParameter").setViewName("/systemParameter/systemParameter");
        registry.addViewController("/links/linksList").setViewName("/links/linksList");
        registry.addViewController("/news/newsList").setViewName("/news/newsList");
        registry.addViewController("/news/newsAdd").setViewName("/news/newsAdd");
    }

}
