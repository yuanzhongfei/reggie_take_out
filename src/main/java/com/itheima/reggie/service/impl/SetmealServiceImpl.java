package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.CustomerException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl  extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private  SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
      this.save(setmealDto);
      List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
      setmealDishes.stream().map(setmealDish -> {
          setmealDish.setSetmealId(setmealDto.getId());
          return setmealDish ;
      }).collect(Collectors.toList());
      setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getCode,1);  ///////
        long count = this.count(queryWrapper);
        if (count > 0 )
            throw  new CustomerException("正在售卖中");
        // 可以删除
        this.removeByIds(ids);
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);
    }

    @Override
    @Transactional
    public SetmealDto getData(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        //在关联表中查询，setmealdish
        queryWrapper.eq(id!=null,SetmealDish::getSetmealId,id);

        if (setmeal != null){
            BeanUtils.copyProperties(setmeal,setmealDto);
            List<SetmealDish> list = setmealDishService.list(queryWrapper);
            setmealDto.setSetmealDishes(list);
            return setmealDto;
        }
        return null;
    }

    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
         // 保存setmeal表中的基本数据。
        this.updateById(setmealDto);
        // 先删除原来的套餐所对应的菜品数据。
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        // 更新套餐关联菜品信息。setmeal_dish表。
        // Field 'setmeal_id' doesn't have a default value] with root cause
        // 所以需要处理setmeal_id字段。
        // 先获得套餐所对应的菜品集合。
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //每一个item为SetmealDish对象。
        setmealDishes = setmealDishes.stream().map((item) -> {
            //设置setmeal_id字段。
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 重新保存套餐对应菜品数据
        setmealDishService.saveBatch(setmealDishes);
    }


}
