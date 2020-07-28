package com.open.capacity.datasource.controller;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.open.capacity.datasource.util.DataSourceHolder;
import com.open.capacity.datasource.util.DynamicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {


	@Autowired
	private Environment env;
    
    @Autowired private DynamicDataSource dynamicDataSource;
	
	/**
	 * 添加数据源示例
	 * 
	 * @return
	 */
	@GetMapping("/add_data_source")
	public Object addDataSource() {
		// 构建 DataSource 属性,
		Map<String, String> map = new HashMap<>();
		map.put(DruidDataSourceFactory.PROP_DRIVERCLASSNAME,
				env.getRequiredProperty("datasource.dbx.driverClassName"));
		map.put(DruidDataSourceFactory.PROP_URL, 
				env.getRequiredProperty("datasource.dbx.url").replace("{0}", "dynamic_db2"));
		map.put(DruidDataSourceFactory.PROP_USERNAME, 
				env.getRequiredProperty("datasource.dbx.username"));
		map.put(DruidDataSourceFactory.PROP_PASSWORD, 
				env.getRequiredProperty("datasource.dbx.password"));
		map.put("database", "dynamic_db2");
		return dynamicDataSource.addDataSource(map);
	}
	
	/**
	 * 切换数据源示例
	 * 
	 * @return
	 */
	@GetMapping("/get")
	public Object get(String str) {
		return DataSourceHolder.getDataSourceKey();
	}

	@GetMapping("/index")
	public Object find(){
		DataSourceHolder.setDataSourceKey("dynamic_db2");
		return DataSourceHolder.getDataSourceKey();
	}
	
}
