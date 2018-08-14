package com.imooc;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
//@Configuration
//@EnableSwagger2  //Swagger2 API
//@MapperScan(basePackages = "com.imooc.repository") mybatis注解扫描mapper
@EnableScheduling  //开启定时任务。 会去扫描@Scedule注解的方法，然后执行
// 修改启动类，继承 SpringBootServletInitializer 并重写 configure 方法
public class CxyAdminApplication extends SpringBootServletInitializer{

	private static Logger log = LoggerFactory.getLogger(CxyAdminApplication.class);
//	@Autowired
//	private Environment environment;

	//来指定Spring配置类。WAR部署
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
		return builder.sources(this.getClass());
	}

	public static void main(String[] args) {
		log.info("application start!");
		SpringApplication.run(CxyAdminApplication.class, args);
		log.info("application has started!");
	}

	//声明了这个Bean之后，默
//	认自动配置的DataSourceBean就会忽略。
//	这个@Bean方法最关键的一点是，它还添加了@Profile注解，说明只有在production
//	Profile被激活时才会创建该Bean。
	//自定义的数据源
	//destroy-method="close"的作用是当数据库连接不使用的时候,就把该连接重新放到数据池中,方便下次使用调用.
//	@Bean()
//	@Profile("production")
//	//DataSource的类型是Tomcat的org.apache.tomcat.jdbc.pool.DataSource，不
////	要和javax.sql.DataSource搞混了。前者是后者的实现。
//	public DataSource data	dataSource(){
//		DruidDataSource dataSource = new DruidDataSource();
//		dataSource.setUrl(environment.getProperty("spring.datasource.url"));
//		dataSource.setUsername(environment.getProperty("spring.datasource.username"));//用户名
//		dataSource.setPassword(environment.getProperty("spring.datasource.password"));//密码
//		dataSource.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));
//		dataSource.setInitialSize(2);//初始化时建立物理连接的个数
//		dataSource.setMaxActive(20);//最大连接池数量
//		dataSource.setMinIdle(0);//最小连接池数量
//		dataSource.setMaxWait(60000);//获取连接时最大等待时间，单位毫秒。
//		dataSource.setValidationQuery("SELECT 1");//用来检测连接是否有效的sql
//		dataSource.setTestOnBorrow(false);//申请连接时执行validationQuery检测连接是否有效
//		dataSource.setTestWhileIdle(true);//建议配置为true，不影响性能，并且保证安全性。
//		dataSource.setPoolPreparedStatements(false);//是否缓存preparedStatement，也就是PSCache
//		return dataSource;
//	}

//	@Bean
//	public Docket createApi(){
//		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
//				.apis(RequestHandlerSelectors.basePackage("com.zzp.controller"))
//				.paths(PathSelectors.any())
//				.build();
//	}
//
//	private ApiInfo apiInfo(){
//		return new ApiInfoBuilder()
//				.title("API文档")
//				.description("API使用即参数定义")
//				.termsOfServiceUrl("http://blog.csdn.net/qq_31001665")
//				.contact("ZZP")
//				.version("0.1")
//				.build();
//	}
}
