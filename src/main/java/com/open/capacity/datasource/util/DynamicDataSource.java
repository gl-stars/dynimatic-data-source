//package com.open.capacity.datasource.util;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.druid.pool.DruidDataSourceFactory;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 动态数据源
// *
// * <p>需要继承AbstractRoutingDataSource</p>
// * @author stars
// */
//public class DynamicDataSource extends AbstractRoutingDataSource {
//
//    private final Logger log = LoggerFactory.getLogger(getClass());
//
//    /**
//     * 保存数据源
//     */
//    private static Map<Object, Object> datasources;
//
//    /**
//     * 指定目标数据源的映射，并将查找键作为键。
//     * 映射的值可以是相应的{@linkjavax.sql.DataSource}
//     * 实例或数据源名称字符串（通过
//     * {@link #setDataSourceLookup datasourceookup}）。
//     * <p>键可以是任意类型；此类实现
//     * 仅通用查找过程。具体的关键代表将
//     * 由{@link #resolveSpecifiedLookupKey（Object）}和
//     * {@link #determineCurrentLookupKey（）}。
//     */
//    public DynamicDataSource() {
//        datasources = new HashMap<>();
//    }
//
//    /**
//     * 判断当前线程使用哪一种数据源
//     */
//    @Override
//    protected Object determineCurrentLookupKey() {
//        log.info("当前数据源是： [{}]", DataSourceHolder.getDataSourceKey());
//        return DataSourceHolder.getDataSourceKey();
//    }
//
//    @Override
//    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
//        super.setTargetDataSources(targetDataSources);
//        DynamicDataSource.datasources = targetDataSources;
//    }
//
//    /**
//     * 是否存在当前key的 DataSource
//     *
//     * @param key
//     * @return 存在返回 true, 不存在返回 false
//     */
//    public static boolean isExistDataSource(String key) {
//        return datasources.containsKey(key);
//    }
//
//    /**
//     * 动态增加数据源
//     *
//     * @param map 数据源属性
//     * @return
//     */
//    public synchronized boolean addDataSource(Map<String, String> map) {
//        try {
//            Connection connection = null;
//            // 排除连接不上的错误
//            try {
//                Class.forName(map.get(DruidDataSourceFactory.PROP_DRIVERCLASSNAME));
//                connection = DriverManager.getConnection(
//                        map.get(DruidDataSourceFactory.PROP_URL),
//                        map.get(DruidDataSourceFactory.PROP_USERNAME),
//                        map.get(DruidDataSourceFactory.PROP_PASSWORD));
//                System.out.println(connection.isClosed());
//            } catch (Exception e) {
//                return false;
//            } finally {
//                if (connection != null && !connection.isClosed()) {
//                    connection.close();
//                }
//            }
//            //获取要添加的数据库名
//            String database = map.get("database");
//            if (StringUtils.isBlank(database)) {
//                return false;
//            }
//            if (DynamicDataSource.isExistDataSource(database)){
//                return true;
//            }
//            DruidDataSource druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(map);
//            druidDataSource.init();
//            Map<Object, Object> targetMap = DynamicDataSource.datasources;
//            targetMap.put(database, druidDataSource);
//            // 当前 targetDataSources 与 父类 targetDataSources 为同一对象 所以不需要set
////			this.setTargetDataSources(targetMap);
//            this.afterPropertiesSet();
//            log.info("dataSource [{}] has been added", database);
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return false;
//        }
//        return true;
//    }
//}