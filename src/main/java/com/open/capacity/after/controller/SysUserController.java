package com.open.capacity.after.controller;


import com.open.capacity.after.service.SysUserService;
import com.open.capacity.datasource.annotation.DataSource;
import com.open.capacity.datasource.constant.DataSourceKey;
import com.open.capacity.datasource.util.DataSourceHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        DataSourceHolder.setDataSourceKey(DataSourceKey.log);
        return sysUserService.count();
    }

    /**
     * {@code @DataSource(name = "log")} 调用数据源
     * @param id
     * @return
     */
    @DataSource(name = "log")
    @GetMapping("/{id}")
    public Object findById(@PathVariable Long id){
        return sysUserService.getById(id);
    }
}
