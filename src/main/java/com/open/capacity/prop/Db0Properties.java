package com.open.capacity.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix="datasource.db0")
public class Db0Properties {

	private String driverClassName = "com.mysql.jdbc.Driver";
	
	private String url = "jdbc:mysql://www.it307.top:3306/dynamic_db0?useSSL=false";;
	
	private String username = "root";
	
	private String password = "mcpmysql";

	public Map<String, Object> getProperties() {
		Map<String, Object> map = new HashMap<>();
    	map.put("driverClassName", this.getDriverClassName());
    	map.put("url", this.getUrl());
    	map.put("username", this.getUsername());
    	map.put("password", this.getPassword());
		return map;
	}
	
	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
