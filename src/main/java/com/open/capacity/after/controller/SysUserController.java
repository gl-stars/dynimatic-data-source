package com.open.capacity.after.controller;


import com.open.capacity.after.service.SysUserService;
import com.open.capacity.datasource.util.DataSourceHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * <p>这里我并没有指定数据源，使用默认的数据源。这里的默认数据源是core，但是我执行一次log数据源之后，没有指定数据源情况下也是执行log数据源。有的时候会两个数据源之间来回切换。</p>
     */
    @GetMapping
    public Object list() {
        return sysUserService.list();
    }

    /**
     * 查询
     *
     * @return
     */
    @GetMapping("/log")
    public Object findUserCount() {
        // 调用log数据源
        DataSourceHolder.setDataSourceKey("log");
        return sysUserService.count();
    }
}
