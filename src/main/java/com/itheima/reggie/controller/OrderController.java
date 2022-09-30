package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrdersService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String number,String beginTime,String endTime){
        Page<Orders> pageInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(number != null,Orders::getNumber,number).
                gt(StringUtils.isNotEmpty(beginTime),Orders::getOrderTime,beginTime).
                lt(StringUtils.isNotEmpty(endTime),Orders::getOrderTime,endTime);
        ordersService.page(pageInfo,queryWrapper);
        return R.success(pageInfo) ;
    }
}
