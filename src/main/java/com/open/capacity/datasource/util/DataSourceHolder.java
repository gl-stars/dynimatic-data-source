package com.open.capacity.datasource.util;

import com.open.capacity.datasource.DataSourceAutoConfig;

/**
 * 用于数据源切换
 * @author stars
 */
public class DataSourceHolder {

    /**
     * 注意使用ThreadLocal，微服务下游建议使用信号量
     */
    private static final ThreadLocal<String> dataSourceKey = new ThreadLocal<String>() {

        /**
         * 设置默认数据源
         * 这个值来自 {@link DataSourceAutoConfig#dataSource()}
         * @return
         */
        /*@Override
        protected String initialValue() {
            return "dynamic_db";
        }*/
    };

    /**
     * 得到当前的数据库连接
     *
     * @return
     */
    public static String getDataSourceKey() {
        return dataSourceKey.get();
    }

    /**
     * 设置当前的数据库连接
     *
     * @param type
     */
    public static void setDataSourceKey(String key) {
        dataSourceKey.set(key);
    }

    /**
     * 清除当前的数据库连接
     */
    public static void clearDataSourceKey() {
        dataSourceKey.remove();
    }
}