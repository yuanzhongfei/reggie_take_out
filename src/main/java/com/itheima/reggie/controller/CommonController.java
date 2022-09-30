package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/*
    文件上传与下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String path;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info(file.toString());
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String string = UUID.randomUUID().toString()+ suffix;
        File file1 = new File(path);
        if (!file1.exists()){
            file1.mkdirs();
        }
        try {
            file.transferTo(new File(path+string));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(string);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {

            FileInputStream inputStream = new FileInputStream(new File(path + name));
            ServletOutputStream servletOutputStream = response.getOutputStream();
            int len = 0 ;
            byte[] bytes = new byte[1024];
            while((len = inputStream.read(bytes))!=-1){
                servletOutputStream.write(bytes,0,len);
                servletOutputStream.flush();
            }
            servletOutputStream.close();
            inputStream.close();

    }






}
