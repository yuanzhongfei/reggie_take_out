package com.itheima.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.itheima.reggie.common.JacksonObjectMapper;
import com.itheima.reggie.interception.LoginInterception;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }
    public void addInterceptors(InterceptorRegistry registry) {
        //1.创捷拦截器对象
        LoginInterception loginInterception = new LoginInterception();
        //2.定义一个集合,用来存储将不进行过滤的资源
        List<String> list = new ArrayList<String>();
        list.add("/employee/login");
        list.add("/employee/logout");
        list.add("/backend/**");
        list.add("/front/**" );
        registry.addInterceptor(loginInterception).addPathPatterns("/**").excludePathPatterns(list);
    }
    @Bean
    public MybatisPlusInterceptor interceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;

    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //添加消息转换类
        converter.setObjectMapper(new JacksonObjectMapper());
        //添加索引
        converters.add(0,converter);
    }
}
