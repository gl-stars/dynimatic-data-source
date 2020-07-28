package com.open.capacity.datasource.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author: stars
 * @date: 2020年 07月 28日 15:21
 **/
@Component
@ConfigurationProperties(prefix = "spring.datasource.druid.core")
public class DbProperties {

    private String driverClassName;

    private String url;

    private String username;

    private String password;

    public Map<String, Object> getProperties() {
        Map<String, Object> map = new HashMap<>(5);
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
