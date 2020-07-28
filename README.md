# 多数据源文档

# 一、分支说明

| 分支 | 说明                 |
| ---- | -------------------- |
| v1   | 实现最简洁的多数据源 |
| v2   | 添加注解切换数据源   |
|      |                      |



# 二、v1 功能说明

在最近的项目业务中，需要在程序的运行过程中，添加新的数据库添链接进来，然后从新数据库链接中读取数据。网上查阅了资料，发现`spring`为多数据源提供了一个抽象类`AbstractRoutingDataSource`，该类中只有一个抽象方法`determineCurrentLookupKey`()需要由我们实现。

以下是使用方法

假设我们创建一个类`DynimaticDataSource`，继承`AbstractRoutingDataSource`，并重写`determineCurrentLookupKey`()方法。`spring`启动初始化`DynimaticDataSource`：

1、可以通过`spring`的自动注入，对`AbstractRoutingDataSource`类注入相应的属性，注入属性不是必须的，可以通过继承的子类重写这些方法来重新设置对这些属性的调用。

2、注入后`spring`会执行`protected`方法`afterPropertiesSet()`，该方法先判断属性`targetDataSources`不能为`null`，即必须初始化时注入至少一个数据源，否则抛出异常"`Property 'targetDataSources' is required`"。然后将该属性(类型为`map`)的值全部转换到属性`resolvedDataSources`(类型为`map`)中去。如果属性`defaultTargetDataSource`不为`null`，即已经设置默认数据源，将其转换为`DataSource`类型并赋值给属性`defaultTargetDataSource`。

经过以上处理后，属性`resolvedDataSources`中会被存放我们添加的数据源，该属性是一个`map`集合，key为`Object`类型，value为数据源。同时可能会有一个默认数据源(可以注入也可以不注入)。

使用`DynimaticDataSource`类获取连接：

1、调用该类的`public`方法`getConnection()`来获取连接。

2、`getConnection`在抽象类中被重写，会先调用`protected`方法`determineTargetDataSource()`。该方法先判断属性`resolvedDataSources`不为`null`，即初始化时候注入了至少一个数据源，否则抛出异常"`DataSource router not initialized`"。然后调用由子类重写的抽象方法`determineCurrentLookupKey()`获取`dataSource`在`resolvedDataSources`中对应的`key`，判断使用哪个数据源。

3、根据`key`从`resolvedDataSources`中获取数据源，如果`resolvedDataSources`中不存在，再判断`lenientFallback`为`true`(默认为`true`，可以设置)或`key`为`null`，返回默认数据源`resolvedDefaultDataSource`。否则抛出异常"`Cannot determine target DataSource for lookup key [" + key+ "]`"。

4、调用获取数据源的`getConnection()`方法获取连接。



在初始化时指定多数据源案例代码：

## 1、创建DynamicDataSource类

创建一个类`DynamicDataSource`，继承`AbstractRoutingDataSource`，并重写`determineCurrentLookupKey()`方法。该方法负责判断当前线程使用哪一种数据源。这是最简单的一种实现方法，不重写任何非抽象方法，但必须在初始化时配置至少一个的数据源。

```java
package com.open.capacity.datasource.util;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * spring动态数据源（需要继承AbstractRoutingDataSource）
 * @author stars
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private Map<Object, Object> datasources;

    /**
     * 指定目标数据源的映射，并将查找键作为键。
     * 映射的值可以是相应的{@linkjavax.sql.DataSource}
     * 实例或数据源名称字符串（通过
     * {@link #setDataSourceLookup datasourceookup}）。
     * <p>键可以是任意类型；此类实现
     * 仅通用查找过程。具体的关键代表将
     * 由{@link #resolveSpecifiedLookupKey（Object）}和
     * {@link #determineCurrentLookupKey（）}。
     */
    public DynamicDataSource() {
        datasources = new HashMap<>();
        super.setTargetDataSources(datasources);
    }

    /**
     * 添加数据源
     * @param key
     * @param data
     * @param <T>
     */
    public <T extends DataSource> void addDataSource(String key, T data) {
        datasources.put(key, data);
    }

    /**
     * 判断当前线程使用哪一种数据源
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceHolder.getDataSourceKey();
    }
}
```

## 2、spring配置

```java
package com.open.capacity.datasource;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.open.capacity.datasource.util.DynamicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


/**
 * 在设置了spring.datasource.enable.dynamic 等于true是开启多数据源，配合日志
 * @author stars
 */
@Configuration
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
        dataSource.addDataSource("core", coreDataSource);
        dataSource.addDataSource("log", logDataSource);
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
```

启动时的时候需要加载这个类，创建 `META-INF/spring.factories`文件，并指定类加载。

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.open.capacity.datasource.DataSourceAutoConfig
```



## 3、切换数据源

```java
package com.open.capacity.datasource.util;

/**
 * 用于数据源切换
 * @author stars
 */
public class DataSourceHolder {

    /**
     * 注意使用ThreadLocal，微服务下游建议使用信号量
     */
    private static final ThreadLocal<String> dataSourceKey = new ThreadLocal<>();

    /**
     * 得到当前的数据库连接
     * @return
     */
    public static String getDataSourceKey() {
        return dataSourceKey.get();
    }

    /**
     * 设置当前的数据库连接
     * @param type
     */
    public static void setDataSourceKey(String type) {
        dataSourceKey.set(type);
    }

    /**
     * 清除当前的数据库连接
     */
    public static void clearDataSourceKey() {
        dataSourceKey.remove();
    }
}
```

<font style="color:blue;font-weight: bold;">由于我们已经在`springboot`的配置文件中指定了属性`defaultTargetDataSource`，因此程序会默认使用该数据源`core`。然后我们执行一次后切换为`log`，之后的执行会改为使用`log`的数据源。</font>

以上方式可以对程序配置多数据源，但是缺点在程序初始化之时就指定所有的数据源，无法在运行时动态添加。

程序动态配置多数据源：

通过最上面我们对获取`DynimaticDataSource`对象的数据源连接的过程分析可知，`AbstractRoutingDataSource`类是通过`determineTargetDataSource()`方法来获取数据源。在该方法里面，又使用了我们重写的抽象方法`determineCurrentLookupKey()`来判断使用哪一个数据源的`key`，通过这个`key`，在数据源初始化后存放的集合`resolvedDataSources`中获取想要的数据源。

因此，如果我们想要动态的添加数据源进去，有两种思路可以考虑，一是重写`determineTargetDataSource()`方法，并且我们自己配置一个数据源集合，通过该方法，调用我们自己的数据源集合中对应的数据源。二是在`resolvedDataSources`属性中动态添加key-value进去，可以在`determineTargetDataSource()`方法中获取该数据源即可。

## 4、参考配置

```yaml
spring:
################## mysql start ############################    
  datasource:
    dynamic:
      enable: true
    druid: 
      core:
        # JDBC 配置(驱动类自动从url的mysql识别,数据源类型自动识别)
        url: jdbc:mysql://www.it307.top:3306/user-center?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
        username: root
        password: mcpmysql
        driver-class-name: com.mysql.cj.jdbc.Driver
      log:
        # JDBC 配置(驱动类自动从url的mysql识别,数据源类型自动识别)
        url: jdbc:mysql://www.it307.top:3306/log-center?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
        username: root
        password: mcpmysql
        driver-class-name: com.mysql.cj.jdbc.Driver  

# 开启驼峰
mybatis:
  configuration:
    mapUnderscoreToCamelCase: true
    map-underscore-to-camel-case: true
```

## 附加说明

在引入依赖的时候，`spring-boot-starter-actuator`依赖必须引入，这是要给`bean`管理的。如果没有这个依赖，在注入两个数据源会报数据源循环注入的问题。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

# 三、v2功能说明

添加 `@DataSource`注解，在需要实现的方法上添加这个注解就可以切换数据源。这里能够很好的区分调用数据源，是因为我在每个调用的方法上都指定了数据源，没有使用默认数据源的。

## 3.1、创建DataSource注解

```java
package com.open.capacity.datasource.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 数据源选择
 * @author stars
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    //数据库名称
    String name();
}
```

## 3.2、创建切面实现注解效果

```java
package com.open.capacity.datasource.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;

import com.open.capacity.datasource.annotation.DataSource;
import com.open.capacity.datasource.constant.DataSourceKey;
import com.open.capacity.datasource.util.DataSourceHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * 切换数据源Advice
 * {@code @Order(-1)}保证该AOP在@Transactional之前执行
 * @author stars
 */
@Slf4j
@Aspect
@Order(-1) // 保证该AOP在@Transactional之前执行
public class DataSourceAOP {

    @Before("@annotation(ds)")
    public void changeDataSource(JoinPoint point, DataSource ds) throws Throwable {
        String dsId = ds.name();
        try {
            DataSourceKey dataSourceKey = DataSourceKey.valueOf(dsId);
            DataSourceHolder.setDataSourceKey(dataSourceKey);
        } catch (Exception e) {
            log.error("数据源[{}]不存在，使用默认数据源 > {}", ds.name(), point.getSignature());
        }
    }

    @After("@annotation(ds)")
    public void restoreDataSource(JoinPoint point, DataSource ds) {
        log.debug("Revert DataSource : {transIdo} > {}", ds.name(), point.getSignature());
        DataSourceHolder.clearDataSourceKey();
    }
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200728131826432.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)![在这里插入图片描述](https://img-blog.csdnimg.cn/20200728131932629.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

