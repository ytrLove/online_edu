package com.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * MyBatis-Plus配置类
 */
@Configuration
@MapperScan({"com.user.mapper", "com.course.mapper", "com.exam.mapper", "com.comm.mapper"})
public class MybatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 自定义SqlSessionFactory配置
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        
        // 设置Mapper文件位置
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:mapper/**/*.xml"));
        
        // 配置MybatisPlus全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setBanner(false);
        factoryBean.setGlobalConfig(globalConfig);
        
        // 设置插件
        factoryBean.setPlugins(mybatisPlusInterceptor());
        
        // 配置Mybatis，设置实体映射时自动映射（没有的列就不映射）
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        // 设置为PARTIAL，仅映射非嵌套的结果，对于不存在的字段不会报错
        configuration.setAutoMappingBehavior(org.apache.ibatis.session.AutoMappingBehavior.PARTIAL);
        factoryBean.setConfiguration(configuration);
        
        return factoryBean.getObject();
    }
} 