package com.hongyan.study.springboothibernatevalidator.entity;

import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class User {
    @NotNull(message = "主键不能为空")
    private Integer id;
    @NotBlank(message = "名称不能为空")
    private String name;
    @AssertTrue(message = "性别必须为true")
    private Boolean sex;
    @NotBlank(message="年龄不能为空")
    @Pattern(regexp="^[0-9]{1,2}$",message="年龄不正确")
    private String age;
}
