package com.hongyan.study.springboothibernatevalidator.handle;

import com.hongyan.study.springboothibernatevalidator.util.OpCode;
import com.hongyan.study.springboothibernatevalidator.util.R;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * 创建一个捕获hibernate-validate的BindingResult通用异常处理方案
 */
@Aspect
@Component
@ControllerAdvice
@Slf4j
public class ValidHandlerAop {

    @Pointcut("execution(* com.hongyan.study.springboothibernatevalidator.controller.*.*.*(..))")
    public void handlerAop() {
    }

    @Around("handlerAop()")
    public R around(ProceedingJoinPoint pjp) {
        try {
            Object[] objects = pjp.getArgs();
            for (Object obj : objects) {
                if (obj instanceof BindingResult) {
                    BindingResult result = (BindingResult) obj;
                    StringBuilder sb = new StringBuilder();
                    if (result.hasErrors()) {
                        for (ObjectError error : result.getAllErrors()) {
                            FieldError fieldError = (FieldError) error;
                            log.error("error:{}", error.getDefaultMessage());
                            log.error("fieldError:{}", fieldError.getDefaultMessage());
                            if(sb.length() > 0){//这种写法比无脑的都新增逗号，然后剔除最后一个字段优雅，且不会出现数组下标越界的风险
                                sb.append(",");
                            }
                            sb.append(error.getDefaultMessage());
                        }
                        return new R(OpCode.InvalidArgument, sb.toString(), null);
                    }
                }
            }
            Object object = pjp.proceed(pjp.getArgs());
            return (R) object;
        }catch (ConstraintViolationException e){
            log.error("valid校验异常",e);
            StringBuilder buffer = new StringBuilder();
            for (ConstraintViolation violation : e.getConstraintViolations()) {
                if (buffer.length() > 0) {//这种写法比无脑的都新增逗号，然后剔除最后一个字段优雅，且不会出现数组下标越界的风险
                    buffer.append(",");
                }
                buffer.append(violation.getMessage());
            }
            return new R(OpCode.InvalidArgument, buffer.toString(), null);
        }catch (Throwable throwable) {
            log.error("aop解析异常",throwable);
            throwable.printStackTrace();
            return new R(OpCode.Internal,"系统内部异常",null);
        }

    }
}
