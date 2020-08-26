package com.hongyan.study.springboothibernatevalidator.handle;

import com.hongyan.study.springboothibernatevalidator.exception.MyException;
import com.hongyan.study.springboothibernatevalidator.util.OpCode;
import com.hongyan.study.springboothibernatevalidator.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ControllerAdvice
@Component
@Slf4j
public class GlobalExceptionHandler {

    //捕获全局异常，处理所有不可知的异常
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R handle(Exception exception, HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        if(exception instanceof ConstraintViolationException){
            ConstraintViolationException exs = (ConstraintViolationException) exception;
            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
                if (sb.length() > 0) {//这种写法比无脑的都新增逗号，然后剔除最后一个字段优雅，且不会出现数组下标越界的风险
                    sb.append(",");
                }
                sb.append(item.getMessage());
            }
            return new R(OpCode.InvalidArgument,sb.toString(),request.getRequestURL());
        }
        return new R(OpCode.InvalidArgument,exception.getMessage(),request.getRequestURL());
    }

    //处理自定义异常
    @ExceptionHandler(value= MyException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleMyException(MyException e, HttpServletRequest request) {
        //返回Json数据，由前端进行界面跳转
        Map<String, Object> map = new HashMap<>();
        map.put("code", e.getCode());
        map.put("msg", e.getMsg());
        map.put("url", request.getRequestURL());
        log.error("custom exception ...{}",map);
        return map;
    }

}
