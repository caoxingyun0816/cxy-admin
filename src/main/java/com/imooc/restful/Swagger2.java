package com.imooc.restful;

import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by caoxingyun on 2018/7/17.
 */
@Configuration
@EnableSwagger2 //允许Swagger2 api
public class Swagger2 {

    public Docket createRestFulApi(){
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("girl 项目接口文档")
                .description("RESTful API 风格")
                .version("6.6.6")
                .build();
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.imooc.controller"))
                .paths(PathSelectors.any())
                .build();
        //其中 .apis(RequestHandlerSelectors.basePackage("com.imooc.controller"))
        // 指定了以扫描包的方式进行，会把com.imooc.controller包下的controller都扫描到。
    }
}
