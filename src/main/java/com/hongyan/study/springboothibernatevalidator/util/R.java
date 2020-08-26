package com.hongyan.study.springboothibernatevalidator.util;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 * @since 2020/2/20 18:18
 */
@Data
public class R<T> implements Serializable {
    private static R ar = null;
    private static final long serialVersionUID = -7442927961544597584L;

    private int code;

    private String msg;

    private double cost;


    private T data;

    public R() {
    }

    public static final R getInstance() {
        if (ar == null) {//默认返回成功,data为空
            return new R(OpCode.Success, null, new Object());
        } else {
            return ar;
        }
    }

    public R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}

