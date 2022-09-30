package com.itheima.reggie.interception;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
public class LoginInterception  implements HandlerInterceptor {
    public  static AntPathMatcher PATH = new AntPathMatcher();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("当前url：{}",requestURI);


        if (request.getSession().getAttribute("employee") != null)
        {
            //在线程中保存id
            BaseContext.setThreadLocal((Long)request.getSession().getAttribute("employee"));
            return true;
        }
        log.info("未登录url：{}",requestURI);
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
//        response.sendRedirect("https://www.baidu.com");
        return false ;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


}
