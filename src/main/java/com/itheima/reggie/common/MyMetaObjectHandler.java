package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler  implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {

        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        //获取id
        Long id = BaseContext.getThreadLocal();
        log.info("公共字段自动插入.....{} ",id);
        metaObject.setValue("createUser",id);
        metaObject.setValue("updateUser",id);


    }

    @Override
    public void updateFill(MetaObject metaObject) {

        //获取id
        Long id = BaseContext.getThreadLocal();
        log.info("公共字段自动更新.....{} ",id);
        metaObject.setValue("createUser",id);
        metaObject.setValue("updateUser",id);
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
    }

}
