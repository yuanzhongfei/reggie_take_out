package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
     @Autowired
     private CategoryService categoryService;
     @PostMapping
     public R<String> save(@RequestBody Category category){
          log.info("category:{}",category);
          categoryService.save(category);
          return R.success("新增分类成功！！！");
     }

     @GetMapping("/page")
     public R<Page> page(int page,int pageSize){
          Page<Category> pageInfo = new Page<>(page,pageSize);
          LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
          //添加排序条件
          queryWrapper.orderByAsc(Category::getSort) ;
          categoryService.page(pageInfo,queryWrapper);
          return R.success(pageInfo);

     }
     @DeleteMapping
     public  R<String> delete(Long id){
          log.info("删除分类：");
//          categoryService.removeById(id);
          categoryService.remove(id);
          return R.success("删除成功!");
     }
/*
  根据条件查询分类数据
 */

     @GetMapping("/list")
     public R<List<Category>> list(Category category){
          LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
          lambdaQueryWrapper.eq(category.getType()!= null,Category::getType,category.getType());
          lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
          List<Category> list = categoryService.list(lambdaQueryWrapper);
          return R.success(list);
     }




}
