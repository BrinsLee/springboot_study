package com.brins.config;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by lipeilin on 2024/10/6.
 */
// 当前类是自动配置类
@AutoConfiguration
public class MyBatisAutoConfig {

    // 注入SqlSessionFactoryBean
    @Bean
    public SqlSessionFactoryBean sessionFactory(DataSource dataSource) {
        // 数据库会话对象
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        return sqlSessionFactoryBean;

    }


    // 注入MapperScannerConfigure
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(BeanFactory beanFactory) {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        // 设置扫描的包 启动类所在的包及其子包
        List<String> packages = AutoConfigurationPackages.get(beanFactory);
        String name = packages.get(0);
        mapperScannerConfigurer.setBasePackage(name);

        // 设置扫描的注解
        mapperScannerConfigurer.setAnnotationClass(Mapper.class);
        return mapperScannerConfigurer;
    }
}
