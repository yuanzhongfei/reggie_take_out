package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
     dishService.saveWithFlavor(dishDto);
     return R.success("新增菜品成功");
    }
//    @Cacheable(value = "Dish_page",key = "#root.method.name")
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        // 查询redis中是否有页面数据，如果有则直接返回
//        String key = "dish_page";
//        Page<DishDto> temp = (Page<DishDto>) redisTemplate.opsForValue().get(key);
//        if (temp != null)
//        {
//            return R.success(temp);
//        }

        //分页构造器
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //增加模糊查询
        dishLambdaQueryWrapper.like(name != null, Dish::getName, name);
        //增加排序
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //调用service层实现条件分页效果
        dishService.page(dishPage, dishLambdaQueryWrapper);

        //分页对象的拷贝,其中records属性不进行拷贝,因为dishpage的erecords的泛型为dish,而dishDtoPage的泛型为dishDto
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        //创建一个dishDtoPage中的records
        List<DishDto> dishDtoList = new ArrayList<>();

        //遍历dishPage中的records集合
        for (Dish dish : dishPage.getRecords()) {
            DishDto dishDto = new DishDto();

            //将父类对象拷贝给子类对象
            BeanUtils.copyProperties(dish, dishDto);

            //根据dishDTO中的CategoryId(继承父类dish的属性)的id查询关联的实体类category(菜品分类)
            Category category = categoryService.getById(dishDto.getCategoryId());
            //获取菜品分类的name并且赋值
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //将拷贝过来的dishDto并且赋值了categoryName的对象放到dishDtoPage中的records中
            dishDtoList.add(dishDto);
        }
        //将dishDtoPage中的records赋值
        dishDtoPage.setRecords(dishDtoList);
       // 更新redis数据
//       redisTemplate.opsForValue().set("dish_page",dishDtoPage,60,TimeUnit.MINUTES);
       //
       return R.success(dishDtoPage);
    }
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdFlavor(id);
        return R.success(dishDto);
    }
    @PutMapping
    @CacheEvict(value = "dish_cache",allEntries = true)
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        // 删除redis信息
//            Set set = redisTemplate.keys("dish_*");
//            redisTemplate.delete(set);
        return R.success("新增成功");
    }
    @GetMapping("/list")
    @Cacheable(value = "dish_cache",key = "#dish.categoryId+'_'+#dish.status")
    public R<List> list(Dish dish){
        //先从redis获取缓存数据,如果存在直接返回，不需要再查询数据库
//        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
//        List<Dish> ll = (List<Dish>) redisTemplate.opsForValue().get(key);
//        if ( ll != null)
//        {
//            return R.success(ll);
//        }

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        //redis中不存在 ，保存在redis中
//        redisTemplate.opsForValue().set(key,list,60, TimeUnit.MINUTES);
        //
        return R.success(list);
    }
    @DeleteMapping
    @CacheEvict(value = "dash_cache",allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.deleteByIds(ids);
        return R.success("删除成功");
    }
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,@RequestParam List<Long> ids){
       LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
       queryWrapper.in( Dish::getId ,ids) ;
        List<Dish> list = dishService.list(queryWrapper);
        list.stream().map(dish->{
            dish.setStatus(status);
            dishService.updateById(dish);
            return dish;
        }).collect(Collectors.toList());

        return R.success("更新成功！");
    }
}
