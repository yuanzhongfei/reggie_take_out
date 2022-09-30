package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;
    //RequestBody 返回json格式
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if (emp == null)
             return R.error("登录失败！");
        if (!emp.getPassword().equals(password))
            return R.error("登录失败！");
        if (emp.getStatus() == 0)
            return R.error("员工禁用");
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
    @PostMapping("/logout") //退出
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }
    @PostMapping
    public R<Employee> save(@RequestBody Employee employee,HttpServletRequest request){
        log.info("新增员工信息：{}",employee.toString());
        // 密码加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        Long id = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(id); // 自动填充
//        employee.setUpdateUser(id)  ;
        employeeService.save(employee);
        return R.success(employee);

//        if (save)
//            return R.success(employee);
//        return R.error("添加失败！");
    }
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }
   @PutMapping
    public R<String> update(@RequestBody Employee employee,HttpServletRequest request){
       Long em_id =(Long) request.getSession().getAttribute("employee");
//       employee.setUpdateUser(em_id);
//       employee.setUpdateTime(LocalDateTime.now());
       employeeService.updateById(employee);
       return R.success("更新成功！");
    }
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息：");
        Employee employee = employeeService.getById(id);
        if(employee != null)
          return R.success(employee);
        return R.error("未查到信息!");
    }

}
