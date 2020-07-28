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