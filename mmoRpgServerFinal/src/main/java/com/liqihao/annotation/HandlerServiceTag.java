package com.liqihao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 李启浩
 * service模块优化
 */
//作用域
@Target(ElementType.METHOD)
//生命周期
@Retention(RetentionPolicy.RUNTIME)
public @interface HandlerServiceTag {
    short cmd();
}
