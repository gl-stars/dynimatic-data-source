package com.open.capacity.after.controller;


import com.open.capacity.after.service.SysUserService;
import com.open.capacity.datasource.constant.DataSourceKey;
import com.open.capacity.datasource.util.DataSourceHolder;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *     
 * </p>
 * @author stars
 * @Date: Create in ${time} 2020-07-25 19:08:54
 */
@RestController
@RequestMapping("/sysuser")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 列表
     */
    @GetMapping
    public Object list() {
        return sysUserService.list();
    }

    /**
     * 查询
     * @param id 编号
     * @return
     */
    @GetMapping("/log")
    public Object findUserById() {
        // 调用log数据源
        DataSourceHolder.setDataSourceKey(DataSourceKey.log);
        return sysUserService.count();
    }
}
