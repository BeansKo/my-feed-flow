package com.beans.my.feedflow.job.config;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 在线文档
 * 访问地址 http://localhost:8100/swagger-ui.html
 */
@Configuration
public class Swagger2 {
	@Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .consumes(new HashSet<>(Arrays.asList("application.json")))
                .produces(new HashSet<>(Arrays.asList("application.json")))
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.beans.my.feedflow"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
        		.title("MyFeedFlow")
        		.description("Beans My FeedFlow Doc")
        		.version("1.0")
        		.build();
    }
}
