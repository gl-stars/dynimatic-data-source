package com.open.capacity.datasource;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.open.capacity.datasource.aop.DataSourceAOP;
import com.open.capacity.datasource.constant.DataSourceKey;
import com.open.capacity.datasource.util.DynamicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


/**
 * 在设置了spring.datasource.enable.dynamic 等于true是开启多数据源，配合日志
 * {@code @Import} 指定加载相应的类
 * @author stars
 */
@Configuration
@Import(DataSourceAOP.class)
@AutoConfigureBefore(value={DruidDataSourceAutoConfigure.class,MybatisPlusAutoConfiguration.class})
@ConditionalOnProperty(name = {"spring.datasource.dynamic.enable"}, matchIfMissing = false, havingValue = "true")
public class DataSourceAutoConfig {


    /**
     * 创建数据源
     * @return
     */
	@Bean
	@ConfigurationProperties("spring.datasource.druid.core")
	public DataSource dataSourceCore(){
	    return DruidDataSourceBuilder.create().build();
	}

    /**
     * 创建数据源
     * @return
     */
	@Bean
	@ConfigurationProperties("spring.datasource.druid.log")
	public DataSource dataSourceLog(){
	    return DruidDataSourceBuilder.create().build();
	}

    /**
     * 将所有的数据源注入 DataSource 中
     * {@code @Primary}注入多个相同的实例时，选中这个注解表示的这个实例。
     * {@code @Qualifier} 如果存在多个相同bean实例，配合使用
     * 只需要纳入动态数据源到spring容器
     * @return
     */
	@Primary
    @Bean
    public DataSource dataSource() {
        DynamicDataSource dataSource = new DynamicDataSource();
        DataSource coreDataSource =  dataSourceCore() ;
        DataSource logDataSource =  dataSourceLog();
        dataSource.addDataSource(DataSourceKey.core, coreDataSource);
        dataSource.addDataSource(DataSourceKey.log, logDataSource);
        // 将 coreDataSource 数据源设置为默认数据源
        dataSource.setDefaultTargetDataSource(coreDataSource);
        return dataSource;
    }

    /**
     * 将数据源纳入spring事物管理
     * <p>{@code @Qualifier}出现多个实例，选择性使用。</p>
     * @param dataSource
     * @return
     */
    @Bean
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource")  DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
