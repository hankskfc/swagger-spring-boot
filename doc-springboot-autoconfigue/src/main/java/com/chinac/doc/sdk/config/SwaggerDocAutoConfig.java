package com.chinac.doc.sdk.config;


import com.chinac.doc.sdk.core.IPHelper;
import com.chinac.doc.sdk.core.ServiceProperties;
import com.chinac.doc.sdk.core.SwaggerDocProperties;
import com.chinac.doc.sdk.core.SwaggerJsonBootstrap;
import com.chinac.doc.sdk.core.SwaggerJsonHandler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

@EnableSwagger2
@EnableWebMvc
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({SwaggerDocProperties.class, ServiceProperties.class})
public class SwaggerDocAutoConfig extends WebMvcConfigurerAdapter {

    private final SwaggerDocProperties swaggerDocProperties;
    private final ServiceProperties serviceProperties;

    public SwaggerDocAutoConfig(SwaggerDocProperties swaggerDocProperties, ServiceProperties serviceProperties) {

        this.swaggerDocProperties = swaggerDocProperties;
        this.serviceProperties = serviceProperties;
    }

    @Value("${server.port}")
    private int port;

    private ApiInfo initApiInfo() {
        ApiInfo apiInfo = new ApiInfo(serviceProperties.getName(), // 大标题
                initContextInfo(), // 简单的描述
                serviceProperties.getVersion(), // 版本
                "服务条款", swaggerDocProperties.getAuthor(), // 作者
                "The Apache License, Version 2.0", // 链接显示文字
                "http://hermes-ui.service.huayun.com"// 网站链接
        );
        return apiInfo;
    }

    private String initContextInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append(serviceProperties.getName() + " API").append("<br/>")
                .append(swaggerDocProperties.getDescription())
                .append("<br/>")
                .append("以下是本项目的API文档");

        return sb.toString();
    }

    @Bean
    public Docket restfulApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(initApiInfo())
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerDocProperties.getBasePackage()))
                .build();

    }

    @Bean
    @ConditionalOnBean(value = {DocumentationCache.class, ServiceModelToSwagger2Mapper.class, JsonSerializer.class})
    public SwaggerJsonHandler getSwaggerJsonHandler() {
        System.out.println("");
        System.out.println("config info---------->");
        System.out.println(String.format("serviceName:%s ", serviceProperties.getName()));
        System.out.println(String.format("serviceVersion:%s", serviceProperties.getVersion()));
        System.out.println(String.format("scan basePackage:%s", swaggerDocProperties.getBasePackage()));
        System.out.println(String.format("service desc:%s", swaggerDocProperties.getDescription()));
        System.out.println(String.format("service author:%s ", swaggerDocProperties.getAuthor()));
        System.out.println(String.format("serviceMetaCenterUrl:%s ", serviceProperties.getMetaUrl()));
        System.out.println(String.format("RemoteEnable:%s ", swaggerDocProperties.isRemoteEnable()));
        System.out.println("");

        Assert.isTrue(!StringUtils.isEmpty(serviceProperties.getName()), String.format("serviceName can't empty"));
        Assert.isTrue(!StringUtils.isEmpty(serviceProperties.getVersion()), String.format("serviceversion can't empty"));
        Assert.isTrue(!StringUtils.isEmpty(swaggerDocProperties.getBasePackage()), String.format("basePackage can't empty"));
        Assert.isTrue(!StringUtils.isEmpty(serviceProperties.getMetaUrl()), String.format("serviceMetaCenterUrl can't empty"));

        return new SwaggerJsonHandler(serviceProperties.getName(),
                serviceProperties.getVersion(),
                serviceProperties.getMetaUrl(),
                IPHelper.getLocalIP(),
                port,
                swaggerDocProperties.isRemoteEnable());
    }

    @Bean
    public SwaggerJsonBootstrap getSwaggerJsonBootstrap() {
        return new SwaggerJsonBootstrap();
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        if (swaggerDocProperties.isSelfhostEnable()) {
            String ui = "swagger-ui.html";
//            registry.addResourceHandler("swagger-ui.html")
//                    .addResourceLocations("classpath:/META-INF/resources/");

            registry.addResourceHandler("/**").addResourceLocations("/");
            registry.addResourceHandler(ui)
                    .addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");

            System.out.println(String.format("现在可以访问api的信息，http://%s:%d/%s", IPHelper.getLocalIP(), port, ui));
        }

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .maxAge(3600);
    }
}
