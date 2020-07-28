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