package com.open.capacity.datasource;


import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.open.capacity.datasource.properties.DbProperties;
import com.open.capacity.datasource.util.DynamicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


/**
 * 多数据源配置
 * 在设置了spring.datasource.enable.dynamic 等于true是开启多数据源，配合日志
 * {@code @Import} 指定加载相应的类
 * @author stars
 */
@Slf4j
@Configuration
@AutoConfigureBefore(value={DruidDataSourceAutoConfigure.class, MybatisPlusAutoConfiguration.class})
@ConditionalOnProperty(name = {"spring.datasource.dynamic.enable"}, matchIfMissing = false, havingValue = "true")
public class DataSourceAutoConfig {

    @Autowired
    private DbProperties dbProperties;

    /**
     * 创建数据源
     * @return
     */
	@Bean
    @Primary
	public DataSource dataSourceCore(){
        DataSource dataSource = null;
        try {
            dataSource = DruidDataSourceFactory.createDataSource(dbProperties.getProperties());
        } catch (Exception e) {
            log.error("Create DataSource Error : {}", e);
            throw new RuntimeException();
        }
        return dataSource;
	}

    /**
     * 注册动态数据源
     * <p>将所有的数据源注入 DataSource 中</p>
     * {@code @Primary}注入多个相同的实例时，选中这个注解表示的这个实例。
     * {@code @Qualifier} 如果存在多个相同bean实例，配合使用
     * 只需要纳入动态数据源到spring容器
     * @return
     */
	@Primary
    @Bean
    public DataSource dataSource() {
        DynamicDataSource dataSource = new DynamicDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>(2);
        dataSourceMap.put("dynamic_db", dataSourceCore());
        // 设置默认数据源
        dataSource.setDefaultTargetDataSource(dataSourceCore());
        dataSource.setTargetDataSources(dataSourceMap);
        return dataSource;
    }

   /**
     * Sql会话工厂bean。
     * 这里配置SqlSessionFactory的数据源
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "mybatis")
    public SqlSessionFactoryBean sqlSessionFactoryBean() {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        // 必须将动态数据源添加到 sqlSessionFactoryBean
        sqlSessionFactoryBean.setDataSource(dataSource());
        return sqlSessionFactoryBean;
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
