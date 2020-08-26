package com.hongyan.study.springboothibernatevalidator.controller;

import com.hongyan.study.springboothibernatevalidator.entity.User;
import com.hongyan.study.springboothibernatevalidator.exception.MyException;
import com.hongyan.study.springboothibernatevalidator.util.OpCode;
import com.hongyan.study.springboothibernatevalidator.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/valid")
@Slf4j
@Validated
public class ValidatorController {

    /**
     * hibernate validator校验之对象校验：需实现：
     * 1、请求参数校验 @Valid，然后后面加BindindResult即可
     * 2、实体对象中引入@NotNull等注解属性
     * @param user
     * @param result
     * @return
     */
    @PostMapping("/demo1")
    public R validDemo1(@Valid @RequestBody User user, BindingResult result){
        //由于实现了ValidHandlerAop通用异常处理，因此此处代码可剔除，通过aop统一处理
//        StringBuilder sb = new StringBuilder();
//        if(result.hasErrors()){
//            for (ObjectError error :result.getAllErrors()){
//                log.error("error:{}",error.getDefaultMessage());
//
//                FieldError fieldError = (FieldError) error;
//                log.error("fieldError:{}",fieldError.getDefaultMessage());
//                sb.append(error.getDefaultMessage()).append(",");
//            }
//        }
//        return new R(OpCode.Success,"success",sb.deleteCharAt(sb.length()-1).toString());
        return new R(OpCode.Success,"success","");
    }


    /**
     * hibernate validator校验之属性值校验：需实现：
     * get请求部分数据时需在类上增加@Validated注解，
     * 而非@Valid注解，且需实现捕获异常处理
     * @param name
     * @param idCard
     * @return
     */
    @GetMapping("/demo2")
    public R validDemo2(@NotBlank(message = "name不能为空") @RequestParam("name") String name,
                        @Min(value = 10,message = "idCard最小10位数") @NotNull(message = "idCard不能为空") @RequestParam(name="idCard", required = true) Integer idCard){
        StringBuilder sb = new StringBuilder();
        return new R(OpCode.Success,"success",null);
    }


    /***
     * 实现并捕获自定义异常以及返回输出
     * @param name
     * @param idCard
     * @return
     */
    @GetMapping("/demo3")
    public Object validDemo3(@RequestParam("name") String name,
                        Integer idCard){
        throw new MyException(OpCode.Internal,"custom exception...");
    }

}
