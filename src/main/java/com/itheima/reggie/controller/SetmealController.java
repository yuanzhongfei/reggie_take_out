package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private  SetmealService setmealService;
    private  SetmealDishService setmealDishService ;
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map(record -> {
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(record, dto);
            Long categoryId = dto.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null)
                dto.setCategoryName(category.getName());
            return dto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);

        return R.success(setmealDtoPage);
    }

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("添加套餐");
        setmealService.saveWithDish(setmealDto);
        return R.success("添加套餐成功！");
    }
    @DeleteMapping
    public  R<String> delete(@RequestParam List<Long> ids)
    {
        log.info(ids.toString());
        setmealService.removeWithDish(ids);
        return R.success("删除成功！");
    }
    @PostMapping("/status/{status}")
    public R<String> status(@RequestParam List<Long> ids,@PathVariable Integer status){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> list = setmealService.list(queryWrapper);
        for(Setmeal setmeal : list){
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return R.success("更新成功");
    }
    @GetMapping("/{id}")
    public R<SetmealDto> getData(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getData(id);
        return R.success(setmealDto);
    }
    @Transactional
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐信息成功");
    }
}
